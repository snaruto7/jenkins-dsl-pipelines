def STATUS = ['SUCCESS': 'good', 'FAILURE': 'danger', 'UNSTABLE': 'danger', 'ABORTED': 'danger']

pipeline {
    agent { label 'master' }
    environment {
        imageFolder = "shivamlabs/pullrequests"
        imageName = "tic-tac-toe"
        codeRepo = "https://github.com/snaruto7/tic-tac-toe.git"
        version = VersionNumber([versionNumberString: '${BUILD_YEAR}.${BUILD_MONTH}.${BUILD_DAY}.TIC-TAC-TOE.${BUILDS_ALL_TIME}', projectStartDate: '2020-04-01'])
    }
    stages {
        stage('SCM'){
            steps{
                script {
                    currentBuild.displayName = version
                }
                step([$class: 'WsCleanup'])
                checkout([$class: 'GitSCM', branches: [[name: "${ghprbActualCommit}"]], doGenerateSubmoduleConfigurations: false, extensions: [], submoduleCfg: [], userRemoteConfigs: [[credentialsId: 'github-creds', url: "${codeRepo}"]]])
            }
        }

        stage('Docker build'){
            steps{
                sh '''
                    docker build -f ./docker/Dockerfile -t $imageName:$version .
                '''
            }
        }
        stage('Get Registry Details'){
            environment{
                VAULT_ADDR = "http://40.70.214.80:8200"
            }
            steps{
                withCredentials([string(credentialsId: "VAULT_ROLE_ID", variable: "ROLE_ID"), string(credentialsId: "VAULT_SEC_ID", variable: "SEC_ID")]) {
                    sh '''
                        set +x
                        export VAULT_TOKEN=$(vault write -field=token auth/approle/login role_id=${ROLE_ID} secret_id=${SEC_ID})
                        vault kv get -field=USERNAME secret/ACR-Creds > username.json
                        vault kv get -field=PASSWORD secret/ACR-Creds > password.json
                    '''
                }
            }
        }
        stage('Docker Push'){
            steps{
                sh '''
                    set +x
                    docker login $registry -u $(cat username.json) --password $(cat password.json)

                    docker tag $imageName:$version $registry/$imageFolder/$imageName:$version
                    docker push $registry/$imageFolder/$imageName:$version
                    docker rmi -f $registry/$imageFolder/$imageName:$version
                '''
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