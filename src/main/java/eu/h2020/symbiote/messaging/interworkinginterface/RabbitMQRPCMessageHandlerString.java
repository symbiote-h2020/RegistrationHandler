package eu.h2020.symbiote.messaging.interworkinginterface;

import eu.h2020.symbiote.messaging.rabbitmq.GenericRabbitMQRPCMessageHandler;

/**
 * This class is to communicate with RabbitMQ. Initially created by Elena
 *
 * @author: Elena Garrido
 * @version: 20/01/2017

 */
/**! \class RabbitMQRPCMessageHandlerString
 * \brief This class extends from the \class GenericRabbitMQRPCMessageHandler and will be able to write and read a \class String
 * from the rabbitMQ RPC queues 
 **/

class RabbitMQRPCMessageHandlerString extends GenericRabbitMQRPCMessageHandler<String,String> {

    public RabbitMQRPCMessageHandlerString(String excchangeName, String requestQueueName, String replyQueueName) {
		super(excchangeName, requestQueueName, replyQueueName, String.class);
	}
}
