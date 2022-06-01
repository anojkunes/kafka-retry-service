# ms-kafka-failure
This is a prototype service in where we have configured a main, retry and error topic. This service has an endpoint 
which publishes message to the main topic. If main topic fails, then it moves to retry topic. If retry topic fails 3 times it moves
to error topic.

However, the main topic would continue to consume hence this becomes more non-blocking for a consumer group.

This service has been built to support `kafka-replay` you would need to clone [kafka-replay](https://github.com/WeiLu1/kafka-replay).
You would need to run them both `kafka-replay` and `ms-kafka-failure` and check the logs on what happens (This is where the true magic lies).

Use the docker-compose file provided by either [kafka-failure](https://github.com/anojkunes/kafka-retry-service)
or [kafka-replay](https://github.com/WeiLu1/kafka-replay) to spin up the docker network.
```bash
docker compose up -d
```

To access mongodb, please use the username and password provided in the docker-compose file:
```
      username: root
      password: rootpassword
```

# Dependencies
- Java 17
- Gradle
- Kafka

# Endpoint
Service will be hosted on localhost:8080, otherwise you can change the server port.

1. Send message to topic Endpoint - this endpoint was created to test what happens when 
```bash
curl --location --request POST 'http://localhost:8080/kafka-failure/v1/internal' \
--header 'Content-Type: application/json' \
--data-raw '{
    "name": "test1",
    "datetime": "2022-03-01T15:00:00Z"
}'
```

# TODO
We are investigating on how we can support for messages that requires sequential data. This architecture would not be 100% best suited for it 
so we are looking at alternatives. Sequential Data meaning that messages depends on another messages within the same topic.

# Logs

Check the logs to see if you are able too see any errors and what is happening when messages are being consumed
