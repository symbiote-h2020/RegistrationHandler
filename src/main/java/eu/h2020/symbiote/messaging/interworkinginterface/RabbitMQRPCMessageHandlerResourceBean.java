package eu.h2020.symbiote.messaging.interworkinginterface;

import eu.h2020.symbiote.cloud.model.CloudResource;
import eu.h2020.symbiote.messaging.rabbitmq.GenericRabbitMQRPCMessageHandler;


/**
 * This class is to communicate with RabbitMQ. Initially created by Elena
 *
 * @author: Elena Garrido
 * @version: 20/01/2017

 */
/**! \class RabbitMQRPCMessageHandlerResourceBean
 * \brief This class extends from the \class GenericRabbitMQRPCMessageHandler and will be able to write and read a \class ResourceBean
 * from the rabbitMQ RPC queues 
 **/
class RabbitMQRPCMessageHandlerResourceBean extends GenericRabbitMQRPCMessageHandler<CloudResource,CloudResource> {

    public RabbitMQRPCMessageHandlerResourceBean(String excchangeName, String requestQueueName, String replyQueueName) {
		super(excchangeName, requestQueueName, replyQueueName, CloudResource.class);
	}
}
