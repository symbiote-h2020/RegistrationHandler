package eu.h2020.symbiote.rh;

import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.FanoutExchange;
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

	public static void main(String[] args) {
		SpringApplication.run(RegistrationHandlerApplication.class, args);
    }
}
