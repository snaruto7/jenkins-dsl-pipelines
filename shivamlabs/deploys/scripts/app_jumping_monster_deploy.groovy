def STATUS = ['SUCCESS': 'good', 'FAILURE': 'danger', 'UNSTABLE': 'danger', 'ABORTED': 'danger']

pipeline {
    agent { label 'master' }
    environment {
        imageName = "jumping-monster"
        codeRepo = "https://github.com/snaruto7/jumping-monster.git"
        branch = "master"
        registrySecret = "docker-secret"
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
        stage('Get Kubernetes Details'){
            environment{
                VAULT_ADDR = "http://40.70.214.80:8200"
            }
            steps{
                withCredentials([string(credentialsId: "${VAULT_ROLE_ID}", variable: "ROLE_ID"), string(credentialsId: "${VAULT_SEC_ID}", variable: "SEC_ID")]) {
                    sh '''
                        set +x
                        export VAULT_TOKEN=$(vault write -field=token auth/approle/login role_id=${ROLE_ID} secret_id=${SEC_ID})
                        vault kv get -field=kube-config secret/Aks-Deployment > kube-config
                        vault kv get -field=USERNAME secret/ACR-Creds > username.json
                        vault kv get -field=PASSWORD secret/ACR-Creds > password.json
                    '''
                }
            }
        }
        stage('Create Registry Secret'){
            steps{
                sh '''
                    set +x
                    if kubectl --kubeconfig=${WORKSPACE}/kube-config describe secret ${registrySecret}
                    then
                    kubectl --kubeconfig=${WORKSPACE}/kube-config delete secret ${registrySecret}
                    fi
                    kubectl --kubeconfig=${WORKSPACE}/kube-config create secret docker-registry ${registrySecret} --docker-server=$registry --docker-username=$(cat username.json) --docker-password=$(cat password.json) --docker-email=shivam.snaruto7@gmail.com
                '''
            }
        }
        stage('Deploy Application'){
            steps{
                sh """
                    sed -ie 's/#{REGISTRY_URL}#\\/#{IMAGE_FOLDER}#\\/#{IMAGE_NAME}#:#{BUILD_ID}#/$registry\\/$imageFolder\\/$imageName:$version/g' kubernetes/deployment.yaml

                    kubectl --kubeconfig=${WORKSPACE}/kube-config apply -f  kubernetes/deployment.yaml

                    kubectl --kubeconfig=${WORKSPACE}/kube-config apply -f kubernetes/service.yaml

                    kubectl --kubeconfig=${WORKSPACE}/kube-config rollout restart deployment/$imageName

                    kubectl --kubeconfig=${WORKSPACE}/kube-config rollout status deployment/$imageName
                """
            }
        }
    }
    post{
        always{
            script {
                BUILD_USER = buildUser()
            }
            slackSend channel: '#apps-dsl-deploy', 
            message: "*${currentBuild.currentResult}:* Job ${env.JOB_NAME} build ${env.BUILD_NUMBER} by ${BUILD_USER}\n More info at: ${env.BUILD_URL}" 
            
        }
    }
}