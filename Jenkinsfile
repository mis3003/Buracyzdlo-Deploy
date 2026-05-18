pipeline {
    agent any
    environment {
        // Zmienna przechowuje ID poświadczeń
        DOCKER_HUB_CREDS_ID = 'Dockerhub_crudentials'
    }

    stages {
        stage('Check Docker Login') {
            steps {
                script {
                    // Używamy zmiennej bezpośrednio (bez cudzysłowu i bez $)
                    // lub jako env.DOCKER_HUB_CREDS_ID
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
                    
                   
                    def imageName = "${env.DOCKER_HUB_CREDS_USR}/buraczyd-backend"
                    def customImage = docker.build("${imageName}:${env.BUILD_ID}")
                    
                   
                    customImage.push()          
                    customImage.push('latest')  
                }
            }
        }
    }
}
