FROM eclipse-temurin:17-jre
LABEL maintainer="macro"
WORKDIR /app

# 复制 Maven 构建好的 jar 包
ADD mall-admin/target/mall-admin-1.0-SNAPSHOT.jar /app/mall-admin-1.0-SNAPSHOT.jar

EXPOSE 8092
ENTRYPOINT ["java", "-Xms256m", "-Xmx512m", "-jar", "/app/mall-admin-1.0-SNAPSHOT.jar"]
