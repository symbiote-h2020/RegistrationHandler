package eu.h2020.symbiote.rh.service;

import eu.h2020.symbiote.client.RegistrationHandlerClient;
import eu.h2020.symbiote.client.SymbioteComponentClientFactory;
import eu.h2020.symbiote.cloud.model.internal.CloudResource;
import eu.h2020.symbiote.cloud.trust.model.TrustEntry;
import eu.h2020.symbiote.rh.db.ResourceRepository;
import eu.h2020.symbiote.util.RabbitConstants;
import org.apache.commons.collections4.Factory;
import org.apache.commons.collections4.FactoryUtils;
import org.apache.commons.collections4.map.LazyMap;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@RunWith(SpringRunner.class)
@SpringBootTest( webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT, properties = "server.port=18034")
//@SpringBootTest( webEnvironment = WebEnvironment.DEFINED_PORT, properties = {"eureka.client.enabled=false", "spring.cloud.sleuth.enabled=false", "platform.id=helloid", "server.port=18033", "symbIoTe.core.cloud.interface.url=http://localhost:18033/testiifnosec", "security.coreAAM.url=http://localhost:18033", "security.rabbitMQ.ip=localhost", "security.enabled=false", "security.user=user", "security.password=password"})
@Configuration
@ComponentScan
@TestPropertySource(
        locations = "classpath:test.properties")
public class FederationsTest {

    public static final int NUM_TEST_RESOURCES = 4;
    private static final int NUM_FEDERATIONS = 2;

    public static final String RES_PF = "internal";
    public static final String FED_PF = "federation";

    @Value("${" + RabbitConstants.EXCHANGE_TRUST_NAME_PROPERTY + "}")
    private String exchangeTrustName;

    @Value("${" + RabbitConstants.ROUTING_KEY_TRUST_RESOURCE_UPDATED + "}")
    private String resourceTrustUpdatedKey;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private ResourceRepository resourceRepository;

    private RegistrationHandlerClient regHandlerClient;

    @Before
    public void setUp() throws Exception {
        regHandlerClient = SymbioteComponentClientFactory.createClient("http://localhost:18034",
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
            testResources.add(TestUtils.getTestActuatorBean(RES_PF +i, "Act"+i));
        }

        List<CloudResource> registered = regHandlerClient.addLocalResources(testResources);
        testResourceList(registered, testResources);
        testResourceList(registered, resourceRepository.findAll());

        for (CloudResource resource : registered) {
            assert resource.getFederationInfo() != null;
            assert resource.getFederationInfo().getAggregationId() != null;
            assert resource.getFederationInfo().getSharingInformation().isEmpty();
        }

        List<String> fedIds = new ArrayList<>();
        for (int i=0; i < NUM_FEDERATIONS; i++) {
            fedIds.add(FED_PF + i);
        }

        Map<String, Map<String, Boolean>> sharingMap = new HashMap<>();

        for (int i=0; i < NUM_FEDERATIONS; i++) {
            String fedId = FED_PF+i;
            sharingMap.put(fedId, new HashMap<>());
            for (int j=0; j < NUM_TEST_RESOURCES; j++) {
                if ((j+1)%(i+1) == 0) {
                    Boolean bartered = j % 2 == 0;
                    sharingMap.get(fedId).put(RES_PF + j, bartered);
                }
            }
        }

        Map<String, List<CloudResource>> sharedResources = regHandlerClient.shareResources(sharingMap);
        for (Map.Entry<String, List<CloudResource>> entry : sharedResources.entrySet()) {
            int i = new Integer(entry.getKey().substring(FED_PF.length()));
            for (CloudResource resource : entry.getValue()) {
                assert resource.getFederationInfo().getSharingInformation().containsKey(entry.getKey());
                int j = new Integer(resource.getInternalId().substring(RES_PF.length()));
                assert (j+1)%(i+1) == 0;
                assert (j%2 == 0) == resource.getFederationInfo().getSharingInformation().get(entry.getKey()).getBartering();

                CloudResource savedResource = resourceRepository.getByInternalId(resource.getInternalId());
                assert savedResource != null;
                assert savedResource.getFederationInfo().getSharingInformation().keySet().size() == resource.getFederationInfo().getSharingInformation().size();
                assert savedResource.getFederationInfo().getSharingInformation().keySet().containsAll(resource.getFederationInfo().getSharingInformation().keySet());
            }
        }

        Map<String, List<String>> unshareMap = LazyMap.lazyMap(new HashMap<String, List<String>>(), new Factory<List<String>>() {
            @Override
            public List<String> create() {
                return new ArrayList<>();
            }
        });

        List<CloudResource> allResources = resourceRepository.findAll();
        for (CloudResource resource : allResources) {
            Set<String> fedKeys = resource.getFederationInfo().getSharingInformation().keySet();
            if (fedKeys.size() > 0) {
                String toRemove = fedKeys.iterator().next();
                List<String> fedShare = unshareMap.get(toRemove);
                fedShare.add(resource.getInternalId());
            }
        }

        Map<String, List<CloudResource>> unshared = regHandlerClient.unshareResources(unshareMap);

        for (Map.Entry<String, List<CloudResource>> entry : unshared.entrySet()) {
            for (CloudResource resource : entry.getValue()) {
                CloudResource found = resourceRepository.getByInternalId(resource.getInternalId());
                assert !found.getFederationInfo().getSharingInformation().containsKey(entry.getKey());
            }
        }

        List<String> toRemove = resourceRepository.findAll().stream().map(resource -> resource.getInternalId())
                .collect(Collectors.toList());
        List<String> removed = regHandlerClient.removeLocalResources(toRemove);

        assert removed.size() == toRemove.size();
        assert removed.containsAll(toRemove);
        assert toRemove.containsAll(removed);
        assert resourceRepository.findAll().isEmpty();

    }

    @Test
    public void testTrust() throws InterruptedException {
        resourceRepository.deleteAll();
        String resId = RES_PF+"_trust";
        double trustValue = 80.0;

        TrustEntry entry = new TrustEntry();
        entry.setValue(trustValue);
        entry.setResourceId(resId);

        rabbitTemplate.convertAndSend(exchangeTrustName, resourceTrustUpdatedKey, entry);

        CloudResource testResource = resourceRepository.getByInternalId(resId);
        assert  testResource == null;

        testResource = TestUtils.getTestActuatorBean(RES_PF+"_trust", "ActuatorTrust");

        regHandlerClient.addLocalResources(Arrays.asList(testResource));

        testResource = resourceRepository.getByInternalId(resId);

        assert testResource != null;
        assert testResource.getFederationInfo().getSharingInformation().isEmpty();

        Map<String, Map<String, Boolean>> sharingMap = new HashMap<>();
        Map<String, Boolean> resMap = new HashMap<>();
        resMap.put(resId, false);
        sharingMap.put(FED_PF+"1", resMap);
        regHandlerClient.shareResources(sharingMap);

        testResource = resourceRepository.getByInternalId(resId);

        assert testResource != null;
        assert testResource.getFederationInfo() != null;
        assert testResource.getFederationInfo().getResourceTrust() == null;

        rabbitTemplate.convertAndSend(exchangeTrustName, resourceTrustUpdatedKey, entry);

        //Wait for message to arrive and value to be updated
        TimeUnit.SECONDS.sleep(2);

        testResource = resourceRepository.getByInternalId(resId);

        assert testResource != null;
        assert testResource.getFederationInfo() != null;
        assert testResource.getFederationInfo().getResourceTrust() == trustValue;
    }
}
