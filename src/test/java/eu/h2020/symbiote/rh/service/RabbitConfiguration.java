/*
 *  Copyright 2018 Atos
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package eu.h2020.symbiote.rh.service;

import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;

@Configuration
public class RabbitConfiguration {

    public static final String REGISTRY_EXCHANGE_TEST_NAME = "symbIoTe.localRegistryTest";
    public static final String REGISTRY_UPDATE_QUEUE_NAME = "symbIoTe.localRegistryTest.update";
    public static final String REGISTRY_DELETE_QUEUE_NAME = "symbIoTe.localRegistryTest.delete";
    public static final String REGISTRY_SHARE_QUEUE_NAME = "symbIoTe.localRegistryTest.share";
    public static final String REGISTRY_UNSHARE_QUEUE_NAME = "symbIoTe.localRegistryTest.unshare";

    @Bean
    public DirectExchange registryTestExchange() {
        return new DirectExchange(REGISTRY_EXCHANGE_TEST_NAME, false, true);
    }

    @Bean
    public Queue updateQueue() {
        return new Queue(REGISTRY_UPDATE_QUEUE_NAME, false, false, true);
    }

    @Bean
    public Queue deleteQueue() {
        return new Queue(REGISTRY_DELETE_QUEUE_NAME, false, false, true);
    }

    @Bean
    public Queue shareQueue() {
        return new Queue(REGISTRY_SHARE_QUEUE_NAME, false, false, true);
    }

    @Bean
    public Queue unshareQueue() {
        return new Queue(REGISTRY_UNSHARE_QUEUE_NAME, false, false, true);
    }

    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(ConnectionFactory connectionFactory) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setConcurrentConsumers(3);
        factory.setMaxConcurrentConsumers(10);
        factory.setMessageConverter(jackson2JsonMessageConverter());
        return factory;
    }

    @Bean
    Jackson2JsonMessageConverter jackson2JsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}
