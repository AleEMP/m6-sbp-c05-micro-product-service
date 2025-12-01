pipeline {
    agent any

    tools {
        dockerTool 'my-docker-tool'
    }

    environment {
        DOCKER_IMAGE = 'aleemp/m6-sbp-c05-micro-product-service'
        DOCKER_TAG = 'latest'
        DOCKER_CREDS_ID = 'docker-hub-credentials'
    }

    stages {
        stage('Preparation') {
            steps {
                sh 'chmod +x mvnw'
            }
        }

        stage('Build & Test') {
            steps {
                sh './mvnw clean package'
            }
        }

        stage('Build Docker Image') {
            steps {
                sh "docker build -t ${DOCKER_IMAGE}:${DOCKER_TAG} ."
            }
        }

        stage('Push to Docker Hub') {
            steps {
                script {
                    withCredentials([usernamePassword(credentialsId: DOCKER_CREDS_ID, usernameVariable: 'DOCKER_USER', passwordVariable: 'DOCKER_PASS')]) {
                        sh "docker login -u ${DOCKER_USER} -p ${DOCKER_PASS}"
                        sh "docker push ${DOCKER_IMAGE}:${DOCKER_TAG}"
                        sh 'docker logout'
                    }
                }
            }
        }
    }

    post {
        always {
            sh "docker rmi ${DOCKER_IMAGE}:${DOCKER_TAG} || true"
        }
    }
}