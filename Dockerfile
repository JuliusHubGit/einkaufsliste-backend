#
# Build stage
#
FROM gradle:9-jdk25 AS build
COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src
RUN gradle build --no-daemon
#
# Package stage
#
FROM eclipse-temurin:25-jdk-jammy
COPY --from=build /home/gradle/src/build/libs/einkaufsliste-backend-0.0.1-SNAPSHOT.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
