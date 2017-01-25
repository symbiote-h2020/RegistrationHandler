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
public class IFDirectResourceMessageHandler {

    private static String EXCHANGE_NAME = "symbIoTe.InterworkingInterface";
    private static String RESOURCE_REGISTRATION_ROUTING_KEY = "symbIoTe.InterworkingInterface.registrationHandler.register_resources";
    private static String RESOURCE_UNREGISTRATION_ROUTING_KEY = "symbIoTe.InterworkingInterface.registrationHandler.unregister_resources";
    private static String RESOURCE_UPDATED_ROUTING_KEY = "symbIoTe.InterworkingInterface.registrationHandler.update_resources";


    private static Log logger = LogFactory.getLog(IFDirectResourceMessageHandler.class);
    
	@Autowired
	private ApplicationContext applicationContext;

    public void sendResourceRegistrationMessage( ResourceBean resourceBean) {
        try {
            logger.info("Sending request for registration " + resourceBean.getInternalId());
            RabbitMQDirectMessageHandlerResourceBean rabbitMQMessageHandler = new RabbitMQDirectMessageHandlerResourceBean(EXCHANGE_NAME, RESOURCE_REGISTRATION_ROUTING_KEY);
        	applicationContext.getAutowireCapableBeanFactory().autowireBean(rabbitMQMessageHandler);
        	rabbitMQMessageHandler.sendMessage(resourceBean);
        } catch (Exception e) {
            logger.error("Fatal error sending data to EXCHANGE_NAME: "+EXCHANGE_NAME+", RESOURCE_REGISTRATION_ROUTING_KEY:"+RESOURCE_REGISTRATION_ROUTING_KEY, e);
        }
    }

    public void sendResourceUnregistrationMessage( String resourceId) {
        try {
            logger.info("Sending request for unregistration " + resourceId);
            RabbitMQDirectMessageHandlerString rabbitMQMessageHandler = new RabbitMQDirectMessageHandlerString(EXCHANGE_NAME, RESOURCE_UNREGISTRATION_ROUTING_KEY);
        	applicationContext.getAutowireCapableBeanFactory().autowireBean(rabbitMQMessageHandler);
        	rabbitMQMessageHandler.sendMessage(resourceId);
            logger.info("Unregistration result for " + resourceId);
            
        } catch (Exception e) {
            logger.error("Fatal error sending data to EXCHANGE_NAME: "+EXCHANGE_NAME+", RESOURCE_UNREGISTRATION_ROUTING_KEY:"+RESOURCE_UNREGISTRATION_ROUTING_KEY, e);
        }
    }

    public void sendResourceUpdateMessage( ResourceBean resourceBean) {
        try {
            logger.info("Sending request for update " + resourceBean.getInternalId());
            RabbitMQDirectMessageHandlerResourceBean rabbitMQMessageHandler = new RabbitMQDirectMessageHandlerResourceBean(EXCHANGE_NAME, RESOURCE_UPDATED_ROUTING_KEY);
        	applicationContext.getAutowireCapableBeanFactory().autowireBean(rabbitMQMessageHandler);
        	rabbitMQMessageHandler.sendMessage(resourceBean);
        } catch (Exception e) {
            logger.error("Fatal error sending data to EXCHANGE_NAME: "+EXCHANGE_NAME+", RESOURCE_UPDATED_ROUTING_KEY:"+RESOURCE_UPDATED_ROUTING_KEY, e);
        }
    }

}
