package eu.h2020.symbiote.rh.messaging.interworkinginterface;

import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import eu.h2020.symbiote.cloud.model.CloudResource;
import feign.Feign;
import feign.gson.GsonDecoder;
import feign.gson.GsonEncoder;

@Component
public class IIFMessageHandler {
	private static final Log logger = LogFactory.getLog(IIFMessageHandler.class);
	private InterworkingInterfaceService jsonclient;
	@Value("${symbIoTe.interworkinginterface.url}")
	private String url;
	
	
	@PostConstruct
	public void createClient() {
		logger.info("Will use "+ url +" to access to interworking interface");
		jsonclient = Feign.builder().decoder(new GsonDecoder()).encoder(new GsonEncoder()).target(InterworkingInterfaceService.class, url);
	}

	
	public List<CloudResource>  createResources(String platformId, List<CloudResource> resources)  {
		List<CloudResource> resourceListReceived = null;
		try{
            logger.info("User trying to createResources in "+platformId);
            
     	    List<CloudResource> listToSend = resources.stream().map(resource ->	{ CloudResource cloned = null;
 	    		try {
 	    			cloned = (CloudResource) resource.clone();
 	    		} catch (Exception e) {
 	    			logger.error("Fatal error cloning resource", e);
 	    		}
 	    		cloned.setInternalId(""); return cloned;} )
 	    	.collect(Collectors.toList());
			
     	    resourceListReceived = jsonclient.createResources(platformId, listToSend);
 	   
     	    //be aware that the list must returned in the same order that it has been send
     	    int i = 0;
     	    for (CloudResource resource:resources)
     	    	resource.setId(resourceListReceived.get(i++).getId());
		}catch(Throwable t){
			logger.error("Error accessing to AAM server at "+url, t);
		}
		return resources;
	}


	public List<CloudResource> updateResources(String platformId, List<CloudResource> resources)  {
		List<CloudResource> resourceListReceived = null;
		try{
            logger.info("User trying to updateResources in "+platformId);
			
			
     	    List<CloudResource> listToSend = resources.stream().map(resource ->	{ CloudResource cloned = null;
 	    	try {
 	    		cloned = (CloudResource) resource.clone();
 	    	} catch (Exception e) {
			
				logger.error("Fatal error cloning resource", e);
 	    	}
 	    	cloned.setInternalId(""); return cloned;} )
 	    	 .collect(Collectors.toList());
     	    
     	    resourceListReceived = jsonclient.updateResource(platformId, listToSend);
 	   
  	   	
  	   	//	be aware that the list must returned in the same order that it has been send
     	    int i = 0;
     	    for (CloudResource resource:resources)
     	    	resource.setId(resourceListReceived.get(i++).getId());
			
		}catch(Throwable t){
			logger.error("Error accessing to AAM server at "+url, t);
		}	
		return resources;
	}

	public List<String> removeResources(String platformId, List<String> resourceIds)  {
		List<String>  result = null;
		try{
            logger.info("User trying to getResources in "+platformId);
			result = jsonclient.removeResources(platformId, resourceIds);
		}catch(Throwable t){
			logger.error("Error accessing to AAM server at "+url, t);
		}
		return result;
	}


}
