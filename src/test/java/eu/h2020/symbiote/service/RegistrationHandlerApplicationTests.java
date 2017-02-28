package eu.h2020.symbiote.service;

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
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.web.client.AsyncRestTemplate;
import org.springframework.web.context.WebApplicationContext;

import com.google.gson.Gson;

import eu.h2020.symbiote.cloud.model.CloudResource;
import eu.h2020.symbiote.core.model.Location;
@RunWith(SpringJUnit4ClassRunner.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)

@SpringBootTest({"webEnvironment=WebEnvironment.RANDOM_PORT", "eureka.client.enabled=false", "spring.cloud.sleuth.enabled=false"})
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
/*
	@RequestLine("GET /resources")
    @Headers("Content-Type: application/json")
    public List<ResourceBean> getResources(); 

    @RequestLine("GET /resource?resourceInternalId={resourceInternalId}")
    @Headers("Content-Type: application/json")
    public ResourceBean getResource(@Param("resourceInternalId")  String resourceInternalId);

    @RequestLine("POST /resource")
    @Headers("Content-Type: application/json")
    public ResourceBean addResource(ResourceBean resource);

    @RequestLine("PUT /resource")
    @Headers("Content-Type: application/json")
    public ResourceBean updateResource(ResourceBean resource);

    @RequestLine("DELETE /resource?resourceInternalId={resourceInternalId}")
    @Headers("Content-Type: application/json")
    public ResourceBean deleteResource(@Param("resourceInternalId")  String resourceInternalId); 
*/

   private CloudResource getTestResourceBean(){
	   CloudResource resource = new CloudResource();
	   
		Location location = new Location();
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
	   return resource; 
   }

	@Test
	public void testCreateResource() throws Exception {
		/*RHRestServiceClient client = Feign.builder()
				.decoder(new GsonDecoder())
				.encoder(new GsonEncoder())
                .target(RHRestServiceClient.class, uri);*/
		CloudResource resource = getTestResourceBean();
	    Gson gson = new Gson();
        String objectInJson = gson.toJson(resource);
		 
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


	@Test
	public void testGetResource()  {
		/*RHRestServiceClient client = Feign.builder()
				.decoder(new GsonDecoder())
				.encoder(new GsonEncoder())
                .target(RHRestServiceClient.class, uri);
		ResourceBean resource = client.getResource(INTERNAL_ID);*/
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
	
	@Test
	public void testUpdateResource() {

        try {
				Gson gson = new Gson();
				CloudResource resource = getTestResourceBean();
				
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
		        System.out.println("testGetResource-------------------------------------------------------- result:"+r2);
				if (!"".equals(r2)){
					CloudResource resource2 = (CloudResource)gson.fromJson(r2, CloudResource.class);
			        System.out.println("End of test ----------------------------- testGetResource");
					assert("Symbiote".equals(resource2.getOwner()));
				}else{
					assert(false);				
				}
		} catch (Throwable t) {
			t.printStackTrace();
			assert (false);
		}         
	        
/*		RHRestServiceClient client = Feign.builder()
				.decoder(new GsonDecoder())
				.encoder(new GsonEncoder())
                .target(RHRestServiceClient.class, uri);
		ResourceBean resource = client.getResource(INTERNAL_ID);
		if (resource!=null){

			resource.setOwner("Symbiote");
			resource = client.updateResource(resource);
			if (resource!=null){
				assert("Symbiote".equals(resource.getOwner()));
			}
		}
		assert (false);*/
		
	}


	@Test
	public void testDeleteResource() {
        try {
        	CloudResource resource = getTestResourceBean();
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

/*		RHRestServiceClient client = Feign.builder()
				.decoder(new GsonDecoder())
				.encoder(new GsonEncoder())
                .target(RHRestServiceClient.class, uri);
		ResourceBean resource = client.deleteResource(INTERNAL_ID);
		assert (resource!=null);*/
	}


}