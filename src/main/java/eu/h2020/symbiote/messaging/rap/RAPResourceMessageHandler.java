package eu.h2020.symbiote.messaging.rap;

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

/**! \class RAPResourceMessageHandler 
 * \brief This class invoke the \class RabbitMQMessageHandlerResourceBean or the \class RabbitMQMessageHandlerString  depending on the operation that is being
 * done with the data. 
 **/
@Component
public class RAPResourceMessageHandler {

    private static String EXCHANGE_NAME = "symbIoTe.rap";
    private static String RESOURCE_REGISTRATION_QUEUE_NAME = "symbIoTe.rap.registrationHandler.register_resources";
    private static String RESOURCE_UNREGISTRATION_QUEUE_NAME = "symbIoTe.rap.registrationHandler.unregister_resources";
    private static String RESOURCE_UPDATED_QUEUE_NAME = "symbIoTe.rap.registrationHandler.update_resources";


    private static Log logger = LogFactory.getLog(RAPResourceMessageHandler.class);
    
	@Autowired
	private ApplicationContext applicationContext;

    public void sendResourceRegistrationMessage( ResourceBean resourceBean) {
        try {
            logger.info("Sending request for registration " + resourceBean.getInternalId());
            RabbitMQMessageHandlerResourceBean rabbitMQMessageHandler = new RabbitMQMessageHandlerResourceBean(EXCHANGE_NAME, RESOURCE_REGISTRATION_QUEUE_NAME);
        	applicationContext.getAutowireCapableBeanFactory().autowireBean(rabbitMQMessageHandler);
        	rabbitMQMessageHandler.sendMessage(resourceBean);
        } catch (Exception e) {
            logger.error("Fatal error sending data to EXCHANGE_NAME: "+EXCHANGE_NAME+", RESOURCE_REGISTRATION_QUEUE_NAME:"+RESOURCE_REGISTRATION_QUEUE_NAME, e);
        }
    }

    public void sendResourceUnregistrationMessage( String resourceId) {
        try {
            logger.info("Sending request for unregistration " + resourceId);
            RabbitMQMessageHandlerString rabbitMQMessageHandler = new RabbitMQMessageHandlerString(EXCHANGE_NAME, RESOURCE_UNREGISTRATION_QUEUE_NAME);
        	applicationContext.getAutowireCapableBeanFactory().autowireBean(rabbitMQMessageHandler);
        	rabbitMQMessageHandler.sendMessage(resourceId);
            logger.info("Unregistration result for " + resourceId);
        } catch (Exception e) {
            logger.error("Fatal error sending data to EXCHANGE_NAME: "+EXCHANGE_NAME+", RESOURCE_UNREGISTRATION_QUEUE_NAME:"+RESOURCE_UNREGISTRATION_QUEUE_NAME, e);
        }
    }

    public void sendResourceUpdateMessage( ResourceBean resourceBean) {
        try {
            logger.info("Sending request for update " + resourceBean.getInternalId());
            RabbitMQMessageHandlerResourceBean rabbitMQMessageHandler = new RabbitMQMessageHandlerResourceBean(EXCHANGE_NAME, RESOURCE_UPDATED_QUEUE_NAME);
        	applicationContext.getAutowireCapableBeanFactory().autowireBean(rabbitMQMessageHandler);
        	rabbitMQMessageHandler.sendMessage(resourceBean);
        } catch (Exception e) {
            logger.error("Fatal error sending data to EXCHANGE_NAME: "+EXCHANGE_NAME+", RESOURCE_UPDATED_QUEUE_NAME:"+RESOURCE_UPDATED_QUEUE_NAME, e);
        }
    }

}
