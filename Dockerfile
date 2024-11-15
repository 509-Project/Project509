FROM openjdk:17-jdk-slim

# 애플리케이션 JAR 파일 복사
COPY build/libs/Project509-0.0.1-SNAPSHOT.jar app.jar

# wait-for-it.sh 스크립트를 컨테이너로 복사
COPY wait-for-it.sh /wait-for-it.sh
RUN chmod +x /wait-for-it.sh

# wait-for-it.sh를 통해 db 서비스가 시작될 때까지 대기한 후 애플리케이션 시작
CMD ["/wait-for-it.sh", "db:3306", "--", "java", "-jar", "app.jar"]
