package eu.h2020.symbiote.messaging.rabbitmq;

/**
 * This class is to communicate with RabbitMQ. Initially created by Elena
 *
 * @author: Elena Garrido
 * @version: 20/01/2017

 */
public class RabbitMQRPCMessageHandlerString extends GenericRabbitMQRPCMessageHandler<String,String> {

    public RabbitMQRPCMessageHandlerString(String requestQueueName, String replyQueueName) {
		super(requestQueueName, replyQueueName, String.class);
		// TODO Auto-generated constructor stub
	}
}
