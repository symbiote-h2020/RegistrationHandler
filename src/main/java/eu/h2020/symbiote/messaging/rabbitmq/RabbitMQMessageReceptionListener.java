package eu.h2020.symbiote.messaging.rabbitmq;

/**
 * Created by s233268 on 18/01/2017.
 */
public interface RabbitMQMessageReceptionListener {
    void onReceivedMessage(String message);
}
