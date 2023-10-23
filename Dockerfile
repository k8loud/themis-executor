FROM openjdk:17-alpine

WORKDIR /executor

COPY ./executor/target/executor-0.0.1-SNAPSHOT.jar executor.jar

CMD ["java", "-jar", "/executor/executor.jar"]

EXPOSE 8080