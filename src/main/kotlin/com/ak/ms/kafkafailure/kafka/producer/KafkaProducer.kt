package com.ak.ms.kafkafailure.kafka.producer

import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.support.KafkaHeaders.MESSAGE_KEY
import org.springframework.kafka.support.KafkaHeaders.TOPIC
import org.springframework.messaging.support.MessageBuilder
import org.springframework.stereotype.Service

@Service
class KafkaProducer(private val kafkaTemplate: KafkaTemplate<String, Any>) {

    fun sendMessage(topic: String, key: String, data: Any) {
      val message = MessageBuilder
        .withPayload(data)
        .setHeader(TOPIC, topic)
        .setHeader(MESSAGE_KEY, key)
        .build();

      kafkaTemplate.send(message)
    }
}