package eu.h2020.symbiote.rh.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import eu.h2020.symbiote.cloud.trust.model.TrustEntry;
import eu.h2020.symbiote.rh.PlatformInformationManager;
import eu.h2020.symbiote.rh.constants.RHConstants;
import eu.h2020.symbiote.util.RabbitConstants;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.Argument;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class RegistrationHandlerRabbitListener {

    private static Log logger = LogFactory.getLog(RegistrationHandlerRabbitListener.class);

    @Autowired
    private PlatformInformationManager infoManager;

    private ObjectMapper mapper = new ObjectMapper();


    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(
                    value = RHConstants.RH_RESOURCE_TRUST_UPDATE_QUEUE_NAME,
                    durable = "true",
                    exclusive = "false",
                    autoDelete = "true", arguments= {
                    @Argument(name = "x-message-ttl", value="${spring.rabbitmq.template.reply-timeout}", type="java.lang.Integer")
            }),
            exchange = @Exchange(
                    value = "${" + RabbitConstants.EXCHANGE_TRUST_NAME_PROPERTY + "}",
                    type = "${" + RabbitConstants.EXCHANGE_TRUST_TYPE_PROPERTY + "}",
                    durable = "${" + RabbitConstants.EXCHANGE_TRUST_DURABLE_PROPERTY + "}",
                    autoDelete = "${" + RabbitConstants.EXCHANGE_TRUST_AUTODELETE_PROPERTY + "}"),
            key = "${" + RabbitConstants.ROUTING_KEY_TRUST_RESOURCE_UPDATED + "}"))
    public void trustUpdated(@Payload Message message) {
        try {
            TrustEntry trustEntry = mapper.readValue(message.getBody(), TrustEntry.class);
            infoManager.updateTrustValue(trustEntry);
        } catch (IOException e) {
            logger.error("Error deserializing trust value from message", e);
        }
    }

}
