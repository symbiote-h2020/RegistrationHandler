package eu.h2020.symbiote.rh;

import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;


/**! \class RegistrationHandlerApplication
 * \brief RegistrationHandlerApplication root class that has to be launched to run the RegistrationHandler with spring boot
 **/

/**
 * This class handles the initialization from the platform. Initially created by jose
 *
 * @author: jose, Elena Garrido
 * @version: 06/10/2016

 */
//@EnableDiscoveryClient    //when Eureka available
@EnableAutoConfiguration
@SpringBootApplication
public class RegistrationHandlerApplication {

	@Value("${rh.exchange.name}")
	private String exchangeName;

	@Bean
	DirectExchange rhExchange() {
		return new DirectExchange(exchangeName, true, false);
	}

	@Bean
	public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(ConnectionFactory connectionFactory) {
		SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
		factory.setConnectionFactory(connectionFactory);
		factory.setConcurrentConsumers(3);
		factory.setMaxConcurrentConsumers(10);
		factory.setMessageConverter(jackson2JsonMessageConverter());
		return factory;
	}

	@Bean
	Jackson2JsonMessageConverter jackson2JsonMessageConverter() {
		return new Jackson2JsonMessageConverter();
	}

	public static void main(String[] args) {
		SpringApplication.run(RegistrationHandlerApplication.class, args);
    }
}
