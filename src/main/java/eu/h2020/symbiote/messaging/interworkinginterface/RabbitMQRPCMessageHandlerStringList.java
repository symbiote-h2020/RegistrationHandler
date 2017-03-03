package eu.h2020.symbiote.messaging.interworkinginterface;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.reflect.TypeToken;

import eu.h2020.symbiote.messaging.rabbitmq.GenericRabbitMQRPCMessageHandler;


/**
 * This class is to communicate with RabbitMQ. Initially created by Elena
 *
 * @author: Elena Garrido
 * @version: 20/01/2017

 */
/**! \class RabbitMQRPCMessageHandlerStringList
 * \brief This class extends from the \class GenericRabbitMQRPCMessageHandler and will be able to write and read a \class List of \class String
 * from the rabbitMQ RPC queues 
 **/
class RabbitMQRPCMessageHandlerStringList extends GenericRabbitMQRPCMessageHandler<List<String>,List<String>> {

    public RabbitMQRPCMessageHandlerStringList(String excchangeName, String requestQueueName, String replyQueueName) {
		super(excchangeName, requestQueueName, replyQueueName, new TypeToken<ArrayList<String>>(){}.getType());
	}
}
