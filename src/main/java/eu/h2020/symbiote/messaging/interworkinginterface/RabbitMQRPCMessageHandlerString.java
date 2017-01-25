package eu.h2020.symbiote.messaging.interworkinginterface;

import eu.h2020.symbiote.messaging.rabbitmq.GenericRabbitMQRPCMessageHandler;

/**
 * This class is to communicate with RabbitMQ. Initially created by Elena
 *
 * @author: Elena Garrido
 * @version: 20/01/2017

 */
public class RabbitMQRPCMessageHandlerString extends GenericRabbitMQRPCMessageHandler<String,String> {

    public RabbitMQRPCMessageHandlerString(String exchangeName, String requestQueueName, String replyQueueName) {
		super(exchangeName, requestQueueName, replyQueueName, String.class);
	}
}
