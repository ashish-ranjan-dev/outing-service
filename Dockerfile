FROM openjdk:17
EXPOSE 8097
ADD ./build/libs/outing-service-0.0.1-SNAPSHOT.jar outing-service-0.0.1-SNAPSHOT.jar
ENTRYPOINT ["java","-jar","outing-service-0.0.1-SNAPSHOT.jar"]
