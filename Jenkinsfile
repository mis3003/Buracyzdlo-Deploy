pipeline {
    agent any
    environment {
       
        DOCKER_HUB_CREDS_ID = 'Dockerhub_crudentials'
        
        
        DOCKER_CREDS = credentials("${DOCKER_HUB_CREDS_ID}")
    }

    stages {
        stage('Check Docker Login') {
            steps {
                script {
                    docker.withRegistry('https://index.docker.io/v1/', env.DOCKER_HUB_CREDS_ID) {
                       
                        sh "docker info | grep Username"
                    }
                }
            }
        }

        stage('Build backend') {
            steps {
                script {
                    dir('Buraczyd-o-Backend') {
                        docker.withRegistry('https://index.docker.io/v1/', env.DOCKER_HUB_CREDS_ID) {
                            
                            
                            def imageName = "${env.DOCKER_CREDS_USR}/buraczydlo-backend"
                            
                            echo "Buduję obraz dla użytkownika: ${env.DOCKER_CREDS_USR}"
                            
                            def customImage = docker.build("${imageName}:${env.BUILD_ID}")
                            customImage.push()          
                            customImage.push('latest')  
                        }
                    }
                }
            }
        }
    }
}