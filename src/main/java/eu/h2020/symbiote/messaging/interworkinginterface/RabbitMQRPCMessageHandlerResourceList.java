package eu.h2020.symbiote.messaging.interworkinginterface;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.reflect.TypeToken;

import eu.h2020.symbiote.cloud.model.CloudResource;
import eu.h2020.symbiote.messaging.rabbitmq.GenericRabbitMQRPCMessageHandler;


/**
 * This class is to communicate with RabbitMQ. Initially created by Elena
 *
 * @author: Elena Garrido
 * @version: 20/01/2017

 */
/**! \class RabbitMQRPCMessageHandlerCloudResourceList
 * \brief This class extends from the \class GenericRabbitMQRPCMessageHandler and will be able to write and read a List of \class CloudResource
 * from the rabbitMQ RPC queues 
 **/
class RabbitMQRPCMessageHandlerResourceList extends GenericRabbitMQRPCMessageHandler<List<CloudResource>,List<CloudResource>> {

    public RabbitMQRPCMessageHandlerResourceList(String excchangeName, String requestQueueName, String replyQueueName) {
		super(excchangeName, requestQueueName, replyQueueName, new TypeToken<ArrayList<CloudResource>>(){}.getType());
	}
}
