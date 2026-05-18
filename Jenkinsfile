pipeline {
    agent any
    environment {
        
        
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
