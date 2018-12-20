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

package eu.h2020.symbiote.rh.messaging.rabbitmq;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import eu.h2020.symbiote.util.RabbitConstants;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.function.Function;


/**
 * This class is to communicate with RabbitMQ. Initially created by mateuszl and updated by Elena
 *
 * @author: mateuszl, Elena Garrido
 * @version: 18/01/2017

 */
@Component
public class RabbitMessageHandler<T>{

    private static Log logger = LogFactory.getLog( RabbitMessageHandler.class );

    @Autowired
    RabbitTemplate rabbitTemplate;

    @Value("${" + RabbitConstants.EXCHANGE_RH_NAME_PROPERTY + "}")
    private String exchangeName;

    /**
     * Method for sending a message to specified 'queue' on RabbitMQ server. Object is converted to Json.
     *
     * @param object
     * @throws Exception
     */
    public void sendMessage(String exchangeName, String keyName, T object) {
        logger.debug("START OF sendMessage to key"+keyName);

        rabbitTemplate.convertAndSend(exchangeName, keyName, object);
        logger.debug("END OF sendMessage to key: "+keyName);
    }

    public void sendMessage(String keyName, T object) {
        sendMessage(exchangeName, keyName, object);
    }

    public <I,O> O sendAndReceive(String exchangeName, String keyName, I object, TypeReference<O> typeReference) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            String response = mapper.writeValueAsString(rabbitTemplate.convertSendAndReceive(exchangeName, keyName, object));
            return mapper.readValue(response, typeReference);
        } catch (IOException e) {
            logger.error("Error deserializing RabbitMQ RPC response",e);
        }
        return null;
    }
}
