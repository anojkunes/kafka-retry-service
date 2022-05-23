package com.ak.ms.kafkafailure.api

import com.ak.ms.kafkafailure.kafka.producer.KafkaProducer
import mu.KotlinLogging
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.*

@RestController
@RequestMapping(value = ["/kafka-failure/v1/internal"], produces = [MediaType.APPLICATION_JSON_VALUE])
class KafkaFailureController(
  val kafkaProducer: KafkaProducer
) {

  private val logger = KotlinLogging.logger {}

  @PostMapping
  fun sendMessage(@RequestBody message: Any) {
    val key = UUID.randomUUID().toString()
    logger.info("Sending message to some-topic: $message for key: $key")
    kafkaProducer.sendMessage("some-topic", key, message)
  }

}