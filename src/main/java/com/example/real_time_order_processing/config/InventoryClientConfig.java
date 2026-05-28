package com.example.real_time_order_processing.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(InventoryProperties.class)
public class InventoryClientConfig
{
}
