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
    }
}
