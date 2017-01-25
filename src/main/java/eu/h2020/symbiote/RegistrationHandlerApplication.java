package eu.h2020.symbiote;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

import feign.Feign;
import feign.gson.GsonDecoder;
import feign.gson.GsonEncoder;


@EnableDiscoveryClient    //when Eureka available
@EnableAutoConfiguration
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
