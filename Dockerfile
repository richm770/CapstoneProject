FROM amazoncorretto:21-alpine-jdk AS build

WORKDIR /app

COPY mvnw* pom.xml /app/
COPY .mvn .mvn

RUN chmod +x mvnw

RUN ./mvnw dependency:go-offline -B

COPY src /app/src

RUN ./mvnw package -DskipTests

FROM amazoncorretto:21-alpine

WORKDIR /app

COPY --from=build /app/target/*.jar app.jar
COPY --from=build /app/mvnw /app/mvnw
COPY --from=build /app/.mvn /app/.mvn

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
