def STATUS = ['SUCCESS': 'good', 'FAILURE': 'danger', 'UNSTABLE': 'danger', 'ABORTED': 'danger']

pipeline {
    agent { label 'master' }
    environment {
        imageName = "aks"
        codeRepo = "https://github.com/snaruto7/terraform-aks-setup.git"
        version = VersionNumber([versionNumberString: '${BUILD_YEAR}.${BUILD_MONTH}.${BUILD_DAY}.AKS.${BUILDS_ALL_TIME}', projectStartDate: '2020-04-01'])
        branch = "master"
        imageFolder = "shivamlabs-terraform"
        targetPath = "aks/project_artifacts.${version}.zip"
    }
    stages {
        stage('SCM'){
            steps{
                script {
                    currentBuild.displayName = version
                }
                step([$class: 'WsCleanup'])
                checkout([$class: 'GitSCM', branches: [[name: "*/${branch}"]], doGenerateSubmoduleConfigurations: false, extensions: [], submoduleCfg: [], userRemoteConfigs: [[credentialsId: 'github-creds', url: "${codeRepo}"]]])
            }
        }
        stage('Update Variables in Tfvars file'){
            steps{
                script{
                    withCredentials([azureServicePrincipal("${subscription_id}")]){
                        sh '''
                            sed -ie "s|ENV|$environment|g" terraform/terraform.tfvars
                            sed -ie "s|SUBS_ID|$AZURE_SUBSCRIPTION_ID|g" terraform/terraform.tfvars
                            sed -ie "s|CLIENT_ID|$AZURE_CLIENT_ID|g" terraform/terraform.tfvars
                            sed -ie "s|CLIENT_SECRET|$AZURE_CLIENT_SECRET|g" terraform/terraform.tfvars
                            sed -ie "s|TENANT_ID|$AZURE_TENANT_ID|g" terraform/terraform.tfvars
                            sed -ie "s|RG_NAME|$rg_name|g" terraform/terraform.tfvars
                            sed -ie "s|LOCATION|$rg_location|g" terraform/terraform.tfvars
                            sed -ie "s|CLUSTER_NAME|$cluster_name|g" terraform/terraform.tfvars
                            sed -ie "s|DNS_PREFIX|$dns_prefix|g" terraform/terraform.tfvars
                            sed -ie "s|NODE_COUNT|$node_count|g" terraform/terraform.tfvars
                            sed -ie "s|VM_SIZE|$vm_size|g" terraform/terraform.tfvars
                        '''
                    }
                }
            }
        }
        stage('Add backend for terraform'){
            steps{
                script{
                    withCredentials([string(credentialsId: 'access-key', variable: 'key'), string(credentialsId: 'storage_acc_name', variable: 'storage_acc_name'), string(credentialsId: 'container_name', variable: 'container_name')]) {
                        sh '''
                            sed -ie "s|ACCESS_KEY|${key}|g" terraform/load-remote-state.tf
                            sed -ie "s|STORAGE_ACC_NAME|${storage_acc_name}|g" terraform/load-remote-state.tf
                            sed -ie "s|CONTAINER_NAME|${container_name}|g" terraform/load-remote-state.tf
                            sed -ie "s|ACCESS_KEY|${key}|g" terraform/config.tf
                            sed -ie "s|STORAGE_ACC_NAME|${storage_acc_name}|g" terraform/config.tf
                            sed -ie "s|CONTAINER_NAME|${container_name}|g" terraform/config.tf
                        '''
                    }
                }
            }
        }
        stage('Formatting TF code'){
            steps{
                dir('terraform'){
                    sh '''
                        terraform fmt -recursive
                    '''
                }
            }
        }
        stage('Store Config'){
            steps{
                sh '''
                    echo $Create > config
                '''
            }
        }
        stage('Zip the code'){
            steps{
                sh '''
                    zip -r project_artifacts.zip terraform/ -x "*.git*"
                    zip -r project_artifacts.zip config
                '''
            }
        }
        stage('Push to Jfrog'){
            steps{
                script{
                    withCredentials([usernamePassword(credentialsId: 'artifactory-jfrog', passwordVariable: 'docker_pw', usernameVariable: 'docker_user')]) {
                        sh """
                            curl -H 'X-JFrog-Art-Api:$docker_pw' -T project_artifacts.zip "https://shivamlabs.jfrog.io/artifactory/${imageFolder}/${targetPath}"
                        """
                    }
                }
            }
        }
        stage('Trigger Deploy') {
            steps {
                build job: 'ShivamLabs_Terraform_aks_Deploy', parameters: [[$class: 'StringParameterValue', name: 'version', value: version]]
            }
        }
    }
    post{
        always{
            script {
                BUILD_USER = buildUser()
            }
            slackSend channel: '#apps-dsl-build',
            message: "*${currentBuild.currentResult}:* Job ${env.JOB_NAME} build ${env.BUILD_NUMBER} by ${BUILD_USER}\n More info at: ${env.BUILD_URL}" 
            
        }
    }
}