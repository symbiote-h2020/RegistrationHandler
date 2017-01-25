package eu.h2020.symbiote.messaging.interworkinginterface;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import eu.h2020.symbiote.beans.PlatformBean;

/**

 * This class invoke the RAbbitMQMessageHandler using the right message queue depending on the operation that is being
 * done with the data
 * @author: Elena Garrido
 * @version: 18/01/2017

 */
@Component
public class IFPlatformMessageHandler {

    private static String EXCHANGE_NAME = "symbIoTe.InterworkingInterface";
    private static String PLATFORM_REGISTRATION_ROUTING_KEY = "symbIoTe.InterworkingInterface.registrationHandler.register_platforms";
    private static String PLATFORM_REGISTRATION_ROUTING_KEY_REPLY = PLATFORM_REGISTRATION_ROUTING_KEY+".reply";
    private static String PLATFORM_UNREGISTRATION_ROUTING_KEY = "symbIoTe.InterworkingInterface.registrationHandler.unregister_platforms";
    private static String PLATFORM_UNREGISTRATION_ROUTING_KEY_REPLY = PLATFORM_UNREGISTRATION_ROUTING_KEY+".reply";
    private static String PLATFORM_UPDATED_ROUTING_KEY = "symbIoTe.InterworkingInterface.registrationHandler.update_platforms";
    private static String PLATFORM_UPDATED_ROUTING_KEY_REPLY = PLATFORM_UPDATED_ROUTING_KEY+".reply";


    private static Log logger = LogFactory.getLog(IFPlatformMessageHandler.class);
    
	@Autowired
	private ApplicationContext applicationContext;

    public PlatformBean sendPlatformRegistrationMessage( PlatformBean platfromBean) {
    	 
    	try {
            logger.info("Sending request for registration " + platfromBean.getInternalId());
            RabbitMQRPCMessageHandlerPlatformBean rabbitMQMessageHandler = new RabbitMQRPCMessageHandlerPlatformBean(PLATFORM_REGISTRATION_ROUTING_KEY, PLATFORM_REGISTRATION_ROUTING_KEY_REPLY);
        	applicationContext.getAutowireCapableBeanFactory().autowireBean(rabbitMQMessageHandler);
        	rabbitMQMessageHandler.connect();
        	PlatformBean platformBeanResult = rabbitMQMessageHandler.sendMessage(platfromBean);
            rabbitMQMessageHandler.close();
            logger.info("Sending result for " + platfromBean.getInternalId()+ " --> symbioteId:"+platfromBean.getSymbioteId());
            return platformBeanResult;
        } catch (Exception e) {
            logger.error("Fatal error sending data to EXCHANGE_NAME: "+EXCHANGE_NAME+", RESOURCE_REGISTRATION_ROUTING_KEY:"+PLATFORM_REGISTRATION_ROUTING_KEY+", RESOURCE_REGISTRATION_ROUTING_KEY_REPLY:"+PLATFORM_REGISTRATION_ROUTING_KEY_REPLY, e);
        }
        return null;
    }

    public String sendPlatformUnregistrationMessage( String platformId) {
        try {
            logger.info("Sending request for unregistration " + platformId);
            RabbitMQRPCMessageHandlerString rabbitMQMessageHandler = new RabbitMQRPCMessageHandlerString(PLATFORM_UNREGISTRATION_ROUTING_KEY, PLATFORM_UNREGISTRATION_ROUTING_KEY_REPLY);
        	applicationContext.getAutowireCapableBeanFactory().autowireBean(rabbitMQMessageHandler);
        	rabbitMQMessageHandler.connect();
        	String resourceIdResult = rabbitMQMessageHandler.sendMessage(platformId);
            rabbitMQMessageHandler.close();
            logger.info("Unregistration result for " + platformId);
            return resourceIdResult;
        } catch (Exception e) {
            logger.error("Fatal error sending data to EXCHANGE_NAME: "+EXCHANGE_NAME+", RESOURCE_UNREGISTRATION_ROUTING_KEY:"+PLATFORM_UNREGISTRATION_ROUTING_KEY+", RESOURCE_UNREGISTRATION_ROUTING_KEY_REPLY:"+PLATFORM_UNREGISTRATION_ROUTING_KEY_REPLY, e);
        }
        return null;
    }

    public PlatformBean sendPlatformUpdateMessage( PlatformBean platfromBean) {
        try {
            logger.info("Sending request for update " + platfromBean.getInternalId());
            RabbitMQRPCMessageHandlerPlatformBean rabbitMQMessageHandler = new RabbitMQRPCMessageHandlerPlatformBean(PLATFORM_UPDATED_ROUTING_KEY, PLATFORM_UPDATED_ROUTING_KEY);
        	applicationContext.getAutowireCapableBeanFactory().autowireBean(rabbitMQMessageHandler);
        	rabbitMQMessageHandler.connect();
        	PlatformBean platfromBeanResult = rabbitMQMessageHandler.sendMessage(platfromBean);
            rabbitMQMessageHandler.close();
            logger.info("Update result for " + platfromBean.getInternalId()+ " --> symbioteId:"+platfromBeanResult.getSymbioteId());
            return platfromBeanResult;
        } catch (Exception e) {
            logger.error("Fatal error sending data to EXCHANGE_NAME: "+EXCHANGE_NAME+", RESOURCE_UPDATED_ROUTING_KEY:"+PLATFORM_UPDATED_ROUTING_KEY+", RESOURCE_UPDATED_ROUTING_KEY_REPLY:"+PLATFORM_UPDATED_ROUTING_KEY_REPLY, e);
        }
        return null;
    }

}
