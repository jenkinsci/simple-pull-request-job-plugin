pipeline {
    agent any
    stages {
        stage('Preparation') {
            steps {
                deleteDir()
                checkout scm
            }
        }

        stage('Build'){
            steps {
                sh 'mvn install'
            }
        }

        stage('Results') {
            steps {
              junit '	**/target/surefire-reports/TEST-*.xml'
              archive 'target/*.jar'
            }
       }
    }
}