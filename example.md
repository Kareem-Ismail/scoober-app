# Scoober Code Challenge Bootstrap Project

Welcome!

We made this template application to demonstrate various dependencies you might find useful for your code challenge project. 
It's there to help you skip the initial setup work. Whether or not you use it is entirely up to you — it's an optional 
resource. You're welcome to select **any combination** of services listed below, **or none at all**. The repository 
includes a Spring Boot application with the following features:

- Docker compose with Redpanda, RabbitMQ, MongoDB
- Maven wrapper (mvnw)
- Spring Web (and an example API with endpoints)
  - http://localhost:8080/test/hello
- MongoDB (and an example class with Spring Data Mongo)
  - http://localhost:8080/test/mongo
  - You can access the MongoDB under: mongodb://localhost:27017/mongodatabase
- SQL (and an example class with Spring JPA over H2 DB in memory)
  - http://localhost:8080/test/h2
  - You can access the H2 database under: http://localhost:8080/h2-console (db name: jdbc:h2:mem:h2database)
- Kafka (and a producer/consumer example class)
  - http://localhost:8080/test/kafka
  - You can acces the RedPanda console under: http://localhost:8090
- RabbitMQ (and a publish/subscribe example class)
  - http://localhost:8080/test/rabbit
  - You can access the RabbitMQ Management tool under: http://localhost:15672 (user: guest, password: guest)


## Instructions

Here are the instructions on how to start this project

1. Start the docker-compose: `docker-compose up`
2. Run the application: `./mvnw spring-boot:run`
