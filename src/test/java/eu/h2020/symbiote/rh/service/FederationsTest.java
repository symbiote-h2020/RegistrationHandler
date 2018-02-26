package eu.h2020.symbiote.rh.service;

import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.junit4.SpringRunner;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@RunWith(SpringRunner.class)
@SpringBootTest( webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT,
        properties = {
                "eureka.client.enabled=false",
                "spring.cloud.sleuth.enabled=false",
                "reghandler.reader.impl=dummyPlatformInfoReader",
                "reghandler.init.autoregister=false",
                "platform.id=helloid",
                "server.port=18033",
                "symbIoTe.core.cloud.interface.url=http://localhost:18033/testiif",
                "symbIoTe.interworking.interface.url=http://www.example.com/Test1Platform",
                "rabbit.host=localhost",
                "rabbit.username=guest",
                "rabbit.password=guest",
                "symbIoTe.core.interface.url=http://localhost:18033",
                "symbIoTe.component.clientId=reghandler@Test1Platform",
                "symbIoTe.component.username=Test1",
                "symbIoTe.component.password=Test1",
                "symbIoTe.component.keystore.path=keystore.jks",
                "symbIoTe.component.keystore.password=kspw",
                "symbIoTe.component.registry.id=registry",
                "symbIoTe.localaam.url=https://localhost:18033",
                "symbIoTe.targetaam.id=SymbIoTe_Core_AAM",
                "symbIoTe.aam.integration=false",
                "spring.data.mongodb.port=18034",
                //TODO update coreAAM URL value, this was added just to be able to start tests
                "symbIoTe.coreaam.url=http://localhost:18033",
                "spring.data.mongodb.database=symbiote-registration-handler-test",
                "localRegistry.exchange.name="+FederationsTest.REGISTRY_EXCHANGE_TEST_NAME})
//@SpringBootTest( webEnvironment = WebEnvironment.DEFINED_PORT, properties = {"eureka.client.enabled=false", "spring.cloud.sleuth.enabled=false", "platform.id=helloid", "server.port=18033", "symbIoTe.core.cloud.interface.url=http://localhost:18033/testiifnosec", "security.coreAAM.url=http://localhost:18033", "security.rabbitMQ.ip=localhost", "security.enabled=false", "security.user=user", "security.password=password"})
@Configuration
@ComponentScan
@EnableAutoConfiguration
public class FederationsTest {

    public static final String REGISTRY_EXCHANGE_TEST_NAME = "symbIoTe.localRegistryTest";
    public static final String REGISTRY_UPDATE_QUEUE_NAME = "symbIoTe.localRegistryTest.update";
    public static final String REGISTRY_DELETE_QUEUE_NAME = "symbIoTe.localRegistryTest.delete";
    public static final String REGISTRY_SHARE_QUEUE_NAME = "symbIoTe.localRegistryTest.share";
    public static final String REGISTRY_UNSHARE_QUEUE_NAME = "symbIoTe.localRegistryTest.unshare";

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Bean
    private DirectExchange registryTestExchange() {
        return new DirectExchange(REGISTRY_EXCHANGE_TEST_NAME, false, true);
    }

    @Bean
    private Queue updateQueue() {
        return new Queue(REGISTRY_UPDATE_QUEUE_NAME, false, true, true);
    }

    @Bean
    private Queue deleteQueue() {
        return new Queue(REGISTRY_DELETE_QUEUE_NAME, false, true, true);
    }

    @Bean
    private Queue shareQueue() {
        return new Queue(REGISTRY_SHARE_QUEUE_NAME, false, true, true);
    }

    @Bean
    private Queue unshareQueue() {
        return new Queue(REGISTRY_UNSHARE_QUEUE_NAME, false, true, true);
    }

    @Before
    public void setUp() throws Exception {

    }
}
