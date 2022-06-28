A chatting service that might become the subject of my thesis. My goal is to achieve the highest possible throughput and resiliency.

**It uses the following technologies:**
* RabbitMQ
* Redis
* Project Reactor
* Sprinbgoot
* Kotlin

**Current features:**
* It supports basic communication between clients and uses an acknowledgement system to ensure delivery. 
* Multiple instances of the same service can be ran to simulate a distributed deployment.

**Trying it out:**
* A development environment can be spun up by running `docker-compose up -d` at the root directory. This sets up a redis and a rabbitmq instance. The authentication credentials for the rabbitmq instance need to be set through environment variables.
* Upon initialization, a websocket endpoint is exposed under the port specified in `application.properties` at `/messages/{user_id}`.
* The endpoint expects messages in the form of `{ "to": <String>, "from": <String>, "eventId": <UUID>, "body": <String> }`.
* The endpoint delivers events from other clients or acknowledgements of events sent by the client.
