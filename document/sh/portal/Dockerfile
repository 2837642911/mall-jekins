FROM eclipse-temurin:17-jre
LABEL maintainer="macro"
WORKDIR /app

# 复制 Maven 构建好的 jar 包
ADD mall-portal/target/mall-portal-1.0-SNAPSHOT.jar /app/mall-portal-1.0-SNAPSHOT.jar

EXPOSE 8085
ENTRYPOINT ["java", "-Xms256m", "-Xmx512m", "-jar", "/app/mall-portal-1.0-SNAPSHOT.jar"]
