package eu.h2020.symbiote.messaging.rabbitmq;

import eu.h2020.symbiote.beans.ResourceBean;


/**
 * This class is to communicate with RabbitMQ. Initially created by Elena
 *
 * @author: Elena Garrido
 * @version: 20/01/2017

 */
public class RabbitMQRPCMessageHandlerResourceBean extends GenericRabbitMQRPCMessageHandler<ResourceBean,ResourceBean> {

    public RabbitMQRPCMessageHandlerResourceBean(String requestQueueName, String replyQueueName) {
		super(requestQueueName, replyQueueName, ResourceBean.class);
	}
}
