package eu.h2020.symbiote.service;

import java.util.Arrays;
import java.util.List;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.test.context.junit4.SpringRunner;

import eu.h2020.symbiote.beans.LocationBean;
import eu.h2020.symbiote.beans.ResourceBean;
import feign.Feign;
import feign.gson.GsonDecoder;
import feign.gson.GsonEncoder; 
 
@RunWith(SpringRunner.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class RegistrationHandlerApplicationTests {
	static final String INTERNAL_ID = "testPurposeResourceId1";
	@Test
	public void testCreateResource() {
		RHRestServiceClient client = Feign.builder()
				.decoder(new GsonDecoder())
				.encoder(new GsonEncoder())
                .target(RHRestServiceClient.class, "http://127.0.0.1:8001");
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
                .target(RHRestServiceClient.class, "http://127.0.0.1:8001");
		ResourceBean resource = client.getResource(INTERNAL_ID);
		assert (resource!=null);
	}
	@Test
	public void testUpdateResource() {
		RHRestServiceClient client = Feign.builder()
				.decoder(new GsonDecoder())
				.encoder(new GsonEncoder())
                .target(RHRestServiceClient.class, "http://127.0.0.1:8001");
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
                .target(RHRestServiceClient.class, "http://127.0.0.1:8001");
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
                .target(RHRestServiceClient.class, "http://127.0.0.1:8001");
		List<ResourceBean> resources = client.getResources();
		if (resources!=null){
			System.out.println("Size of resources list: "+resources.size());
			for (ResourceBean r:resources){
				System.out.println("Resource with id "+r.getInternalId()+ " is registered");
			}
		}
		assert (true);
	}
	

}