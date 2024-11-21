FROM openjdk:17-jdk-slim

# 애플리케이션 JAR 파일 복사
COPY build/libs/Project509-0.0.1-SNAPSHOT.jar app.jar

# wait-for-it.sh 스크립트를 컨테이너로 복사
COPY wait-for-it.sh /wait-for-it.sh
RUN chmod +x /wait-for-it.sh

# wait-for-it.sh를 통해 db 서비스가 시작될 때까지 대기한 후 애플리케이션 시작
CMD ["/wait-for-it.sh", "db:3306", "--", "java", "-jar", "app.jar"]



## 관리형 이미지를 설정합니다. 여기서는 RabbitMQ 이미지를 선택했습니다.
#FROM rabbitmq:latest
#
## rabbitmq.conf에서 구성한 RabbitMQ 설정 파일을 복사합니다.
#COPY ./module-rabbitmq/config/rabbitmq.conf /etc/rabbitmq/rabbitmq.conf
#
## RabbitMQ 플러그인을 활성화합니다.
#RUN rabbitmq-plugins enable --offline rabbitmq_management
#
## RabbitMQ 관리자 계정, 비밀번호를 지정합니다.
#ENV RABBITMQ_DEFAULT_USER admin
#ENV RABBITMQ_DEFAULT_PASS 1234
#
## 컨테이너가 시작될 때 실행될 명령을 설정합니다.
#CMD ["rabbitmq-server"]