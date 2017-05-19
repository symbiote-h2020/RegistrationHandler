package eu.h2020.symbiote.rh.service;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Calendar;
import java.util.Date;

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
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.web.client.AsyncRestTemplate;
import org.springframework.web.context.WebApplicationContext;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.fasterxml.jackson.databind.ObjectMapper;

import eu.h2020.symbiote.cloud.model.internal.CloudResource;
import eu.h2020.symbiote.core.model.WKTLocation;
import eu.h2020.symbiote.core.model.resources.Actuator;
import eu.h2020.symbiote.rh.service.aams.DummyAAMAMQPLoginListener;

import static org.junit.Assert.assertEquals;


@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@RunWith(SpringRunner.class)
@SpringBootTest( webEnvironment = WebEnvironment.DEFINED_PORT, properties = {"eureka.client.enabled=false", "spring.cloud.sleuth.enabled=false", "platform.id=helloid", "server.port=18033", "symbIoTe.interworkinginterface.url=http://localhost:18033/testiif", "security.coreaam.url=http://localhost:18033", "security.rabbitMQ.ip=localhost", "security.enabled=true", "security.user=user", "security.password=password"})
//@SpringBootTest( webEnvironment = WebEnvironment.DEFINED_PORT, properties = {"eureka.client.enabled=false", "spring.cloud.sleuth.enabled=false", "platform.id=helloid", "server.port=18033", "symbIoTe.interworkinginterface.url=http://localhost:18033/testiifnosec", "security.coreAAM.url=http://localhost:18033", "security.rabbitMQ.ip=localhost", "security.enabled=false", "security.user=user", "security.password=password"})
@Configuration
@ComponentScan
@EnableAutoConfiguration
public class RegistrationHandlerApplicationTests {
	static final String INTERNAL_ID = "testPurposeResourceId1";

	@Autowired
	private WebApplicationContext webApplicationContext;

    static private MockMvc mockMvc;

    private DummyAAMAMQPLoginListener dummyAAMAMQPLoginListener = new DummyAAMAMQPLoginListener();

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
		dummyAAMAMQPLoginListener.init();
	}


   private CloudResource getTestActuatorBean(){
       Actuator actuator = new Actuator();
       WKTLocation location = new WKTLocation();
       location.setValue("location");
       actuator.setLabels(Arrays.asList("Act1"));
       actuator.setInterworkingServiceURL("http://example.com/url");
       actuator.setComments(Arrays.asList("Desc"));

       CloudResource cloudResource = new CloudResource();
       cloudResource.setCloudMonitoringHost("hostofcloudres");
       cloudResource.setInternalId(INTERNAL_ID);
       cloudResource.setResource(actuator);	   
	   
	   return cloudResource; 
   }

   private CloudResource getTestActuatorBeanInvalid(){
       Actuator actuator = new Actuator();
       WKTLocation location = new WKTLocation();
       location.setValue("location");
       actuator.setLabels(Arrays.asList("invalid"));
       actuator.setInterworkingServiceURL("http://example.com/url");
       actuator.setComments(Arrays.asList("Desc"));

       CloudResource cloudResource = new CloudResource();
       cloudResource.setCloudMonitoringHost("hostofcloudres");
       cloudResource.setInternalId(INTERNAL_ID);
       cloudResource.setResource(actuator);	   
	   
	   return cloudResource; 
   }

	// @Test
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
        logger.info("End of test ----------------------------- testCreateResource");
        assert(true);
       }

	// @Test
	public void testCreateResources() throws Exception {
		CloudResource cloudResource = getTestActuatorBean();
		List<CloudResource> list = new ArrayList<CloudResource>();
		list.add(cloudResource);
        ObjectMapper mapper = new ObjectMapper();
        String objectInJson = mapper.writeValueAsString(list);
		 
        RequestBuilder requestBuilder = post("/resources")
        		.content(objectInJson)
        		.accept(MediaType.APPLICATION_JSON)
        		.contentType(MediaType.APPLICATION_JSON);
        mockMvc.perform(requestBuilder)
                 .andExpect(status().isOk())
                 .andReturn();         
        logger.info("End of test ----------------------------- testCreateResource");
        assert(true);
	}

	@Test
	public void testCreateResourcesWithInvalidToken() throws Exception {
		CloudResource cloudResource = getTestActuatorBeanInvalid();
		List<CloudResource> list = new ArrayList<CloudResource>();
		list.add(cloudResource);
        ObjectMapper mapper = new ObjectMapper();
        String objectInJson = mapper.writeValueAsString(list);
		 
        RequestBuilder requestBuilder = post("/resources")
        		.content(objectInJson)
        		.accept(MediaType.APPLICATION_JSON)
        		.contentType(MediaType.APPLICATION_JSON);
        MvcResult result = mockMvc.perform(requestBuilder)
                 .andExpect(status().isBadRequest())
                 .andReturn(); 

        assertEquals("Token was invalid, but now refreshed. Reissue your request", result.getResponse().getContentAsString()); 
        logger.info("End of test ----------------------------- testCreateResource");
        assert(true);
	}

	// @Test
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
	
	// @Test
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


	// @Test
	public void testDeleteResource() {
        try {
    		ObjectMapper mapper = new ObjectMapper();
			CloudResource cloudResource = getTestActuatorBean();
    		String objectInJson = mapper.writeValueAsString(cloudResource);
    		 
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