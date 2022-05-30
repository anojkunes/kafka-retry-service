# ms-kafka-failure
This is a prototype service in where we have configured a main, retry and error topic. This service has an endpoint 
which publishes message to the main topic. If main topic fails, then it moves to retry topic. If retry topic fails 3 times it moves
to error topic.

However, the main topic would continue to consume hence this becomes more non-blocking.

# Dependencies
- Run docker compose up command. This will start up mongodb, kafkadrop, kafka and zookeeper
- Run application
- Requires Java 17 and Gradle installed

# Endpoint

```curl
curl --location --request POST 'http://localhost:8080/kafka-failure/v1/internal' \
--header 'Content-Type: application/json' \
--data-raw '{
    "name": "test1",
    "datetime": "2022-03-01T15:00:00Z"
}'
```

# Logs

Check the logs to see if you are able too see any errors and what is happening when messages are being consumed