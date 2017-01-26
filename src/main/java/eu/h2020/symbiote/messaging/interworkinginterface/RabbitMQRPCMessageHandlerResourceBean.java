package eu.h2020.symbiote.messaging.interworkinginterface;

import eu.h2020.symbiote.beans.ResourceBean;
import eu.h2020.symbiote.messaging.rabbitmq.GenericRabbitMQRPCMessageHandler;


/**
 * This class is to communicate with RabbitMQ. Initially created by Elena
 *
 * @author: Elena Garrido
 * @version: 20/01/2017

 */
public class RabbitMQRPCMessageHandlerResourceBean extends GenericRabbitMQRPCMessageHandler<ResourceBean,ResourceBean> {

    public RabbitMQRPCMessageHandlerResourceBean(String excchangeName, String requestQueueName, String replyQueueName) {
		super(excchangeName, requestQueueName, replyQueueName, ResourceBean.class);
	}
}
