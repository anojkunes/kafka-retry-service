package com.ak.ms.kafkafailure.config

import org.apache.kafka.clients.admin.NewTopic
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory
import org.springframework.context.annotation.Configuration
import javax.annotation.PostConstruct


@Configuration
class KafkaTopicConfig(
    private val beanFactory: ConfigurableListableBeanFactory,
    private val properties: KafkaTopicProperties
) {
    @PostConstruct
    fun registerBeans() {
        properties.topics.forEach { topic ->
            val newTopic = NewTopic(topic.name, topic.partition, topic.replicaFactor.toShort())
            beanFactory.initializeBean(newTopic, newTopic.name())
            beanFactory.registerSingleton(newTopic.name(), newTopic)

            setOf("_RETRY", "_ERROR").forEach{
                val newErrorOrRetryTopic = NewTopic(topic.name + "_${properties.consumer.groupId}" + it, topic.partition, topic.replicaFactor.toShort())
                beanFactory.initializeBean(newErrorOrRetryTopic, newErrorOrRetryTopic.name())
                beanFactory.registerSingleton(newErrorOrRetryTopic.name(), topic)
            }
        }
    }
}