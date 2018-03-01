package eu.h2020.symbiote.rh.service;

import eu.h2020.symbiote.client.RegistrationHandlerClient;
import eu.h2020.symbiote.client.SymbioteComponentClientFactory;
import eu.h2020.symbiote.cloud.model.internal.CloudResource;
import eu.h2020.symbiote.rh.db.ResourceRepository;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
                //TODO update coreAAM URL value, this was added just to be able to start tests
                "symbIoTe.coreaam.url=http://localhost:18033",
                "spring.data.mongodb.database=symbiote-registration-handler-test",
                "localRegistry.exchange.name="+RabbitConfiguration.REGISTRY_EXCHANGE_TEST_NAME})
//@SpringBootTest( webEnvironment = WebEnvironment.DEFINED_PORT, properties = {"eureka.client.enabled=false", "spring.cloud.sleuth.enabled=false", "platform.id=helloid", "server.port=18033", "symbIoTe.core.cloud.interface.url=http://localhost:18033/testiifnosec", "security.coreAAM.url=http://localhost:18033", "security.rabbitMQ.ip=localhost", "security.enabled=false", "security.user=user", "security.password=password"})
@Configuration
@ComponentScan
@EnableAutoConfiguration
public class FederationsTest {

    public static final int NUM_TEST_RESOURCES = 4;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private ResourceRepository resourceRepository;

    private RegistrationHandlerClient regHandlerClient;

    @Before
    public void setUp() throws Exception {
        regHandlerClient = SymbioteComponentClientFactory.createClient("http://localhost:18033",
                RegistrationHandlerClient.class, null);
        resourceRepository.deleteAll();
    }

    private void testResourceList(List<CloudResource> result, List<CloudResource> test) {
        assert result.size() == test.size();
        for (CloudResource resource : result) {
            assert test.stream().filter(res -> res.getInternalId().equals(resource.getInternalId())).findAny().isPresent();
        }
    }


    @Test
    public void testFederations() {
        List<CloudResource> testResources = new ArrayList<>();
        for (int i=0; i < NUM_TEST_RESOURCES; i++ ) {
            testResources.add(TestUtils.createTestCloudResource("internal"+i));
        }

        List<CloudResource> registered = regHandlerClient.addLocalResources(testResources);
        testResourceList(registered, testResources);
        testResourceList(registered, resourceRepository.findAll());

        for (CloudResource resource : registered) {
            resource.getFederationInfo().isEmpty();
        }
    }
}
