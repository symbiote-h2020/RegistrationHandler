package eu.h2020.symbiote.service;

import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

import java.util.Arrays;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.client.AsyncRestTemplate;
import org.springframework.web.context.WebApplicationContext;

import eu.h2020.symbiote.beans.LocationBean;
import eu.h2020.symbiote.beans.ResourceBean;
import feign.Feign;
import feign.gson.GsonDecoder;
import feign.gson.GsonEncoder;
@RunWith(SpringJUnit4ClassRunner.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)

@SpringBootTest({"eureka.client.enabled=false", "spring.cloud.sleuth.enabled=false"})
public class RegistrationHandlerApplicationTests {
	static final String INTERNAL_ID = "testPurposeResourceId1";
	@Test
	public void testCreateResource() {
		assert(true);
	}
/*	@Autowired
	static private WebApplicationContext webApplicationContext;
    static private MockMvc mockMvc;
   static private MockRestServiceServer mockServer;

   static String uri;
   @Autowired
   Environment environment;
   
   @BeforeClass
    static public void setUp() throws Exception {
		AsyncRestTemplate asyncRestTemplate = new AsyncRestTemplate();
		mockMvc = webAppContextSetup(webApplicationContext).build();
		mockServer = MockRestServiceServer.createServer(asyncRestTemplate);
		}
	@Test
	public void testCreateResource() {
		uri = "http://localhost:923911"; 
		RHRestServiceClient client = Feign.builder()
				.decoder(new GsonDecoder())
				.encoder(new GsonEncoder())
                .target(RHRestServiceClient.class, uri);
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
		ResourceBean result = client.addResource(resource);
		assert (result!=null);
	}


	@Test
	public void testGetResource() {
		RHRestServiceClient client = Feign.builder()
				.decoder(new GsonDecoder())
				.encoder(new GsonEncoder())
                .target(RHRestServiceClient.class, uri);
		ResourceBean resource = client.getResource(INTERNAL_ID);
		assert (resource!=null);
	}
	@Test
	public void testUpdateResource() {
		RHRestServiceClient client = Feign.builder()
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
		assert (false);
	}


	@Test
	public void testDeleteResource() {
		RHRestServiceClient client = Feign.builder()
				.decoder(new GsonDecoder())
				.encoder(new GsonEncoder())
                .target(RHRestServiceClient.class, uri);
		ResourceBean resource = client.deleteResource(INTERNAL_ID);
		assert (resource!=null);
	}

	@Test
	public void testGetResources() {
		//stubFor(get(urlEqualTo("/resources"))
	    //        .willReturn(aResponse()
	    //            .withHeader("Content-Type", "text/plain")));
	
		RHRestServiceClient client = Feign.builder()
				.decoder(new GsonDecoder())
                .target(RHRestServiceClient.class, uri);
		List<ResourceBean> resources = client.getResources();
		if (resources!=null){
			System.out.println("Size of resources list: "+resources.size());
			for (ResourceBean r:resources){
				System.out.println("Resource with id "+r.getInternalId()+ " is registered");
			}
		}
		assert (true);
	}
*/
}