package com.ak.ms.kafkafailure.config

import org.apache.kafka.clients.producer.ProducerConfig.*
import org.apache.kafka.common.serialization.StringSerializer
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.core.DefaultKafkaProducerFactory
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.core.ProducerFactory
import org.springframework.kafka.support.serializer.JsonSerializer

@Configuration
class KafkaProducerConfig(@Value(value = "\${spring.kafka.bootstrap-servers}") val bootstrapAddress: String) {
  @Bean
  fun producerFactory(): ProducerFactory<String, Any> {
    val configProps: MutableMap<String, Any> = HashMap()
    configProps[BOOTSTRAP_SERVERS_CONFIG] = bootstrapAddress
    configProps[KEY_SERIALIZER_CLASS_CONFIG] = StringSerializer::class.java
    configProps[VALUE_SERIALIZER_CLASS_CONFIG] = JsonSerializer::class.java

    return DefaultKafkaProducerFactory(configProps)
  }

  @Bean
  fun kafkaTemplate(): KafkaTemplate<String, Any> {
    return KafkaTemplate(producerFactory())
  }
}