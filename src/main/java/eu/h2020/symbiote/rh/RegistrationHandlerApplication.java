package eu.h2020.symbiote.rh;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**! \class RegistrationHandlerApplication
 * \brief RegistrationHandlerApplication root class that has to be launched to run the RegistrationHandler with spring boot
 **/

/**
 * This class handles the initialization from the platform. Initially created by jose
 *
 * @author: jose, Elena Garrido
 * @version: 06/10/2016

 */
@EnableDiscoveryClient    //when Eureka available
//@EnableAutoConfiguration
@SpringBootApplication
public class RegistrationHandlerApplication {

	public static void main(String[] args) {
		WaitForPort.waitForServices(System.getenv("SPRING_BOOT_WAIT_FOR_SERVICES"));
		
		SpringApplication.run(RegistrationHandlerApplication.class, args);
    }
}
