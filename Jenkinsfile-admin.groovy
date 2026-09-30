pipeline {
    agent any

    environment {
        // 写死 Git 仓库信息
        GIT_REPO_URL = 'https://github.com/2837642911/mall-learning-java.git'
        GIT_BRANCH   = 'master' // 如果你的主分支是 master，请修改为 master
        
        // 部署相关镜像名称定义
        ADMIN_IMAGE  = 'mall/mall-admin:latest'
        PORTAL_IMAGE = 'mall/mall-portal:latest'
    }

    tools {
        // 需在 Jenkins "全局工具配置" (Global Tool Configuration) 中配置好对应的名称
        maven 'maven3.8.8'
        jdk 'jdk17'
    }

    stages {
        stage('1. 写死拉取代码 (Checkout)') {
            steps {
                echo "=== 开始从固定仓库拉取代码: ${env.GIT_REPO_URL} (分支: ${env.GIT_BRANCH}) ==="
                // 写死分支与 Git 仓库地址
                git branch: "${env.GIT_BRANCH}", 
                    url: "${env.GIT_REPO_URL}"
                    // 如果是私有仓库，需要在此加上 credentialsId: '你的凭据ID'
            }
        }

        stage('2. Maven 纯编译打包 (Build Jar)') {
            steps {
                echo '=== 阶段：执行 Maven 打包，仅生成 Jar 包 ==='
                // -DskipTests 跳过单元测试
                sh 'mvn clean package -DskipTests'
            }
        }

        stage('3. Docker 独立构建镜像 (Build Docker Images)') {
            steps {
                echo '=== 阶段：读取各子模块的 Dockerfile 构建 Docker 镜像 ==='
                
                // 1. 构建 mall-admin 镜像：
                //    -f 指定位于 document/sh/admin/ 下的 Dockerfile
                //    末尾上下文设为项目根目录 . 以便拿到 mall-admin/target/*.jar
                sh "docker build -t ${env.ADMIN_IMAGE} -f document/sh/admin/Dockerfile ."
                
                // 2. 构建 mall-portal 镜像：
                sh "docker build -t ${env.PORTAL_IMAGE} -f document/sh/portal/Dockerfile ."
                
                // 如果需要构建 mall-search，取消下方注释
                // sh "docker build -t ${env.SEARCH_IMAGE} -f document/sh/search/Dockerfile ."
            }
        }

        stage('4. Docker Compose 部署 (Deploy)') {
            steps {
                echo '=== 阶段：启动/重新部署服务 ==='
                
                // 1. 启动基础设施环境（MySQL, Redis, RabbitMQ 等）
                sh 'docker compose -f document/docker/docker-compose-env.yml up -d'
                
                // 2. 重启并部署应用服务（mall-admin, mall-portal 等）
                sh 'docker compose -f document/docker/docker-compose-app.yml up -d --force-recreate'
            }
        }

        stage('5. 清理虚悬镜像 (Clean Cache)') {
            steps {
                echo '=== 阶段：清理未使用的无用镜像 ==='
                sh 'docker image prune -f'
            }
        }
    }

    post {
        success {
            echo '========================================='
            echo '  [SUCCESS] 项目拉取、编译、镜像打包与部署成功！'
            echo '========================================='
        }
        failure {
            echo '========================================='
            echo '  [FAILURE] 构建或部署失败，请检查上方日志！'
            echo '========================================='
        }
    }
}