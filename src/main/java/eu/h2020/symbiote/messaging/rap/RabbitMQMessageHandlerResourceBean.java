package eu.h2020.symbiote.messaging.rap;

import eu.h2020.symbiote.beans.ResourceBean;
import eu.h2020.symbiote.messaging.rabbitmq.GenericRabbitMQRoutingMessageHandler;


/**
 * This class is to communicate with RabbitMQ. Initially created by Elena
 *
 * @author: Elena Garrido
 * @version: 20/01/2017

 */
public class RabbitMQMessageHandlerResourceBean extends GenericRabbitMQRoutingMessageHandler<ResourceBean> {

    public RabbitMQMessageHandlerResourceBean(String exchangeName, String queueName) {
		super(exchangeName, queueName, ResourceBean.class);
	}
}
