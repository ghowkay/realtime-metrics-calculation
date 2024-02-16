FROM maven:3.6.2-jdk-8 AS build
COPY src /usr/src/app/src
COPY pom.xml /usr/src/app
RUN mvn -f /usr/src/app/pom.xml clean package


FROM gcr.io/distroless/java
COPY --from=build /usr/src/app/target/traveler_metrics /usr/app/traveler_metrics


ENTRYPOINT ["java","-jar","/usr/app/traveler_metrics/numinaMetrics.jar"]
