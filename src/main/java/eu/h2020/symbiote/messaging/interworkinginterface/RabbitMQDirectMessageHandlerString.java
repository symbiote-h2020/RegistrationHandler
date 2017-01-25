package eu.h2020.symbiote.messaging.interworkinginterface;

import eu.h2020.symbiote.messaging.rabbitmq.GenericRabbitMQDirectMessageHandler;

/**
 * This class is to communicate with RabbitMQ. Initially created by Elena
 *
 * @author: Elena Garrido
 * @version: 20/01/2017

 */
public class RabbitMQDirectMessageHandlerString extends GenericRabbitMQDirectMessageHandler<String> {

    public RabbitMQDirectMessageHandlerString(String exchangeName, String requestQueueName) {
		super(exchangeName, requestQueueName, String.class);
	}
}
