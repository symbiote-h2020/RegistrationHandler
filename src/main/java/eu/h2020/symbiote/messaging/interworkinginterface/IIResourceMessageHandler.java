package eu.h2020.symbiote.messaging.interworkinginterface;

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

    public CloudResource sendResourceRegistrationMessage(CloudResource resourceBean) {
        try {
            logger.info("Sending request for registration resource with internal id " + resourceBean.getInternalId());
            RabbitMQRPCMessageHandlerResourceBean rabbitMQMessageHandler = new RabbitMQRPCMessageHandlerResourceBean(EXCHANGE_NAME, RESOURCE_REGISTRATION_ROUTING_KEY, RESOURCE_REGISTRATION_ROUTING_KEY_REPLY);
        	applicationContext.getAutowireCapableBeanFactory().autowireBean(rabbitMQMessageHandler);
        	rabbitMQMessageHandler.connect();
        	String internalId = resourceBean.getInternalId();
        	resourceBean.setInternalId("");
        	CloudResource resourceBeanResult = rabbitMQMessageHandler.sendMessage(resourceBean);
        	resourceBeanResult.setInternalId(internalId);
            rabbitMQMessageHandler.close();
            logger.info("Sending result for resource with internal id " + resourceBeanResult.getInternalId()+ " --> symbioteId:"+resourceBeanResult.getId());
            return resourceBeanResult;
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

    public CloudResource sendResourceUpdateMessage(CloudResource resourceBean) {
        try {
            logger.info("Sending request for update resource with internal id " + resourceBean.getInternalId());
            RabbitMQRPCMessageHandlerResourceBean rabbitMQMessageHandler = new RabbitMQRPCMessageHandlerResourceBean(EXCHANGE_NAME, RESOURCE_UPDATED_ROUTING_KEY, RESOURCE_UPDATED_ROUTING_KEY_REPLY);
        	applicationContext.getAutowireCapableBeanFactory().autowireBean(rabbitMQMessageHandler);
        	rabbitMQMessageHandler.connect();
        	String internalId = resourceBean.getInternalId();
        	resourceBean.setInternalId("");       	
        	CloudResource resourceBeanResult = rabbitMQMessageHandler.sendMessage(resourceBean);
        	resourceBeanResult.setInternalId(internalId);        	
            rabbitMQMessageHandler.close();
            logger.info("Update result for resource with internal id " + resourceBeanResult.getInternalId()+ " --> symbioteId:"+resourceBeanResult.getId());
            return resourceBeanResult;
        } catch (Exception e) {
            logger.error("Fatal error sending data to EXCHANGE_NAME: "+EXCHANGE_NAME+", RESOURCE_UPDATED_ROUTING_KEY:"+RESOURCE_UPDATED_ROUTING_KEY+", RESOURCE_UPDATED_ROUTING_KEY_REPLY:"+RESOURCE_UPDATED_ROUTING_KEY_REPLY, e);
        }
        return null;
    }

}
