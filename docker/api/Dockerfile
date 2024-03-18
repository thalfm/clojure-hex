FROM clojure:openjdk-17-lein-slim-buster AS BUILD
COPY . /code
WORKDIR /code

RUN lein uberjar

FROM openjdk:17-jdk-alpine
ENV PORT 8890
EXPOSE 8890
WORKDIR /app
COPY --from=BUILD /code/target/uberjar/*-standalone.jar ./app.jar
CMD ["java", "-jar", "app.jar"]