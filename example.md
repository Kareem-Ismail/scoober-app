# Scoober Code Challenge Bootstrap Project

Welcome!

We created this template application to showcase some of the dependencies you might want to use in your code
challenge and then avoid the boilerplate work to set up everything. The usage of this repository is completely optional
and is provided as a helper if you want to save time setting up a project. Feel free to choose whichever features you need 
from the list below. This repository contains a Spring-boot application with the following capabilities:

- Docker compose with Redpanda, RabbitMQ, MongoDB
- Maven wrapper (mvnw)
- Spring Web (and an example API with endpoints)
  - http://localhost:8080/hello
- MongoDB (and an example class with Spring Data Mongo)
  - http://localhost:8080/mongo
  - You can access the MongoDB under: mongodb://localhost:27017/mongodatabase
- SQL (and an example class with Spring JPA over H2 DB in memory)
  - http://localhost:8080/h2
  - You can access the H2 database under: http://localhost:8080/h2-console (db name: jdbc:h2:mem:h2database)
- Kafka (and a producer/consumer example class)
  - http://localhost:8080/kafka
  - You can acces the RedPanda console under: http://localhost:8090
- RabbitMQ (and a publish/subscribe example class)
  - http://localhost:8080/rabbit
  - You can access the RabbitMQ Management tool under: http://localhost:15672 (user: guest, password: guest)


## Instructions

- To start the docker-compose: `docker-compose up`
- To run the application: `./mvnw spring-boot:run`
