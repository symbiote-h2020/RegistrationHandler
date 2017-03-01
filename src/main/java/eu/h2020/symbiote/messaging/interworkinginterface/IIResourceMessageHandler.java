package eu.h2020.symbiote.messaging.interworkinginterface;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import eu.h2020.symbiote.cloud.model.CloudResource;

/**

 * This class invoke the RAbbitMQMessageHandler using the right message queue depending on the operation that is being
 * done with the data
 * @author: Elena Garrido
 * @version: 18/01/2017

 */
/**! \class IIResourceMessageHandler 
 * \brief This class invoke the \class RabbitMQRPCMessageHandlerResourceBean or the \class RabbitMQRPCMessageHandlerString depending on the operation that is being
 * done with the data. Before sending a \class ResourceBean it's \a internalId is set to blank. The aim of this is that this id doesn't leave the platform   
 **/
@Component
public class IIResourceMessageHandler {

    private static String EXCHANGE_NAME = "symbIoTe.InterworkingInterface";
    private static String RESOURCE_REGISTRATION_ROUTING_KEY = "symbIoTe.InterworkingInterface.registrationHandler.register_resources";
    private static String RESOURCE_REGISTRATION_ROUTING_KEY_REPLY = RESOURCE_REGISTRATION_ROUTING_KEY+".reply";
    private static String RESOURCE_UNREGISTRATION_ROUTING_KEY = "symbIoTe.InterworkingInterface.registrationHandler.unregister_resources";
    private static String RESOURCE_UNREGISTRATION_ROUTING_KEY_REPLY = RESOURCE_UNREGISTRATION_ROUTING_KEY+".reply";
    private static String RESOURCE_UPDATED_ROUTING_KEY = "symbIoTe.InterworkingInterface.registrationHandler.update_resources";
    private static String RESOURCE_UPDATED_ROUTING_KEY_REPLY = RESOURCE_UPDATED_ROUTING_KEY+".reply";


    private static Log logger = LogFactory.getLog(IIResourceMessageHandler.class);
    
	@Autowired
	private ApplicationContext applicationContext;

    public List<CloudResource> sendResourcesRegistrationMessage(List<CloudResource> resources) {
        try {
            logger.info("Sending request for registration of "+resources.size()+" resources ");
            RabbitMQRPCMessageHandlerResourceList rabbitMQMessageHandler = new RabbitMQRPCMessageHandlerResourceList(EXCHANGE_NAME, RESOURCE_REGISTRATION_ROUTING_KEY, RESOURCE_REGISTRATION_ROUTING_KEY_REPLY);
        	applicationContext.getAutowireCapableBeanFactory().autowireBean(rabbitMQMessageHandler);
        	rabbitMQMessageHandler.connect();
     	    List<CloudResource> listToSend = resources.stream().map(resource ->	{ CloudResource cloned = null;
     	    	try {
     	    		cloned = (CloudResource) resource.clone();
     	    	} catch (Exception e) {
				
					logger.error("Fatal error cloning resource", e);
     	    	}
     	    	cloned.setInternalId(""); return cloned;} )
     	    	 .collect(Collectors.toList());

     	    List<CloudResource> resourceListReceived = rabbitMQMessageHandler.sendMessage(listToSend);
     	   
      	   	
      	   	//be aware that the list must returned in the same order that it has been send
     	    int i = 0;
     	    for (CloudResource resource:resources)
     	    	resource.setId(resourceListReceived.get(i++).getId());
     	   
      	   	rabbitMQMessageHandler.close();
      	   	return resources;
        } catch (Exception e) {
            logger.error("Fatal error sending data to EXCHANGE_NAME: "+EXCHANGE_NAME+", RESOURCE_REGISTRATION_ROUTING_KEY:"+RESOURCE_REGISTRATION_ROUTING_KEY+", RESOURCE_REGISTRATION_ROUTING_KEY_REPLY:"+RESOURCE_REGISTRATION_ROUTING_KEY_REPLY, e);
        }
        return null;
    }

    public String sendResourceUnregistrationMessage( String resourceId) {
        try {
            logger.info("Sending request for unregistration resource with internal id " + resourceId);
            RabbitMQRPCMessageHandlerString rabbitMQMessageHandler = new RabbitMQRPCMessageHandlerString(EXCHANGE_NAME, RESOURCE_UNREGISTRATION_ROUTING_KEY, RESOURCE_UNREGISTRATION_ROUTING_KEY_REPLY);
        	applicationContext.getAutowireCapableBeanFactory().autowireBean(rabbitMQMessageHandler);
        	rabbitMQMessageHandler.connect();
        	String resourceIdResult = rabbitMQMessageHandler.sendMessage(resourceId);
            rabbitMQMessageHandler.close();
            logger.info("Unregistration result for resource with internal id " + resourceId);
            return resourceIdResult;
        } catch (Exception e) {
            logger.error("Fatal error sending data to EXCHANGE_NAME: "+EXCHANGE_NAME+", RESOURCE_UNREGISTRATION_ROUTING_KEY:"+RESOURCE_UNREGISTRATION_ROUTING_KEY+", RESOURCE_UNREGISTRATION_ROUTING_KEY_REPLY:"+RESOURCE_UNREGISTRATION_ROUTING_KEY_REPLY, e);
        }
        return null;
    }

    public List<CloudResource> sendResourceUpdateMessage(List<CloudResource> resources) {
        try {
            logger.info("Sending request for updating of "+resources.size()+" resources ");
            RabbitMQRPCMessageHandlerResourceList rabbitMQMessageHandler = new RabbitMQRPCMessageHandlerResourceList(EXCHANGE_NAME, RESOURCE_UPDATED_ROUTING_KEY, RESOURCE_UPDATED_ROUTING_KEY);
        	applicationContext.getAutowireCapableBeanFactory().autowireBean(rabbitMQMessageHandler);
        	rabbitMQMessageHandler.connect();
     	    List<CloudResource> listToSend = resources.stream().map(resource ->	{ CloudResource cloned = null;
     	    	try {
     	    		cloned = (CloudResource) resource.clone();
     	    	} catch (Exception e) {
				
					logger.error("Fatal error cloning resource", e);
     	    	}
     	    	cloned.setInternalId(""); return cloned;} )
     	    	 .collect(Collectors.toList());

     	    List<CloudResource> resourceListReceived = rabbitMQMessageHandler.sendMessage(listToSend);
     	   
      	   	
      	   	//be aware that the list must returned in the same order that it has been send
     	    int i = 0;
     	    for (CloudResource resource:resources)
     	    	resource.setId(resourceListReceived.get(i++).getId());
     	   
      	   	rabbitMQMessageHandler.close();
      	   	return resources;
            
        } catch (Exception e) {
            logger.error("Fatal error sending data to EXCHANGE_NAME: "+EXCHANGE_NAME+", RESOURCE_UPDATED_ROUTING_KEY:"+RESOURCE_UPDATED_ROUTING_KEY+", RESOURCE_UPDATED_ROUTING_KEY_REPLY:"+RESOURCE_UPDATED_ROUTING_KEY_REPLY, e);
        }
        return null;
    }

}
