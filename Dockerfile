
FROM amazoncorretto:11-alpine-jdk
MAINTAINER JUAN
COPY target/jsw-0.0.1-SNAPSHOT.jar jsw-app.jar
ENTRYPOINT ["java","-jar","/jsw-app.jar"]
CMD ["/bin/sh"]
