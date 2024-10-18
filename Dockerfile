FROM amazoncorretto:11-alpine-jdk
MAINTAINER JUAN

# Instalar Maven o Gradle si es necesario
RUN apk add --no-cache maven

# Copiar el código fuente y construir el JAR
COPY . /app
WORKDIR /app
RUN mvn clean package # O gradle build, dependiendo de tu build tool

# Copiar el archivo JAR generado
COPY target/jsw-0.0.1-SNAPSHOT.jar jsw-app.jar
ENTRYPOINT ["java", "-jar", "/jsw-app.jar"]
CMD ["/bin/sh"]

