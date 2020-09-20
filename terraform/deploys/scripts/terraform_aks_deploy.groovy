def STATUS = ['SUCCESS': 'good', 'FAILURE': 'danger', 'UNSTABLE': 'danger', 'ABORTED': 'danger']

pipeline {
    agent { label 'master' }
    environment {
        imageName = "aks"
        targetPath = "${imageName}/project_artifacts.${version}.zip"
        sourceURl = "https://shivamlabs.jfrog.io/artifactory/${imageFolder}/${targetPath}"
    }
    stages {
        stage('Pull From Jfrog'){
            steps{
                script{
                    withCredentials([usernamePassword(credentialsId: 'artifactory-jfrog', passwordVariable: 'docker_pw', usernameVariable: 'docker_user')]) {
                        sh """
                            curl -H 'X-JFrog-Art-Api:$docker_pw' -X GET -G $sourceURL -o project_artifacts.zip
                        """
                    }
                }
            }
        }
        stage('Unzip files'){
            steps {
                sh '''
                    unzip project_artifacts.zip
                '''
            }
        }
        stage('Initialize Terraform'){
            steps{
                dir('terraform'){
                    sh 'terraform init'
                }
            }
        }
        stage('Plan Infra'){
            steps {
                dir('terraform'){
                    sh 'terraform plan'
                }
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