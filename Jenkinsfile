pipeline {
    agent any

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }
        stage('Test') {
            steps {
                script {
                    if (isUnix()) {
                        sh 'mvn -B test'
                    } else {
                        bat '''
                            set "PATH=C:\\Program Files\\maven\\bin;%PATH%"
                            mvn -B test
                        '''
                    }
                }
            }
        }
        stage('Build Package') {
            steps {
                script {
                    if (isUnix()) {
                        sh 'mvn -B package -DskipTests'
                    } else {
                        bat '''
                            set "PATH=C:\\Program Files\\maven\\bin;%PATH%"
                            mvn -B package -DskipTests
                        '''
                    }
                }
            }
        }
    }

    post {
        success {
            archiveArtifacts artifacts: 'target/*.jar', fingerprint: true
            echo 'Maven build and tests completed successfully!'
        }
    }
}
