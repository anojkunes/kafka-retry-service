package com.ak.ms.kafkafailure.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

@Component
@ConfigurationProperties(prefix = "spring.kafka")
data class KafkaTopicProperties(
  val topics: List<KafkaTopicProperty>,
  val consumer: KafkaConsumerProperty
)

@Component
data class KafkaTopicProperty(
  var name: String = "",
  var replicaFactor: Int = 0,
  var partition: Int = 0
)

@Component
data class KafkaConsumerProperty(
  var groupId: String = ""
)