pipeline {
    agent any
    
    tools {
        maven 'Maven-3.9'
        jdk 'JDK-17'
    }
    
    environment {
        SONAR_HOST_URL = 'http://sonarqube:9000'
        DOCKER_IMAGE = 'tricol-supplier-chain'
        DOCKER_TAG = "${BUILD_NUMBER}"
        // Docker Hub credentials (configure in Jenkins credentials)
        DOCKERHUB_CREDENTIALS = credentials('dockerhub-credentials')
    }
    
    options {
        buildDiscarder(logRotator(numToKeepStr: '10'))
        timestamps()
        timeout(time: 30, unit: 'MINUTES')
        disableConcurrentBuilds()
    }
    
    stages {
        // ===========================================
        // Stage 1: Checkout
        // ===========================================
        stage('Checkout') {
            steps {
                echo '📥 Checking out source code...'
                checkout scm
            }
        }
        
        // ===========================================
        // Stage 2: Build
        // ===========================================
        stage('Build') {
            steps {
                echo '🔨 Building the application...'
                sh 'mvn clean compile -DskipTests'
            }
        }
        
        // ===========================================
        // Stage 3: Unit Tests
        // ===========================================
        stage('Unit Tests') {
            steps {
                echo '🧪 Running unit tests...'
                sh 'mvn test'
            }
            post {
                always {
                    // Publish test results
                    junit allowEmptyResults: true, testResults: 'target/surefire-reports/*.xml'
                }
            }
        }
        
        // ===========================================
        // Stage 4: Code Coverage (JaCoCo)
        // ===========================================
        stage('Code Coverage') {
            steps {
                echo '📊 Generating code coverage report...'
                sh 'mvn jacoco:report'
            }
            post {
                always {
                    // Publish JaCoCo report
                    jacoco(
                        execPattern: 'target/jacoco.exec',
                        classPattern: 'target/classes',
                        sourcePattern: 'src/main/java',
                        exclusionPattern: 'src/test/*'
                    )
                }
            }
        }
        
        // ===========================================
        // Stage 5: SonarQube Analysis
        // ===========================================
        stage('SonarQube Analysis') {
            steps {
                echo '🔍 Running SonarQube analysis...'
                withSonarQubeEnv('SonarQube') {
                    sh '''
                        mvn sonar:sonar \
                            -Dsonar.projectKey=tricol-supplier-chain \
                            -Dsonar.projectName="Tricol Supplier Chain" \
                            -Dsonar.host.url=${SONAR_HOST_URL} \
                            -Dsonar.java.binaries=target/classes \
                            -Dsonar.coverage.jacoco.xmlReportPaths=target/site/jacoco/jacoco.xml
                    '''
                }
            }
        }
        
        // ===========================================
        // Stage 6: Quality Gate
        // ===========================================
        stage('Quality Gate') {
            steps {
                echo '🚦 Checking SonarQube Quality Gate...'
                timeout(time: 5, unit: 'MINUTES') {
                    waitForQualityGate abortPipeline: true
                }
            }
        }
        
        // ===========================================
        // Stage 7: Package
        // ===========================================
        stage('Package') {
            steps {
                echo '📦 Packaging the application...'
                sh 'mvn package -DskipTests'
            }
            post {
                success {
                    archiveArtifacts artifacts: 'target/*.jar', fingerprint: true
                }
            }
        }
        
        // ===========================================
        // Stage 8: Docker Build
        // ===========================================
        stage('Docker Build') {
            steps {
                echo '🐳 Building Docker image...'
                sh """
                    docker build -t ${DOCKER_IMAGE}:${DOCKER_TAG} .
                    docker tag ${DOCKER_IMAGE}:${DOCKER_TAG} ${DOCKER_IMAGE}:latest
                """
            }
        }
        
        // ===========================================
        // Stage 9: Docker Push (Optional - uncomment to enable)
        // ===========================================
        stage('Docker Push') {
            when {
                branch 'main'
            }
            steps {
                echo '🚀 Pushing Docker image to registry...'
                sh """
                    echo ${DOCKERHUB_CREDENTIALS_PSW} | docker login -u ${DOCKERHUB_CREDENTIALS_USR} --password-stdin
                    docker push ${DOCKER_IMAGE}:${DOCKER_TAG}
                    docker push ${DOCKER_IMAGE}:latest
                """
            }
        }
        
        // ===========================================
        // Stage 10: Deploy (Optional)
        // ===========================================
        stage('Deploy') {
            when {
                branch 'main'
            }
            steps {
                echo '🚀 Deploying application...'
                sh '''
                    docker-compose down app || true
                    docker-compose up -d app
                '''
            }
        }
    }
    
    post {
        always {
            echo '🧹 Cleaning up workspace...'
            cleanWs()
        }
        success {
            echo '✅ Pipeline completed successfully!'
        }
        failure {
            echo '❌ Pipeline failed!'
            // Optionally send notifications
            // mail to: 'team@example.com', subject: "Pipeline Failed: ${env.JOB_NAME}", body: "Check Jenkins for details."
        }
        unstable {
            echo '⚠️ Pipeline is unstable!'
        }
    }
}
