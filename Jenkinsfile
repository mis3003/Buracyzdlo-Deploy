pipeline {
    agent any
    environment {
        DOCKER_HUB_CREDS = credentials('Dockerhub_crudentials')
        
    }


    stages {

       stage('Check Docker Login') {
    steps {
        script {
            // Używamy Twoich poświadczeń
            docker.withRegistry('https://index.docker.io/v1/', 'docker-hub-credentials') {
                // Ta komenda wypisze nazwę użytkownika w logach Jenkinsa
                sh "docker info | grep Username"
            }
        }
    }
}

}
}
