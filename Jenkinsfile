// Jenkins pipeline for building and pushing Teedy Docker image
pipeline {
  agent any

  environment {
    // Set this to your Docker Hub repository (override in Jenkins job if needed)
    DOCKER_REPO = "<your-dockerhub-username>/teedy"
    // Credentials ID in Jenkins (create a Username with password credential)
    DOCKER_CREDENTIALS_ID = 'dockerhub_credentials'
  }

  stages {
    stage('Checkout') {
      steps {
        checkout scm
      }
    }

    stage('Build Docker image') {
      steps {
        sh 'docker --version || true'
        sh 'docker build -t ${DOCKER_REPO}:${BUILD_NUMBER} .'
      }
    }

    stage('Push to Docker Hub') {
      steps {
        withCredentials([usernamePassword(credentialsId: DOCKER_CREDENTIALS_ID, usernameVariable: 'DH_USER', passwordVariable: 'DH_PASS')]) {
          sh 'echo $DH_PASS | docker login -u $DH_USER --password-stdin'
          sh 'docker push ${DOCKER_REPO}:${BUILD_NUMBER}'
        }
      }
    }
  }

  post {
    always {
      // keep last image tag reference available in the build logs
      echo "Built image: ${DOCKER_REPO}:${BUILD_NUMBER}"
    }
    success {
      echo 'Docker image pushed successfully.'
    }
    failure {
      echo 'Build or push failed. Check the logs.'
    }
  }
}

// Usage notes:
// 1) In Jenkins, create a credential (kind: Username with password) and give it the ID
//    'dockerhub_credentials' (or change DOCKER_CREDENTIALS_ID above).
// 2) Ensure the Jenkins agent has Docker installed and permissions to run Docker commands.
// 3) Optionally parameterize DOCKER_REPO or branch filters per your CI policy.
pipeline {
    agent any
    stages {
        stage('Clean') {
            steps {
                sh 'mvn clean'
            }
        }
        stage('Compile') {
            steps {
                sh 'mvn compile'
            }
        }
        stage('Test') {
            steps {
                sh 'mvn test -Dmaven.test.failure.ignore=true'
            }
        }
        stage('Install') {
            steps {
                sh 'mvn install -DskipTests'
            }
        }
        stage('PMD') {
            steps {
                sh 'mvn pmd:pmd'
            }
        }
        stage('JaCoCo') {
            steps {
                sh 'mvn jacoco:report'
            }
        }
        // stage('Javadoc') {
        //     steps {
        //         sh 'mvn javadoc:javadoc'
        //     }
        // }
        stage('Site') {
            steps {
                sh 'mvn site'
            }
        }
        stage('Package') {
            steps {
                sh 'mvn package -DskipTests'
            }
        }
    }
    post {
        always {
            archiveArtifacts artifacts: '**/target/site/**/*.*', fingerprint: true
            archiveArtifacts artifacts: '**/target/**/*.jar', fingerprint: true
            archiveArtifacts artifacts: '**/target/**/*.war', fingerprint: true
            junit '**/target/surefire-reports/*.xml'
        }
    }
}