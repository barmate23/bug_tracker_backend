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
    APP_CORS_ALLOWED_ORIGINS = 'http://localhost:3000,http://127.0.0.1:3000,http://*:3000'
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
        sh '''
          docker network inspect "$DOCKER_NETWORK" >/dev/null
          docker rm -f "$CONTAINER_NAME" 2>/dev/null || true
          docker run -d \
            --name "$CONTAINER_NAME" \
            --restart unless-stopped \
            --network "$DOCKER_NETWORK" \
            -p 8080:8080 \
            -e SPRING_DATASOURCE_URL="jdbc:mysql://$MYSQL_HOST:3306/$MYSQL_SCHEMA?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true" \
            -e SPRING_DATASOURCE_DRIVER="com.mysql.cj.jdbc.Driver" \
            -e SPRING_DATASOURCE_USERNAME="$MYSQL_USER" \
            -e SPRING_DATASOURCE_PASSWORD="$MYSQL_PASSWORD" \
            -e SPRING_JPA_HIBERNATE_DDL_AUTO="update" \
            -e APP_CORS_ALLOWED_ORIGINS="$APP_CORS_ALLOWED_ORIGINS" \
            "$IMAGE_NAME:latest"
        '''
      }
    }
  }

  post {
    always {
      sh 'docker image ls $IMAGE_NAME:latest || true'
    }
  }
}
