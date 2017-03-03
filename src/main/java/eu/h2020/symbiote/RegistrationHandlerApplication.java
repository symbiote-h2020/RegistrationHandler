package eu.h2020.symbiote;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import feign.Feign;
import feign.gson.GsonDecoder;
import feign.gson.GsonEncoder;


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
//@EnableAutoConfiguration
@SpringBootApplication
public class RegistrationHandlerApplication {

    public static <T> T createFeignClient(Class<T> client, String baseUrl) {
        return Feign.builder().
                encoder(new GsonEncoder()).decoder(new GsonDecoder()).
                target(client,baseUrl);
    }

	public static void main(String[] args) {
		SpringApplication.run(RegistrationHandlerApplication.class, args);
    }
}
