package eu.h2020.symbiote.messaging.interworkinginterface;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import eu.h2020.symbiote.beans.ResourceBean;

/**

 * This class invoke the RAbbitMQMessageHandler using the right message queue depending on the operation that is being
 * done with the data
 * @author: Elena Garrido
 * @version: 18/01/2017

 */
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

    public ResourceBean sendResourceRegistrationMessage( ResourceBean resourceBean) {
        try {
            logger.info("Sending request for registration " + resourceBean.getInternalId());
            RabbitMQRPCMessageHandlerResourceBean rabbitMQMessageHandler = new RabbitMQRPCMessageHandlerResourceBean(EXCHANGE_NAME, RESOURCE_REGISTRATION_ROUTING_KEY, RESOURCE_REGISTRATION_ROUTING_KEY_REPLY);
        	applicationContext.getAutowireCapableBeanFactory().autowireBean(rabbitMQMessageHandler);
        	rabbitMQMessageHandler.connect();
        	ResourceBean resourceBeanResult = rabbitMQMessageHandler.sendMessage(resourceBean);
            rabbitMQMessageHandler.close();
            logger.info("Sending result for " + resourceBean.getInternalId()+ " --> symbioteId:"+resourceBeanResult.getSymbioteId());
            return resourceBeanResult;
        } catch (Exception e) {
            logger.error("Fatal error sending data to EXCHANGE_NAME: "+EXCHANGE_NAME+", RESOURCE_REGISTRATION_ROUTING_KEY:"+RESOURCE_REGISTRATION_ROUTING_KEY+", RESOURCE_REGISTRATION_ROUTING_KEY_REPLY:"+RESOURCE_REGISTRATION_ROUTING_KEY_REPLY, e);
        }
        return null;
    }

    public String sendResourceUnregistrationMessage( String resourceId) {
        try {
            logger.info("Sending request for unregistration " + resourceId);
            RabbitMQRPCMessageHandlerString rabbitMQMessageHandler = new RabbitMQRPCMessageHandlerString(EXCHANGE_NAME, RESOURCE_UNREGISTRATION_ROUTING_KEY, RESOURCE_UNREGISTRATION_ROUTING_KEY_REPLY);
        	applicationContext.getAutowireCapableBeanFactory().autowireBean(rabbitMQMessageHandler);
        	rabbitMQMessageHandler.connect();
        	String resourceIdResult = rabbitMQMessageHandler.sendMessage(resourceId);
            rabbitMQMessageHandler.close();
            logger.info("Unregistration result for " + resourceId);
            return resourceIdResult;
        } catch (Exception e) {
            logger.error("Fatal error sending data to EXCHANGE_NAME: "+EXCHANGE_NAME+", RESOURCE_UNREGISTRATION_ROUTING_KEY:"+RESOURCE_UNREGISTRATION_ROUTING_KEY+", RESOURCE_UNREGISTRATION_ROUTING_KEY_REPLY:"+RESOURCE_UNREGISTRATION_ROUTING_KEY_REPLY, e);
        }
        return null;
    }

    public ResourceBean sendResourceUpdateMessage( ResourceBean resourceBean) {
        try {
            logger.info("Sending request for update " + resourceBean.getInternalId());
            RabbitMQRPCMessageHandlerResourceBean rabbitMQMessageHandler = new RabbitMQRPCMessageHandlerResourceBean(EXCHANGE_NAME, RESOURCE_UPDATED_ROUTING_KEY, RESOURCE_UPDATED_ROUTING_KEY_REPLY);
        	applicationContext.getAutowireCapableBeanFactory().autowireBean(rabbitMQMessageHandler);
        	rabbitMQMessageHandler.connect();
        	ResourceBean resourceBeanResult = rabbitMQMessageHandler.sendMessage(resourceBean);
            rabbitMQMessageHandler.close();
            logger.info("Update result for " + resourceBean.getInternalId()+ " --> symbioteId:"+resourceBeanResult.getSymbioteId());
            return resourceBeanResult;
        } catch (Exception e) {
            logger.error("Fatal error sending data to EXCHANGE_NAME: "+EXCHANGE_NAME+", RESOURCE_UPDATED_ROUTING_KEY:"+RESOURCE_UPDATED_ROUTING_KEY+", RESOURCE_UPDATED_ROUTING_KEY_REPLY:"+RESOURCE_UPDATED_ROUTING_KEY_REPLY, e);
        }
        return null;
    }

}
