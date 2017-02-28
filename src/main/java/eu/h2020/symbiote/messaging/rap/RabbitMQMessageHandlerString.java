package eu.h2020.symbiote.messaging.rap;

import eu.h2020.symbiote.messaging.rabbitmq.GenericRabbitMQFanoutMessageHandler;


/**
 * This class is to communicate with RabbitMQ. Initially created by Elena
 *
 * @author: Elena Garrido
 * @version: 20/01/2017

 */
/**! \class RabbitMQMessageHandlerString
 * \brief This class extends from the \class GenericRabbitMQRoutingMessageHandler and will be able to write and read a \class String 
 * from the rabbitMQ routing queues 
 **/
class RabbitMQMessageHandlerString extends GenericRabbitMQFanoutMessageHandler<String> {

    public RabbitMQMessageHandlerString(String exchangeName, String queueName) {
		super(exchangeName, queueName, String.class);
	}
}
