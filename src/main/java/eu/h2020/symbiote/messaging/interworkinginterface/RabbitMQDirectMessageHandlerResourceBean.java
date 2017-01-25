package eu.h2020.symbiote.messaging.interworkinginterface;

import eu.h2020.symbiote.beans.ResourceBean;
import eu.h2020.symbiote.messaging.rabbitmq.GenericRabbitMQDirectMessageHandler;


/**
 * This class is to communicate with RabbitMQ. Initially created by Elena
 *
 * @author: Elena Garrido
 * @version: 20/01/2017

 */
public class RabbitMQDirectMessageHandlerResourceBean extends GenericRabbitMQDirectMessageHandler<ResourceBean> {

    public RabbitMQDirectMessageHandlerResourceBean(String exchangeName, String requestQueueName) {
		super(exchangeName, requestQueueName, ResourceBean.class);
	}
}
