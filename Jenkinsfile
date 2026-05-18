pipeline {
    agent any

    stages {
        stage('Build Java') {

            agent{
                docker{
                    image 'maven:3.9-eclipse-temurin-17'
                     reuseNode true
                }
            }
            steps {
                dir('Buraczyd-o-Backend') {
            sh 'mvn clean package -DskipTests'
        }
             archiveArtifacts artifacts: 'Buraczyd-o-Backend/target/*.jar', fingerprint: true
            }
        }
    }
}
