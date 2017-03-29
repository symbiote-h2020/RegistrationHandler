package eu.h2020.symbiote.service;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.stereotype.Service;
@Service
public class MonitoringRAPDummyServer {
    private static Log logger = LogFactory.getLog(MonitoringRAPDummyServer.class);
	
    private static final String EXCHANGE_NAME_REGISTRATION = "symbIoTe.rh.reg";
    private static final String EXCHANGE_NAME_UNREGISTRATION = "symbIoTe.rh.unreg";
    private static final String EXCHANGE_NAME_UPDATED = "symbIoTe.rh.update";
    private static final String RESOURCE_REGISTRATION_QUEUE_NAME = "symbIoTe.rap.registrationHandler.register_resources";
    private static final String RESOURCE_UNREGISTRATION_QUEUE_NAME = "symbIoTe.rap.registrationHandler.unregister_resources";
    private static final String RESOURCE_UPDATED_QUEUE_NAME = "symbIoTe.rap.registrationHandler.update_resources";

    private static final String RESOURCE_REGISTRATION_KEY = RESOURCE_REGISTRATION_QUEUE_NAME+"key";
    private static final String RESOURCE_UNREGISTRATION_KEY = RESOURCE_UNREGISTRATION_QUEUE_NAME+"key";
    private static final String RESOURCE_UPDATED_KEY = RESOURCE_UPDATED_QUEUE_NAME+"key";

	
	    @RabbitListener(bindings = @QueueBinding(
	        value = @Queue(value = RESOURCE_REGISTRATION_QUEUE_NAME, durable = "true", autoDelete = "false", exclusive = "false"),
	        exchange = @Exchange(value = EXCHANGE_NAME_REGISTRATION, ignoreDeclarationExceptions = "false", type = ExchangeTypes.FANOUT),
	        key = RESOURCE_REGISTRATION_KEY)
	    )
	    public void resourceRegistration(Message message, @Headers() Map<String, String> headers) {
	    	logger.info("**---->resourceRegistration "+new String(message.getBody()));
	    	
	    }
	    
	    @RabbitListener(bindings = @QueueBinding(
	            value = @Queue(value = RESOURCE_UNREGISTRATION_QUEUE_NAME, durable = "true", autoDelete = "false", exclusive = "false"),
	            exchange = @Exchange(value = EXCHANGE_NAME_UNREGISTRATION, ignoreDeclarationExceptions = "false", type = ExchangeTypes.FANOUT),
	            key = RESOURCE_UNREGISTRATION_KEY)
	        )
        public void resourceUnregistration(Message message, @Headers() Map<String, String> headers) {
	    	logger.info("**---->resourceUnregistration "+new String(message.getBody()));
        }
	    
	    @RabbitListener(bindings = @QueueBinding(
	            value = @Queue(value = RESOURCE_UPDATED_QUEUE_NAME, durable = "true", autoDelete = "false", exclusive = "false"),
	            exchange = @Exchange(value = EXCHANGE_NAME_UPDATED, ignoreDeclarationExceptions = "false", type = ExchangeTypes.FANOUT),
	            key = RESOURCE_UPDATED_KEY)
	        )
        public void resourceUpdate(Message message, @Headers() Map<String, String> headers) {
	    	logger.info("**---->resourceUpdate "+new String(message.getBody()));
        }

	    
}

