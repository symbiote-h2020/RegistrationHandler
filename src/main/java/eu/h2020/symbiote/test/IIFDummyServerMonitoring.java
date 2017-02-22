package eu.h2020.symbiote.test;
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
public class IIFDummyServerMonitoring {
    private static final String EXCHANGE_NAME = "symbIoTe.rap";
    private static final String RESOURCE_REGISTRATION_QUEUE_NAME = "symbIoTe.rap.registrationHandler.register_resources";
    private static final String RESOURCE_UNREGISTRATION_QUEUE_NAME = "symbIoTe.rap.registrationHandler.unregister_resources";
    private static final String RESOURCE_UPDATED_QUEUE_NAME = "symbIoTe.rap.registrationHandler.update_resources";


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
	        value = @Queue(value = "symbIoTe-register_resources", durable = "true", autoDelete = "false", exclusive = "false"),
	        exchange = @Exchange(value = EXCHANGE_NAME, ignoreDeclarationExceptions = "true", type = ExchangeTypes.FANOUT),
	        key = RESOURCE_REGISTRATION_QUEUE_NAME)
	    )
	    public void resourceRegistration(Message message, @Headers() Map<String, String> headers) {
            Gson gson = new Gson();
            ResourceBean resourceBean = gson.fromJson(new String(message.getBody()), ResourceBean.class);
            System.out.println("resourceBean "+resourceBean + " - "+ message.getBody());
	    	
	    }
	    
	    @RabbitListener(bindings = @QueueBinding(
	            value = @Queue(value = "symbIoTe-unregister_resources", durable = "true", autoDelete = "false", exclusive = "false"),
	            exchange = @Exchange(value = EXCHANGE_NAME, ignoreDeclarationExceptions = "true", type = ExchangeTypes.FANOUT),
	            key = RESOURCE_UNREGISTRATION_QUEUE_NAME)
	        )
        public void resourceUnregistration(Message message, @Headers() Map<String, String> headers) {
            System.out.println(message.getBody());
        }
	    
	    @RabbitListener(bindings = @QueueBinding(
	            value = @Queue(value = "symbIoTe-update_resources", durable = "true", autoDelete = "false", exclusive = "false"),
	            exchange = @Exchange(value = EXCHANGE_NAME, ignoreDeclarationExceptions = "true", type = ExchangeTypes.FANOUT),
	            key = RESOURCE_UPDATED_QUEUE_NAME)
	        )
        public void resourceUpdate(Message message, @Headers() Map<String, String> headers) {
            Gson gson = new Gson();
            ResourceBean resourceBean = gson.fromJson(new String(message.getBody()), ResourceBean.class);
            System.out.println("resourceBean "+resourceBean + " - "+ message.getBody());
        }

	    
}

