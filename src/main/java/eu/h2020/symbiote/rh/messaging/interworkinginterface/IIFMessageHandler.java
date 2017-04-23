package eu.h2020.symbiote.rh.messaging.interworkinginterface;

import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;

import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import eu.h2020.symbiote.cloud.model.CloudResource;
import eu.h2020.symbiote.core.model.resources.Resource;
import eu.h2020.symbiote.core.cci.ResourceRegistryRequest;
import eu.h2020.symbiote.core.cci.ResourceRegistryResponse;
import feign.Feign;
import feign.jackson.JacksonDecoder;
import feign.jackson.JacksonEncoder;

@Component
public class IIFMessageHandler {
	private static final Log logger = LogFactory.getLog(IIFMessageHandler.class);
	private InterworkingInterfaceService jsonclient;
	@Value("${symbIoTe.interworkinginterface.url}")
	private String url;
	
	
	@PostConstruct
	public void createClient() {
		logger.info("Will use "+ url +" to access to interworking interface");
		jsonclient = Feign.builder().decoder(new JacksonDecoder()).encoder(new JacksonEncoder()).target(InterworkingInterfaceService.class, url);
	}

	
	public List<CloudResource>  createResources(String platformId, List<CloudResource> cloudResources)  {
		ArrayList<Resource> resourceListReceived = new ArrayList<Resource>();
		try{
            logger.info("User trying to createResources in "+platformId);
     	    List<Resource> listToSend = cloudResources.stream().map(resource ->	{ return resource.getResource(); } )
 	    	.collect(Collectors.toList());
			
			ResourceRegistryRequest request = new ResourceRegistryRequest();
			request.setResources(listToSend);
     	    ResourceRegistryResponse response = jsonclient.createResources(platformId, request);

     	    for (Iterator<Resource> iter = response.getResources().iterator(); iter.hasNext();){
     	    	Resource resource= (Resource) iter.next();
     	    	resourceListReceived.add(resource);
     	    }
            
     	    //be aware that the list must returned in the same order that it has been send
     	    int i = 0;
     	    for (CloudResource cloudResource:cloudResources)
     	    	cloudResource.setResource(resourceListReceived.get(i++));
		}catch(Throwable t){
			logger.error("Error accessing to AAM server at "+url, t);
		}
		return cloudResources;
	}


	public List<CloudResource> updateResources(String platformId, List<CloudResource> cloudResources)  {
		ArrayList<Resource> resourceListReceived = new ArrayList<Resource>();
		try{
            logger.info("User trying to updateResources in "+platformId);
			
     	    List<Resource> listToSend = cloudResources.stream().map(resource ->	{ return resource.getResource(); } )
 	    	.collect(Collectors.toList());

			ResourceRegistryRequest request = new ResourceRegistryRequest();
			request.setResources(listToSend);
     	    ResourceRegistryResponse response = jsonclient.updateResource(platformId, request);

     	    for (Iterator<Resource> iter = response.getResources().iterator(); iter.hasNext();){
     	    	Resource resource= (Resource) iter.next();
     	    	resourceListReceived.add(resource);
     	    }   
  	   	
  	   	//	be aware that the list must returned in the same order that it has been send
     	    int i = 0;
     	    for (CloudResource cloudResource:cloudResources)
     	    	cloudResource.setResource(resourceListReceived.get(i++));
			
		}catch(Throwable t){
			logger.error("Error accessing to AAM server at "+url, t);
		}	
		return cloudResources;
	}

	public List<String> removeResources(String platformId, List<String> resourceIds)  {
		ArrayList<String>  result = new ArrayList<String>();
		try{
			logger.info("User trying to removeResources in "+platformId);
			ArrayList<Resource> listToSend = new ArrayList<Resource>();

     	    for (Iterator<String> iter = resourceIds.iterator(); iter.hasNext();){
     	    	String resourceId = (String) iter.next();
     	    	Resource resource = new Resource();
     	    	resource.setId(resourceId);
     	    	listToSend.add(resource);
     	    } 

			ResourceRegistryRequest request = new ResourceRegistryRequest();
			request.setResources(listToSend);
     	    ResourceRegistryResponse response = jsonclient.removeResources(platformId, request);

     	    for (Iterator<Resource> iter = response.getResources().iterator(); iter.hasNext();){
     	    	Resource resource= (Resource) iter.next();
     	    	result.add(resource.getId());
     	    } 
		}catch(Throwable t){
			logger.error("Error accessing to AAM server at "+url, t);
		}
		return result;
	}


}
