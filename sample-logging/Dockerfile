FROM openjdk:11.0.8-slim
ARG JAR_FILE=target/kubernetes-spring-logging-0.0.1-SNAPSHOT.jar
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java","-jar","/app.jar"]