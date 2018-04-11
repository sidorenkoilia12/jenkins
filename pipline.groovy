pipeline {
    agent any

    stages {
        stage('cloning') {
            steps {
                git credentialsId: '85f3a979-5fb5-4463-b373-cb8e40c3c4be', url: 'https://github.com/sidorenkoilia12/getting-started-java.git'

            }
        }
        stage('clean install') {
            steps {
                sh "cd helloworld-springboot && mvn clean install"
            }
        }
        stage('deleting') {
            steps {
                sh "sudo rm /opt/Spring/*.jar"
            }
        }
        stage('copying') {
            steps {
                sh "sudo cp ${WORKSPACE}/helloworld-springboot/target/*.jar /opt/Spring/"
            }
        }
        stage('docker build') {
            steps {
                //sh "echo spring:v${BUILD_NUMBER}"
                sh "sudo docker build -t spring:v${BUILD_NUMBER} /opt/Spring/"
            }
        }
        stage('docker check') {
            steps {
                sh '''if sudo docker ps | grep spring;
                      then
                      sudo docker kill $(sudo docker ps | grep spring | awk '{print $1}')
                      fi'''
            }
        }
        stage('docker run') {
            steps {
                sh "sudo docker run -d -p 8081:8081 spring:v${BUILD_NUMBER}"
            }
        }
    }
}
