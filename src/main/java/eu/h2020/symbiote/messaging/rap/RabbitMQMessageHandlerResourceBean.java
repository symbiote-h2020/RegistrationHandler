package eu.h2020.symbiote.messaging.rap;

import eu.h2020.symbiote.cloud.model.CloudResource;
import eu.h2020.symbiote.messaging.rabbitmq.GenericRabbitMQFanoutMessageHandler;


/**
 * This class is to communicate with RabbitMQ. Initially created by Elena
 *
 * @author: Elena Garrido
 * @version: 20/01/2017

 */
/**! \class RabbitMQMessageHandlerResourceBean
 * \brief This class extends from the \class GenericRabbitMQRoutingMessageHandler and will be able to write and read a \class ResourceBean
 * from the rabbitMQ routing queues 
 **/

class RabbitMQMessageHandlerResourceBean extends GenericRabbitMQFanoutMessageHandler<CloudResource> {

    public RabbitMQMessageHandlerResourceBean(String exchangeName, String queueName) {
		super(exchangeName, queueName, CloudResource.class);
	}
}
