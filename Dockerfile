FROM openjdk:17-alpine
COPY ../build/libs/shop-price.jar shop-price.jar
ENTRYPOINT ["java","-jar","/shop-price.jar"]