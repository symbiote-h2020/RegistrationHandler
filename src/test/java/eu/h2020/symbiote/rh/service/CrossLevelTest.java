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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@RunWith(SpringRunner.class)
@SpringBootTest( webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT, properties = {
        "server.port=18035",
        "symbIoTe.core.cloud.interface.url=http://localhost:18035/testiif"})
//@SpringBootTest( webEnvironment = WebEnvironment.DEFINED_PORT, properties = {"eureka.client.enabled=false", "spring.cloud.sleuth.enabled=false", "platform.id=helloid", "server.port=18033", "symbIoTe.core.cloud.interface.url=http://localhost:18033/testiifnosec", "security.coreAAM.url=http://localhost:18033", "security.rabbitMQ.ip=localhost", "security.enabled=false", "security.user=user", "security.password=password"})
@Configuration
@ComponentScan
@TestPropertySource(
        locations = "classpath:test.properties")
public class CrossLevelTest {

    public static final int NUM_RESOURCES = 12;
    public static final int L1_RESOURCES = (2 * NUM_RESOURCES) / 3;

    @Autowired
    private ResourceRepository resourceRepository;

    private RegistrationHandlerClient regHandlerClient;

    @Before
    public void setUp() throws Exception {
        regHandlerClient = SymbioteComponentClientFactory.createClient("http://localhost:18035",
                RegistrationHandlerClient.class, null);
        resourceRepository.deleteAll();
    }

    @Test
    public void testCrossLevel() {
        List<CloudResource> l1Resources = new ArrayList<>();
        List<CloudResource> l2Resources = new ArrayList<>();

        for (int i=0; i < NUM_RESOURCES; i++) {
            CloudResource resource = TestUtils.getTestActuatorBean(Integer.toString(i), "Act"+i);

            if (i < L1_RESOURCES) {
                l1Resources.add(resource);
            }

            if (i > NUM_RESOURCES / 3) {
                l2Resources.add(resource);
            }
        }

        regHandlerClient.addResources(l1Resources);

        List<CloudResource> existing = resourceRepository.findAll();
        assert  existing.size() == l1Resources.size();

        existing.forEach(resource -> {

            assert resource.getInternalId() != null;
            assert Integer.parseInt(resource.getInternalId()) < L1_RESOURCES;
            assert resource.getResource() != null;
            assert resource.getResource().getId() != null;

            assert resource.getFederationInfo() == null;

        });

        regHandlerClient.addLocalResources(l2Resources);

        existing = resourceRepository.findAll();
        assert existing.size() == NUM_RESOURCES;

        existing.forEach(resource -> {

            assert resource.getInternalId() != null;
            assert Integer.parseInt(resource.getInternalId()) < NUM_RESOURCES;
            assert resource.getResource() != null;

            int resId = Integer.parseInt(resource.getInternalId());

            if (resId < L1_RESOURCES) {
                assert resource.getResource() != null;
                assert resource.getResource().getId() != null;
            }

            if (resId > NUM_RESOURCES / 3) {
                assert resource.getFederationInfo() != null;
                assert resource.getFederationInfo().getSymbioteId() != null;
            }

        });

        regHandlerClient.deleteResources(l1Resources.stream().map(resource -> resource.getInternalId())
                .collect(Collectors.toList()));

        existing = resourceRepository.findAll();
        assert existing.size() == l2Resources.size();

        resourceRepository.findAll().forEach(resource -> {

            assert resource.getInternalId() != null;
            assert Integer.parseInt(resource.getInternalId()) < NUM_RESOURCES;
            assert resource.getResource() != null;

            int resId = Integer.parseInt(resource.getInternalId());

            assert resId > NUM_RESOURCES /3;

            assert resource.getFederationInfo() != null;
            assert resource.getFederationInfo().getSymbioteId() != null;

        });

        regHandlerClient.removeLocalResources(l2Resources.stream().map(resource -> resource.getInternalId())
                .collect(Collectors.toList()));

        existing = resourceRepository.findAll();
        assert existing.isEmpty();
    }

}
