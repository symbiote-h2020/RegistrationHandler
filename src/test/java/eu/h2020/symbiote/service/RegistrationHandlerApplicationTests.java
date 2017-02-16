package eu.h2020.symbiote.service;
/*
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.request;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

import java.util.Arrays;

import org.junit.Before;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.http.MediaType;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.web.client.AsyncRestTemplate;
import org.springframework.web.client.AsyncRestTemplate;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.WebApplicationContext;

import com.google.gson.Gson;

import eu.h2020.symbiote.beans.LocationBean;
import eu.h2020.symbiote.beans.ResourceBean;
@RunWith(SpringJUnit4ClassRunner.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)

@SpringBootTest({"webEnvironment=WebEnvironment.RANDOM_PORT", "eureka.client.enabled=false", "spring.cloud.sleuth.enabled=false"})
public class RegistrationHandlerApplicationTests {
	static final String INTERNAL_ID = "testPurposeResourceId1";
	@Autowired
	private WebApplicationContext webApplicationContext;
    static private MockMvc mockMvc;
    static private MockRestServiceServer mockServer;

    String uri;
   @Autowired
   Environment environment;
   @Autowired
   ConfigurableApplicationContext context;
   @Before
    public void setUp() throws Exception {
		AsyncRestTemplate asyncRestTemplate = new AsyncRestTemplate();
		mockMvc = webAppContextSetup(webApplicationContext).build();
		mockServer = MockRestServiceServer.createServer(asyncRestTemplate);
	}


	@Test
	public void testCreateResource() throws Exception {
		ResourceBean resource = new ResourceBean();
		LocationBean location = new LocationBean();
		location.setAltitude(500.0);
		location.setDescription("my location");
		location.setLatitude(45.0);
		location.setLongitude(34.3);
		location.setName("my location name");
		resource.setInternalId(INTERNAL_ID);
		resource.setLocation(location);
		resource.setDescription("my resource description");
		resource.setName("my resource name");
		
		resource.setObservedProperties(Arrays.asList(new String[]{"temperature", "humidity"}));
		resource.setOwner("me");
		resource.setResourceURL("http://localhost:4545/myresourceurl");
	    Gson gson = new Gson();
        String objectInJson = gson.toJson(resource);
         
		
         
         
        RequestBuilder requestBuilder = post("/resource")
        		.content(objectInJson)
        		.accept(MediaType.APPLICATION_JSON)
        		.contentType(MediaType.APPLICATION_JSON);
         
        mockMvc.perform(requestBuilder)
                 .andExpect(status().isOk())
                 .andReturn();         
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
			String r = result.getContentAsString();
			assert(!"".equals(r));
		} catch (Throwable e) {
			e.printStackTrace();
			assert(false);
		}     
		
	}
	
	@Test
	public void testUpdateResource() {
        try {
	        RequestBuilder requestBuilder = get("/resource?resourceInternalId="+INTERNAL_ID)
	        		.accept(MediaType.APPLICATION_JSON)
	        		.contentType(MediaType.APPLICATION_JSON);
	
	        MockHttpServletResponse result = mockMvc.perform(requestBuilder)
			         .andExpect(status().isOk())
			         .andReturn()
			         .getResponse();
			String r = result.getContentAsString();
			if (!"".equals(r)){
				Gson gson = new Gson();
				ResourceBean resource = (ResourceBean)gson.fromJson(r, ResourceBean.class);
				resource.setOwner("Symbiote");
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
				if (!"".equals(r2)){
					ResourceBean resource2 = (ResourceBean)gson.fromJson(r, ResourceBean.class);
					assert("Symbiote".equals(resource2.getOwner()));
				}else{
					assert(false);				
				}
			}else{
				assert(false);
			}
		} catch (Throwable t) {
			t.printStackTrace();
			assert (false);
		}         
	        
		
	}


	@Test
	public void testDeleteResource() {
        try {
        	RequestBuilder requestBuilder = delete("/resource?resourceInternalId="+INTERNAL_ID)
        		.accept(MediaType.APPLICATION_JSON)
        		.contentType(MediaType.APPLICATION_JSON);
         
			mockMvc.perform(requestBuilder)
			         .andExpect(status().isOk())
			         .andExpect(request().asyncStarted())
			         .andReturn();

			RequestBuilder requestBuilder2 = get("/resource?resourceInternalId="+INTERNAL_ID)
        		.accept(MediaType.APPLICATION_JSON)
        		.contentType(MediaType.APPLICATION_JSON);
			MockHttpServletResponse result2 = mockMvc.perform(requestBuilder2)
		         .andExpect(status().isOk())
		         .andReturn()
		         .getResponse();
			String r2 = result2.getContentAsString();
			assert ("".equals(r2));
		} catch (Throwable t) {
			t.printStackTrace();
			assert (false);
		}         

	}

	@Test
	public void testGetResources() {
        RequestBuilder requestBuilder = get("/resource")
        		.accept(MediaType.APPLICATION_JSON)
        		.contentType(MediaType.APPLICATION_JSON);
         
        try {
			mockMvc.perform(requestBuilder)
			         .andExpect(status().isOk())
			         .andExpect(request().asyncStarted())
			         .andReturn();
		} catch (Throwable t) {
			t.printStackTrace();
			assert (false);
		}         
		assert (true);
	}

}*/