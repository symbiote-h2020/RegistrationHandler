package eu.h2020.symbiote.messaging.rap;

import eu.h2020.symbiote.beans.ResourceBean;
import eu.h2020.symbiote.messaging.rabbitmq.GenericRabbitMQMessageHandler;


/**
 * This class is to communicate with RabbitMQ. Initially created by Elena
 *
 * @author: Elena Garrido
 * @version: 20/01/2017

 */
public class RabbitMQMessageHandlerResourceBean extends GenericRabbitMQMessageHandler<ResourceBean> {

    public RabbitMQMessageHandlerResourceBean(String exchangeName, String queueName) {
		super(exchangeName, queueName, ResourceBean.class);
	}
}
