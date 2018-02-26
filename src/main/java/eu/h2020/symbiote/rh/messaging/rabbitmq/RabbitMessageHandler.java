package eu.h2020.symbiote.rh.messaging.rabbitmq;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
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

    @Value("${rh.exchange.name}")
    private String exchangeName;

    /**
     * Method for sending a message to specified 'queue' on RabbitMQ server. Object is converted to Json.
     *
     * @param object
     * @throws Exception
     */
    public void sendMessage(String exchangeName, String keyName, T object) {
        try {
            logger.debug("START OF sendMessage to key"+keyName);
            ObjectMapper mapper = new ObjectMapper();
            String objectInJson = mapper.writeValueAsString(object);

            rabbitTemplate.convertAndSend(exchangeName, keyName, objectInJson.getBytes("UTF-8"));
            logger.debug("END OF sendMessage to key: "+keyName);
        } catch (JsonProcessingException e) {
            logger.error("Invalid object passed to RabbitMQ", e);
        } catch (UnsupportedEncodingException e) {
            logger.error("Bad enconding when sending RabbitMQ message",e);
        }
    }

    public void sendMessage(String keyName, T object) {
        sendMessage(exchangeName, keyName, object);
    }

    public <I,O> O sendAndReceive(String exchangeName, String keyName, I object, TypeReference<O> typeReference) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            String objectInJson = mapper.writeValueAsString(object);

            Message response = (Message) rabbitTemplate.convertSendAndReceive(exchangeName, keyName, objectInJson.getBytes("UTF-8"));
            return mapper.readValue(response.getBody(), typeReference);
        } catch (IOException e) {
            logger.error("Error sending RPC message",e);
        }
        return null;
    }
}
