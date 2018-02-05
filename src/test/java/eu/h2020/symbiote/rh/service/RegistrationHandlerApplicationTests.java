package eu.h2020.symbiote.rh.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import eu.h2020.symbiote.cloud.model.CloudResourceParams;
import eu.h2020.symbiote.cloud.model.internal.CloudResource;
import eu.h2020.symbiote.cloud.model.internal.RdfCloudResourceList;
import eu.h2020.symbiote.core.internal.RDFFormat;
import eu.h2020.symbiote.core.internal.RDFInfo;
import eu.h2020.symbiote.model.cim.Actuator;
import eu.h2020.symbiote.model.cim.WKTLocation;
import eu.h2020.symbiote.rh.db.ResourceRepository;
import eu.h2020.symbiote.security.accesspolicies.common.AccessPolicyType;
import eu.h2020.symbiote.security.accesspolicies.common.IAccessPolicySpecifier;
import eu.h2020.symbiote.security.accesspolicies.common.singletoken.SingleTokenAccessPolicySpecifier;
import eu.h2020.symbiote.security.commons.exceptions.custom.InvalidArgumentsException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.web.client.AsyncRestTemplate;
import org.springframework.web.context.WebApplicationContext;

import java.util.*;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;


@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@RunWith(SpringRunner.class)
@SpringBootTest( webEnvironment = WebEnvironment.DEFINED_PORT,
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
				"symbIoTe.coreaam.url=http://localhost:18033"})
//@SpringBootTest( webEnvironment = WebEnvironment.DEFINED_PORT, properties = {"eureka.client.enabled=false", "spring.cloud.sleuth.enabled=false", "platform.id=helloid", "server.port=18033", "symbIoTe.core.cloud.interface.url=http://localhost:18033/testiifnosec", "security.coreAAM.url=http://localhost:18033", "security.rabbitMQ.ip=localhost", "security.enabled=false", "security.user=user", "security.password=password"})
@Configuration
@ComponentScan
@EnableAutoConfiguration
public class RegistrationHandlerApplicationTests {
	static final String INTERNAL_ID = "testPurposeResourceId1";

	@Autowired
	private WebApplicationContext webApplicationContext;

    static private MockMvc mockMvc;

  
  @Autowired
  private ResourceRepository resourceRepository;

	private static final Log logger = LogFactory.getLog(RegistrationHandlerApplicationTests.class);

   String uri;
   @Autowired
   Environment environment;
   @Autowired
   ConfigurableApplicationContext context;
   @Before
    public void setUp() throws Exception {
		AsyncRestTemplate asyncRestTemplate = new AsyncRestTemplate();
		mockMvc = webAppContextSetup(webApplicationContext).build();
		MockRestServiceServer.createServer(asyncRestTemplate);
	}

	private CloudResource createTestCloudResource(String internalId) {
   	CloudResource resource = new CloudResource();
   	resource.setInternalId(internalId);
   	resource.setPluginId("plugin_"+internalId);
   	resource.setCloudMonitoringHost("monitoring_"+internalId);
		try {
			IAccessPolicySpecifier testPolicy = new SingleTokenAccessPolicySpecifier(
					AccessPolicyType.PUBLIC, null
			);
			resource.setAccessPolicy(testPolicy);
		} catch (InvalidArgumentsException e) {
			e.printStackTrace();
		}
		CloudResourceParams params = new CloudResourceParams();
		params.setType("Actuator");
		resource.setParams(params);
		
		return resource;
	}

	private CloudResource getTestActuatorBean(){
	   Actuator actuator = new Actuator();
	   WKTLocation location = new WKTLocation();
	   location.setValue("location");
	   actuator.setName("Act1");
	   actuator.setInterworkingServiceURL("http://example.com/url");
	   actuator.setDescription(Arrays.asList("Desc"));

	   CloudResource cloudResource = createTestCloudResource(INTERNAL_ID);
	   cloudResource.setResource(actuator);	   
	   
	   return cloudResource; 
	}

	private CloudResource getTestActuatorBeanInvalid(){
	   Actuator actuator = new Actuator();
	   WKTLocation location = new WKTLocation();
	   location.setValue("location");
	   actuator.setName("invalid");
	   actuator.setInterworkingServiceURL("http://example.com/url");
	   actuator.setDescription(Arrays.asList("Desc"));

	   CloudResource cloudResource = createTestCloudResource(INTERNAL_ID+1);
	   cloudResource.setResource(actuator);	   
	   
	   return cloudResource; 
	}

	@Test
	public void testCreateResource() throws Exception {
		CloudResource cloudResource = getTestActuatorBean();
        ObjectMapper mapper = new ObjectMapper();
        String objectInJson = mapper.writeValueAsString(cloudResource);
		 
        RequestBuilder requestBuilder = post("/resource")
        		.content(objectInJson)
        		.accept(MediaType.APPLICATION_JSON)
        		.contentType(MediaType.APPLICATION_JSON);
        mockMvc.perform(requestBuilder)
                 .andExpect(status().isOk())
                 .andReturn();
        
        requestBuilder = get("/resources")
						.accept(MediaType.APPLICATION_JSON);
        
        String strResponse = mockMvc.perform(requestBuilder)
						.andExpect(status().isOk())
						.andReturn().getResponse().getContentAsString();
        List<CloudResource> body = mapper.readValue(strResponse,
						new TypeReference<List<CloudResource>>(){});
		
        
        assert(!body.isEmpty());
        
        requestBuilder = get("/resource?resourceInternalId="+INTERNAL_ID)
						.accept(MediaType.APPLICATION_JSON);
				strResponse = mockMvc.perform(requestBuilder)
														 .andExpect(status().isOk())
														 .andReturn().getResponse().getContentAsString();
        CloudResource resource = mapper.readValue(strResponse, CloudResource.class);
        
        assert(resource != null);
        assert(INTERNAL_ID.equals(resource.getInternalId()));
        assert(resource.getResource() != null);
        assert(resource.getResource().getId() != null);
        
        logger.info("End of test ----------------------------- testCreateResource");
        assert(true);
	}
	
	@Test
	public void testCreateRdfResources() throws Exception {
	  
	  //TODO: Investigate a way to test without MongoDB connection
    resourceRepository.delete("internal1");
    resourceRepository.delete("internal2");
    resourceRepository.delete("internal3");
	  
	  Map<String, String> expected = new HashMap<>();
	  expected.put("internal1", "service1234");
	  expected.put("internal2", "sensor1");
	  expected.put("internal3", "actuator1");
	  
		Map<String, CloudResource> mapping = new HashMap<>();
		
		mapping.put("http://www.testcompany.eu/customPlatform/service1234", createTestCloudResource("internal1"));
		mapping.put("http://www.testcompany.eu/customPlatform/sensor1", createTestCloudResource("internal2"));
		mapping.put("http://www.testcompany.eu/customPlatform/actuator1", createTestCloudResource("internal3"));
		
		RdfCloudResourceList list = new RdfCloudResourceList();
		list.setIdMappings(mapping);
		
		RDFInfo info = new RDFInfo();
		info.setRdf("some rdf");
		info.setRdfFormat(RDFFormat.NTriples);
		
		list.setRdfInfo(info);
		
		ObjectMapper mapper = new ObjectMapper();
		String objectInJson = mapper.writeValueAsString(list);
		
		System.out.println("Sending rdf information:\n"+objectInJson);
		
		RequestBuilder requestBuilder = post("/rdf-resources")
																				.content(objectInJson)
																				.accept(MediaType.APPLICATION_JSON)
																				.contentType(MediaType.APPLICATION_JSON);
		String response = mockMvc.perform(requestBuilder)
				.andExpect(status().isOk())
				.andReturn().getResponse().getContentAsString();
    
    List<CloudResource> body = mapper.readValue(response,
        new TypeReference<List<CloudResource>>(){});
    
    assert body != null;
    assert body.size() == 3;
    
    body.forEach(resource -> {
      assert expected.get(resource.getInternalId()).equals(resource.getResource().getId());
    });
		
	}

	@Test
	public void testGetResource()  {
        RequestBuilder requestBuilder = get("/resource?resourceInternalId="+INTERNAL_ID)
        		.accept(MediaType.APPLICATION_JSON)
        		.contentType(MediaType.APPLICATION_JSON);
         
        
		try {
			MockHttpServletResponse result = mockMvc.perform(requestBuilder)
			         .andExpect(status().isOk())
			         .andReturn()
			         .getResponse();
	        logger.info("End of test ----------------------------- testGetResource");
			assert(true);
		} catch (Throwable e) {
			e.printStackTrace();
			assert(false);
		}     
		
	}
	
	@Test
	public void testUpdateResource() {
        try {
        		ObjectMapper mapper = new ObjectMapper();
				CloudResource cloudResource = getTestActuatorBean();
        		String objectInJson = mapper.writeValueAsString(cloudResource);
				
				RequestBuilder requestBuilder2 = put("/resource")
		        		.content(objectInJson)
		        		.accept(MediaType.APPLICATION_JSON)
		        		.contentType(MediaType.APPLICATION_JSON);
				MockHttpServletResponse result2 = mockMvc.perform(requestBuilder2)
				         .andExpect(status().isOk())
				         .andReturn()
				         .getResponse();
				String r2 = result2.getContentAsString();
		        logger.info("testGetResource-------------------------------------------------------- result:"+r2);
				if (!"".equals(r2)){
					CloudResource resource2 = (CloudResource)mapper.readValue(r2, CloudResource.class);
			        logger.info("End of test ----------------------------- testGetResource");
					assert(true);
				}else{
					assert(false);				
				}
		} catch (Throwable t) {
		    logger.info("failed End of test ----------------------------- testUpdateResource");
			t.printStackTrace();
			assert (false);
		}         
	}


	@Test
	public void testDeleteResource() {
        try {
    		ObjectMapper mapper = new ObjectMapper();
			CloudResource cloudResource = getTestActuatorBean();
    		String objectInJson = mapper.writeValueAsString(cloudResource);
    		logger.info(objectInJson);
            RequestBuilder requestBuilder = post("/resource")
            		.content(objectInJson)
            		.accept(MediaType.APPLICATION_JSON)
            		.contentType(MediaType.APPLICATION_JSON);
            mockMvc.perform(requestBuilder)
                     .andExpect(status().isOk())
                     .andReturn();         
        	
        	requestBuilder = delete("/resource?resourceInternalId="+INTERNAL_ID)
        		.accept(MediaType.APPLICATION_JSON)
        		.contentType(MediaType.APPLICATION_JSON);
         
			mockMvc.perform(requestBuilder)
			         .andExpect(status().isOk())
			         .andReturn();	        
			logger.info("End of test ----------------------------- testDeleteResource");
			assert (true);
		} catch (Throwable t) {
	        logger.info("failed End of test ----------------------------- testDeleteResource");
			t.printStackTrace();
			assert (false);
		}         
	}

    static public class DateUtil {
        public static Date addDays(Date date, int days) {
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            cal.add(Calendar.DATE, days); //minus number would decrement the days
            return cal.getTime();
        }
    }
}