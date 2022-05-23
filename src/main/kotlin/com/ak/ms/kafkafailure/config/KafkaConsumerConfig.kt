package com.ak.ms.kafkafailure.config

import com.fasterxml.jackson.databind.ObjectMapper
import mu.KotlinLogging
import org.apache.kafka.clients.consumer.ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG
import org.apache.kafka.clients.consumer.ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.common.TopicPartition
import org.apache.kafka.common.serialization.StringDeserializer
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory
import org.springframework.kafka.core.ConsumerFactory
import org.springframework.kafka.core.DefaultKafkaConsumerFactory
import org.springframework.kafka.core.KafkaOperations
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer
import org.springframework.kafka.listener.DefaultErrorHandler
import org.springframework.kafka.support.converter.StringJsonMessageConverter
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer.KEY_DESERIALIZER_CLASS
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer.VALUE_DESERIALIZER_CLASS
import org.springframework.util.backoff.FixedBackOff

@Configuration
class KafkaConsumerConfig(@Value(value = "\${spring.kafka.bootstrap-servers}") val bootstrapAddress: String) {
  private val logger = KotlinLogging.logger {}

  @Bean
  fun consumerFactory(): ConsumerFactory<String, Any> {
    val configProps: MutableMap<String, Any> = HashMap()
    configProps[ProducerConfig.BOOTSTRAP_SERVERS_CONFIG] = bootstrapAddress
    configProps[KEY_DESERIALIZER_CLASS] = StringDeserializer::class.java
    configProps[VALUE_DESERIALIZER_CLASS] = StringDeserializer::class.java
    configProps[KEY_DESERIALIZER_CLASS_CONFIG] = ErrorHandlingDeserializer::class.java
    configProps[VALUE_DESERIALIZER_CLASS_CONFIG] = ErrorHandlingDeserializer::class.java
    return DefaultKafkaConsumerFactory(configProps)
  }

  @Bean
  fun kafkaListenerContainerFactory(
    kafkaTemplate: KafkaTemplate<String, Any>,
    kafkaOperations: KafkaOperations<*, *>,
    objectMapper: ObjectMapper,
    kafkaTopicProperties: KafkaTopicProperties
  ): ConcurrentKafkaListenerContainerFactory<String, Any> {
    val factory = ConcurrentKafkaListenerContainerFactory<String, Any>()
    factory.setMessageConverter(StringJsonMessageConverter())
    factory.consumerFactory = consumerFactory()
    factory.setCommonErrorHandler(
      DefaultErrorHandler(
        DeadLetterPublishingRecoverer(
          kafkaOperations
        ) { cr: ConsumerRecord<*, *>, e: Exception? ->
          logger.info("Received Error From Topic:${cr.topic()} and Partition: ${cr.partition()}")
          TopicPartition(
            "${cr.topic()}_${kafkaTopicProperties.consumer.groupId}_RETRY",
            cr.partition()
          )
        },
        FixedBackOff(0, 0)
      )
    )
    return factory;
  }

  @Bean
  fun kafkaRetryListenerContainerFactory(
    kafkaTemplate: KafkaTemplate<String, Any>,
    kafkaOperations: KafkaOperations<*, *>,
    objectMapper: ObjectMapper
  ): ConcurrentKafkaListenerContainerFactory<String, Any> {
    val factory = ConcurrentKafkaListenerContainerFactory<String, Any>()
    factory.setMessageConverter(StringJsonMessageConverter())
    factory.consumerFactory = consumerFactory()
    factory.setCommonErrorHandler(
      DefaultErrorHandler(
        DeadLetterPublishingRecoverer(
          kafkaOperations
        ) { cr: ConsumerRecord<*, *>, e: Exception? ->

          TopicPartition(
            cr.topic().replace("_RETRY", "_ERROR"),
            cr.partition()
          )
        },
        FixedBackOff(3000, 3)
      )
    )
    return factory;
  }

  @Bean
  fun kafkaDeadLetterContainerFactory(): ConcurrentKafkaListenerContainerFactory<String, Any> {
    val factory = ConcurrentKafkaListenerContainerFactory<String, Any>()
    factory.setMessageConverter(StringJsonMessageConverter())
    factory.consumerFactory = consumerFactory()
    return factory;
  }

}