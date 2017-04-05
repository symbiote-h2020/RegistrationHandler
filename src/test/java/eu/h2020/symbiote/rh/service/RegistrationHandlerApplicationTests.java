package eu.h2020.symbiote.rh.service;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

import java.util.Arrays;

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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;

import eu.h2020.symbiote.cloud.model.CloudResource;
import eu.h2020.symbiote.core.model.resources.Actuator;
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@RunWith(SpringRunner.class)
@SpringBootTest( webEnvironment = WebEnvironment.DEFINED_PORT, properties = {"eureka.client.enabled=false", "spring.cloud.sleuth.enabled=false", "platform.id=helloid", "server.port=18033", "symbIoTe.interworkinginterface.url=http://localhost:18033/testiif"})
@Configuration
@ComponentScan
@EnableAutoConfiguration
public class RegistrationHandlerApplicationTests {
	static final String INTERNAL_ID = "testPurposeResourceId1";
	@Autowired
	private WebApplicationContext webApplicationContext;
    static private MockMvc mockMvc;

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


   private CloudResource getTestActuatorBean(){
	   
	   
       Actuator actuator = new Actuator();
       actuator.setLocatedAt("loc");
       actuator.setLabels(Arrays.asList("Act1"));
       actuator.setInterworkingServiceURL("http://example.com/url");
       actuator.setComments(Arrays.asList("Desc"));

       CloudResource cloudResource = new CloudResource();
       cloudResource.setHost("hostofcloudres");
       cloudResource.setInternalId("internalId1");
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
        System.out.println("End of test ----------------------------- testCreateResource");
        assert(true);
       }


	//@Test
	public void testGetResource()  {
        RequestBuilder requestBuilder = get("/resource?resourceInternalId="+INTERNAL_ID)
        		.accept(MediaType.APPLICATION_JSON)
        		.contentType(MediaType.APPLICATION_JSON);
         
        
		try {
			MockHttpServletResponse result = mockMvc.perform(requestBuilder)
			         .andExpect(status().isOk())
			         .andReturn()
			         .getResponse();
	        System.out.println("End of test ----------------------------- testGetResource");
			assert(true);
		} catch (Throwable e) {
			e.printStackTrace();
			assert(false);
		}     
		
	}
	
	//@Test
	public void testUpdateResource() {
        try {
				Gson gson = new Gson();
				CloudResource resource = getTestActuatorBean();
				
				String objectInJson = gson.toJson(resource);
				RequestBuilder requestBuilder2 = put("/resource")
		        		.content(objectInJson)
		        		.accept(MediaType.APPLICATION_JSON)
		        		.contentType(MediaType.APPLICATION_JSON);
				MockHttpServletResponse result2 = mockMvc.perform(requestBuilder2)
				         .andExpect(status().isOk())
				         .andReturn()
				         .getResponse();
				String r2 = result2.getContentAsString();
		        System.out.println("testGetResource-------------------------------------------------------- result:"+r2);
				if (!"".equals(r2)){
					CloudResource resource2 = (CloudResource)gson.fromJson(r2, CloudResource.class);
			        System.out.println("End of test ----------------------------- testGetResource");
					assert(true);
				}else{
					assert(false);				
				}
		} catch (Throwable t) {
			t.printStackTrace();
			assert (false);
		}         
	}


	//@Test
	public void testDeleteResource() {
        try {
        	CloudResource resource = getTestActuatorBean();
    	    Gson gson = new Gson();
            String objectInJson = gson.toJson(resource);
    		 
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
			System.out.println("End of test ----------------------------- testDeleteResource");
			assert (true);
		} catch (Throwable t) {
	        System.out.println("failed End of test ----------------------------- testDeleteResource");
			t.printStackTrace();
			assert (false);
		}         
	}


}