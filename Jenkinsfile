// Jenkins pipeline for building and pushing Teedy Docker image (customized)
pipeline {
  agent any

  environment {
    // Jenkins credentials ID for Docker Hub (must exist in Jenkins)
    DOCKER_HUB_CREDENTIALS = 'dockerhub_credentials'
    // Use your Docker Hub repo (updated for your account)
    DOCKER_IMAGE = 'yangyichong/teedy'
    DOCKER_TAG = "${env.BUILD_NUMBER}"
  }

  stages {
    stage('Checkout') {
      steps {
        // checkout from your GitHub repository
        checkout([$class: 'GitSCM', branches: [[name: '*/master']], userRemoteConfigs: [[url: 'https://github.com/YANGYichong/Teedy.git']]])
      }
    }

    stage('Build') {
      steps {
        // build the project (requires Maven on the agent) - adapt to your agent if needed
        sh 'mvn -B -DskipTests clean package'
      }
    }

    stage('Building image') {
      steps {
        script {
          docker.build("${env.DOCKER_IMAGE}:${env.DOCKER_TAG}")
        }
      }
    }

    stage('Upload image') {
      steps {
        script {
          docker.withRegistry('https://registry.hub.docker.com', DOCKER_HUB_CREDENTIALS) {
            docker.image("${env.DOCKER_IMAGE}:${env.DOCKER_TAG}").push()
            // optional: tag as latest
            docker.image("${env.DOCKER_IMAGE}:${env.DOCKER_TAG}").push('latest')
          }
        }
      }
    }

    stage('Run containers') {
      steps {
        script {
          sh 'docker stop teedy-container-8081 || true'
          sh 'docker rm teedy-container-8081 || true'
          docker.image("${env.DOCKER_IMAGE}:${env.DOCKER_TAG}").run('--name teedy-container-8081 -d -p 8081:8080')
          sh 'docker ps --filter "name=teedy-container"'
        }
      }
    }
  }

  post {
    always {
      echo "Built image: ${env.DOCKER_IMAGE}:${env.DOCKER_TAG}"
    }
    success {
      echo 'Docker image pushed and container started.'
    }
    failure {
      echo 'Build or push failed. Check the logs.'
    }
  }
}

// Notes:
// - This Jenkinsfile assumes the agent can run shell commands and has Docker & Maven installed.
// - If your Jenkins agent is Windows-only, replace 'sh' with 'bat' where appropriate, or run the job on a linux-labeled agent.
// - You can override DOCKER_IMAGE via job parameters or environment variables in Jenkins.
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