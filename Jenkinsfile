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
    DOCKER_BUILDKIT = '1'
    SPRING_DATASOURCE_URL = 'jdbc:mysql://erp-mysql:3306/bug_tracker?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true'
  }

  stages {
    stage('Checkout') {
      steps {
        checkout scm
      }
    }

    stage('Docker Build') {
      steps {
        sh 'docker build -t $IMAGE_NAME:latest .'
      }
    }

    stage('Deploy') {
      steps {
        sh 'docker rm -f $CONTAINER_NAME || true'
        sh '''
          docker run -d \
            --name $CONTAINER_NAME \
            --restart unless-stopped \
            --network $DOCKER_NETWORK \
            --network-alias $CONTAINER_NAME \
            -p 9099:8080 \
            -e SPRING_DATASOURCE_URL="$SPRING_DATASOURCE_URL" \
            -e SPRING_DATASOURCE_DRIVER=com.mysql.cj.jdbc.Driver \
            -e SPRING_DATASOURCE_USERNAME="$MYSQL_USER" \
            -e SPRING_DATASOURCE_PASSWORD="$MYSQL_PASSWORD" \
            -e SPRING_JPA_HIBERNATE_DDL_AUTO=update \
            -e APP_CORS_ALLOWED_ORIGINS="$APP_CORS_ALLOWED_ORIGINS" \
            -e SERVER_SERVLET_SESSION_COOKIE_SAME_SITE=none \
            -e SERVER_SERVLET_SESSION_COOKIE_SECURE=true \
            $IMAGE_NAME:latest
        '''
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
