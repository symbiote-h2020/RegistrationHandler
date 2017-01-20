package eu.h2020.symbiote.messaging.rabbitmq;

import com.google.gson.Gson;
import com.rabbitmq.client.*;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;


/**
 * This class is to communicate with RabbitMQ. Initially created by mateuszl and updated by Elena
 *
 * @author: mateuszl, Elena Garrido
 * @version: 18/01/2017

 */
@Component
public class RabbitMQMessageHandler {

    private static Log logger = LogFactory.getLog( RabbitMQMessageHandler.class );

    @Value("${symbiote.rabbitmq.host.ip}")
    String rabbitMQHostIP;


    /**
     * Method for sending a message to specified 'queue' on RabbitMQ server. Object is converted to Json.
     *
     * @param queueName
     * @param object
     * @throws Exception
     */
    public void sendMessage(String exchangeName, String routingKey, String queueName, Object object) throws Exception {
        logger.info("START OF sendMessage to queue"+queueName);
        Gson gson = new Gson();
        String objectInJson = gson.toJson(object);

        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(rabbitMQHostIP);
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();
        channel.exchangeDeclare(exchangeName, "direct");
        channel.queueDeclare(queueName, false, false, false, null);

        String message = objectInJson;
        channel.basicPublish(exchangeName, queueName, null, message.getBytes("UTF-8"));

        channel.close();
        connection.close();
        logger.info("END OF sendMessage to queue"+queueName);

    }
    /**
     * Method to receive messages from a specific queue.
     *
     * @param queueName
     * @param receptionListener object that implements the listener
     * @throws Exception
     */

    public String subscribeToRoutingQueue(String exchangeName, String queueName, RabbitMQMessageReceptionListener receptionListener) throws Exception {

        String receivedMessage = "";

        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(rabbitMQHostIP);
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        channel.queueDeclare(queueName, false, false, false, null);
        logger.info("Subscription to "+queueName+ " done. Waiting for messages.");

        Consumer consumer = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body)
                    throws IOException {
                String message = new String(body, "UTF-8");
                receptionListener.onReceivedMessage(message);
                System.out.println(" [x] Received '" + message + "'");
                //TODO use the message
            }
        };
        channel.basicConsume(queueName, true, consumer);

        return receivedMessage;
    }
}
