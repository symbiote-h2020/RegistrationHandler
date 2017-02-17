package eu.h2020.symbiote.service;
import java.util.Map;

import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;

import eu.h2020.symbiote.beans.ResourceBean;
@Service
public class IIFDummyServer {
	
    @Autowired
    private RabbitTemplate rabbitTemplate;

	   /**
	   * Spring AMQP Listener for resource registration requests. This method is invoked when Registration
	   * Handler sends a resource registration request and it is responsible for forwarding the message
	   * to the symbIoTe core. As soon as it receives a reply, it manually sends back the response
	   * to the Registration Handler via the appropriate message queue by the use of the RestAPICallback.
	   * 
	   * @param jsonObject A jsonObject containing the resource description
	   * @param headers The AMQP headers
	   */
	    @RabbitListener(bindings = @QueueBinding(
	        value = @Queue(value = "symbIoTe-InterworkingInterface-registrationHandler-register_resources", durable = "true", autoDelete = "false", exclusive = "false"),
	        exchange = @Exchange(value = "symbIoTe.InterworkingInterface", ignoreDeclarationExceptions = "true", type = ExchangeTypes.DIRECT),
	        key = "symbIoTe.InterworkingInterface.registrationHandler.register_resources")
	    )
	    public void resourceRegistration(Message message, @Headers() Map<String, String> headers) {
	    	
            Gson gson = new Gson();
            ResourceBean resourceBean = gson.fromJson(new String(message.getBody()), ResourceBean.class);

            resourceBean.setId("symbiote"+resourceBean.getInternalId());
            String response  = gson.toJson(resourceBean);
	    	
	        rabbitTemplate.convertAndSend(headers.get("amqp_replyTo"), response.getBytes(),
               m -> {
                		Object a = headers.get("amqp_correlationId");
                        m.getMessageProperties().setCorrelationId((byte[])a);
                        return m;
               });
	    }
	    
	    @RabbitListener(bindings = @QueueBinding(
	            value = @Queue(value = "symbIoTe-InterworkingInterface-registrationHandler-unregister_resources", durable = "true", autoDelete = "false", exclusive = "false"),
	            exchange = @Exchange(value = "symbIoTe.InterworkingInterface", ignoreDeclarationExceptions = "true", type = ExchangeTypes.DIRECT),
	            key = "symbIoTe.InterworkingInterface.registrationHandler.unregister_resources")
	        )
        public void resourceUnregistration(Message message, @Headers() Map<String, String> headers) {
	        rabbitTemplate.convertAndSend(headers.get("amqp_replyTo"), message.getBody(),
               m -> {
                		Object a = headers.get("amqp_correlationId");
                        m.getMessageProperties().setCorrelationId((byte[])a);
                        return m;
               });
        }
	    
	    @RabbitListener(bindings = @QueueBinding(
	            value = @Queue(value = "symbIoTe-InterworkingInterface-registrationHandler-update_resources", durable = "true", autoDelete = "false", exclusive = "false"),
	            exchange = @Exchange(value = "symbIoTe.InterworkingInterface", ignoreDeclarationExceptions = "true", type = ExchangeTypes.DIRECT),
	            key = "symbIoTe.InterworkingInterface.registrationHandler.update_resources")
	        )
        public void resourceUpdate(Message message, @Headers() Map<String, String> headers) {
            Gson gson = new Gson();
            ResourceBean resourceBean = gson.fromJson(new String(message.getBody()), ResourceBean.class);

            resourceBean.setId("symbiote"+resourceBean.getInternalId());
            String response  = gson.toJson(resourceBean);
	    	
	        rabbitTemplate.convertAndSend(headers.get("amqp_replyTo"), response.getBytes(),
               m -> {
                		Object a = headers.get("amqp_correlationId");
                        m.getMessageProperties().setCorrelationId((byte[])a);
                        return m;
               });

        }

	    
}

