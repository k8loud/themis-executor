FROM openjdk:17-alpine

WORKDIR "/themis-executor"

ARG VER

COPY ./target/themis-executor-${VER}.jar themis-executor.jar

CMD ["java", "-jar", "/themis-executor/themis-executor.jar"]

EXPOSE 8080