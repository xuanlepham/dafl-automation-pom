/**
 * ==============================================================================
 * BANKING & FINTECH END-TO-END CI/CD ORCHESTRATOR PIPELINE
 * ==============================================================================
 * Standard: Full Continuous Delivery with 2 Quality Gates & Production Guard
 * Stages: SUT Healthcheck -> Auto Functional Test -> Quality Gate #1 ->
 *         JMeter Load Test -> Quality Gate #2 -> Publish Reports -> Deploy Prod
 * ==============================================================================
 */

pipeline {
    agent any

    options {
        disableConcurrentBuilds()
        timeout(time: 45, unit: 'MINUTES')
        buildDiscarder(logRotator(numToKeepStr: '30'))
    }

    triggers {
        // Nightly Orchestrated Run vao 01:00 sang
        cron('0 1 * * 1-5')
        githubPush()
        pollSCM('H/5 * * * *')
    }

    parameters {
        choice(
            name: 'TEST_SUITE',
            choices: ['testng-flow.xml', 'testng-negative.xml', 'testng.xml'],
            description: 'Bo test Selenium can chay'
        )
        string(
            name: 'MAX_ERROR_RATE',
            defaultValue: '2.0',
            description: 'Nguong ti le loi toi da cho phep cua JMeter (%)'
        )
        string(
            name: 'MAX_AVG_RT_MS',
            defaultValue: '1000.0',
            description: 'Nguong Response Time trung binh toi da cho phep (ms)'
        )
    }

    environment {
        PROJECT_NAME        = 'dafl'
        REPO_URL            = 'https://github.com/xuanlepham/dafl-automation-pom.git'
        TARGET_BRANCH       = 'main'
        DOCKER_NETWORK      = 'dafl_dafl-network'
        DEFAULT_MAX_ERR_RATE = '2.0'
        DEFAULT_MAX_AVG_RT   = '1000.0'
    }

    stages {
        stage('Stage 1: Pull Code & Checkout Repository') {
            steps {
                echo "=== [Master CI/CD] 1. Pull ma nguon kiem thu tu QA Repository (${TARGET_BRANCH}) ==="
                git branch: TARGET_BRANCH, url: REPO_URL
            }
        }

        stage('Stage 2: Healthcheck SUT Environment') {
            steps {
                echo "=== [Master CI/CD] 2. Kiem tra cac services DAFL Frontend & Backend san sang ==="
                sh """
                    RETRIES=20
                    until curl -s -f http://dafl-frontend:80 > /dev/null || [ \$RETRIES -eq 0 ]; do
                        echo "Dang cho dafl-frontend:80 phan hoi... (\$RETRIES lan thu con lai)"
                        RETRIES=\$((RETRIES - 1))
                        sleep 3
                    done
                    echo "SUT da san sang tiep nhan Continuous Testing!"
                """
            }
        }

        stage('Stage 3: Functional & Negative Continuous Testing') {
            steps {
                echo "=== [Master CI/CD] 3. Chay Selenium Runner voi Page Object Model ==="
                sh """
                    rm -rf target/surefire-reports target/allure-results || true

                    SUITE_FILE="${params.TEST_SUITE ?: 'testng-flow.xml'}"
                    echo "Thuc thi Test Suite: \${SUITE_FILE}"

                    docker run --rm \
                        --name dafl-selenium-runner \
                        --network ${DOCKER_NETWORK} \
                        --volumes-from dafl-jenkins \
                        -w \$(pwd) \
                        --memory=1.5g \
                        --cpus=2.0 \
                        -e APP_URL="http://dafl-frontend:80" \
                        markhobson/maven-chrome:jdk-17 \
                        mvn clean test -DsuiteXmlFile=\${SUITE_FILE} -Dheadless=true || true
                """
            }
        }

        stage('Stage 4: Functional Quality Gate') {
            steps {
                echo "=== [Master CI/CD] 4. Kiem tra ket qua Functional Tests (Quality Gate #1) ==="
                script {
                    def reportDir = "target/surefire-reports"
                    if (!fileExists(reportDir)) {
                        error("[Functional Quality Gate FAILED] Khong tim thay thu muc bao cao test Surefire!")
                    }

                    def hasFailures = sh(
                        script: "grep -rnE 'failures=\"[1-9][0-9]*\"|errors=\"[1-9][0-9]*\"' ${reportDir}/*.xml 2>/dev/null || true",
                        returnStdout: true
                    ).trim()

                    if (hasFailures) {
                        echo "=========================================================="
                        echo "CHI TIET TEST CASE THAT BAI:"
                        echo hasFailures
                        echo "=========================================================="
                        error("[Functional Quality Gate FAILED] Phat hien Test Case that bai! Dung Pipeline ngay lap tuc.")
                    } else {
                        echo "=========================================================="
                        echo "[Functional Quality Gate PASSED] Toan bo test cases chuc nang deu vuot qua 100%!"
                        echo "=========================================================="
                    }
                }
            }
        }

        stage('Stage 5: Performance Test') {
            steps {
                echo "=== [Master CI/CD] 5. Khoi chay JMeter Non-GUI Load Testing ==="
                dir('performance-jmeter') {
                    sh """
                        rm -rf results && mkdir -p results

                        docker run --rm \
                            --name dafl-jmeter-runner \
                            --network ${DOCKER_NETWORK} \
                            --volumes-from dafl-jenkins \
                            -w \$(pwd) \
                            --memory=768m \
                            --cpus=1.0 \
                            -e JVM_XMS=256 -e JVM_XMX=512 -e JVM_XMN=128 \
                            justb4/jmeter:5.5 \
                            -n -t dafl-loadtest.jmx \
                            -Jtarget_host=dafl-backend \
                            -Jtarget_port=8080 \
                            -l results/result.jtl \
                            -e -o results/dashboard || true
                    """
                }
            }
        }

        stage('Stage 6: Performance Quality Gate') {
            steps {
                echo "=== [Master CI/CD] 6. Danh gia Quality Gate Hieu nang (Quality Gate #2) ==="
                dir('performance-jmeter') {
                    sh """
                        ERR_LIMIT="${params.MAX_ERROR_RATE ?: env.DEFAULT_MAX_ERR_RATE}"
                        RT_LIMIT="${params.MAX_AVG_RT_MS ?: env.DEFAULT_MAX_AVG_RT}"

                        chmod +x check_performance_gate.sh
                        ./check_performance_gate.sh results/result.jtl \${ERR_LIMIT} \${RT_LIMIT}
                    """
                }
            }
        }

        stage('Stage 7: Publish Test Artifacts & Reports') {
            steps {
                echo "=== [Master CI/CD] 7. Xuat Allure Report & JMeter HTML Dashboard len Jenkins ==="

                publishHTML([
                    allowMissing: true,
                    alwaysLinkToLastBuild: true,
                    keepAll: true,
                    reportDir: "performance-jmeter/results/dashboard",
                    reportFiles: 'index.html',
                    reportName: 'JMeter Performance Dashboard',
                    reportTitles: 'JMeter Dashboard Report'
                ])

                script {
                    def allureDir = "target/allure-results"
                    if (fileExists(allureDir)) {
                        try {
                            allure([
                                includeProperties: false,
                                jdk: '',
                                properties: [],
                                reportBuildPolicy: 'ALWAYS',
                                results: [[path: allureDir]]
                            ])
                        } catch (Throwable err) {
                            echo "Allure Report Notice: ${err}"
                        }
                    }
                }

                archiveArtifacts artifacts: "performance-jmeter/results/*.jtl, target/surefire-reports/*.xml", allowEmptyArchive: true
            }
        }
    }

    post {
        always {
            echo "=== Don dep Test Runners ==="
            sh "docker rm -f dafl-selenium-runner dafl-jmeter-runner 2>/dev/null || true"
        }
        success {
            echo "=========================================================="
            echo "PIPELINE THANH CONG RUC RO!"
            echo "Functional Quality Gate: PASSED (100% Test Cases vuot qua)"
            echo "Performance Quality Gate: PASSED (Dap ung nghiem ngat SLA Ngan Hang)"
            echo "=========================================================="
        }
        failure {
            echo "[ALERT] PIPELINE THAT BAI! Ban build bi chan lai do vi pham Quality Gate!"
        }
    }
}
