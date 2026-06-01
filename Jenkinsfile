pipeline {
  agent any

  environment {
    IMAGE_NAME = 'bug-tracker-backend'
    CONTAINER_NAME = 'bug-tracker-backend'
    DOCKER_NETWORK = 'updated_orgadmin_rmscadminnetwork'
    MYSQL_HOST = 'erp-mysql'
    MYSQL_SCHEMA = 'bug_tracker'
    MYSQL_USER = 'root'
    MYSQL_PASSWORD = 'root'
    APP_CORS_ALLOWED_ORIGINS = 'http://localhost:3000,http://127.0.0.1:3000,http://*:3000,https://*.sarvosmi.io'
  }

  stages {
    stage('Checkout') {
      steps {
        checkout scm
      }
    }

    stage('Docker Build') {
      steps {
        sh 'docker build --no-cache -t $IMAGE_NAME:latest .'
      }
    }

    stage('Deploy') {
      steps {
        sh 'docker-compose down || true'
        sh 'docker rm -f $CONTAINER_NAME || true'
        sh 'docker-compose up -d'
      }
    }
  }

  post {
    always {
      cleanWs()
    }
    success {
      echo 'Backend deployment successful!'
    }
    failure {
      echo 'Backend deployment failed. Please check the logs.'
    }
  }
}
