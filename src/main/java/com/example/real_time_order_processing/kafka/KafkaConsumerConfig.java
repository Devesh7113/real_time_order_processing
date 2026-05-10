package com.example.real_time_order_processing.kafka;

import com.example.real_time_order_processing.modules.orderService.dto.InventoryProcessResponse;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaConsumerConfig
{
    @Bean
    public ConsumerFactory<String, InventoryProcessResponse> inventoryConsumerFactory(KafkaProperties kafkaProperties) {
        Map<String, Object> props = new HashMap<>(kafkaProperties.buildConsumerProperties(null));
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "order-service-inventory");
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        JsonDeserializer<InventoryProcessResponse> valueDeserializer = new JsonDeserializer<>(InventoryProcessResponse.class);
        valueDeserializer.addTrustedPackages("com.example.real_time_order_processing");

        return new DefaultKafkaConsumerFactory<>(props, new StringDeserializer(), valueDeserializer);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, InventoryProcessResponse> inventoryKafkaListenerContainerFactory(
            ConsumerFactory<String, InventoryProcessResponse> inventoryConsumerFactory) {
        ConcurrentKafkaListenerContainerFactory<String, InventoryProcessResponse> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(inventoryConsumerFactory);
        return factory;
    }
}
