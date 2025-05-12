FROM openjdk:23
LABEL authors="tonac"

COPY build/libs/tic-tac-toe_spring.jar app.jar
#COPY build/libs/tictactoe_spring-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8081

ENTRYPOINT ["java", "-jar", "app.jar"]