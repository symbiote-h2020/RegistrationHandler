package eu.h2020.symbiote.rh.messaging.interworkinginterface;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import eu.h2020.symbiote.cloud.model.internal.CloudResource;
import eu.h2020.symbiote.core.model.resources.Resource;
import eu.h2020.symbiote.rh.constants.RHConstants;
import eu.h2020.symbiote.rh.security.SecurityManager;
import feign.Feign;
import feign.jackson.JacksonDecoder;
import feign.jackson.JacksonEncoder;

@Component
public class IIFMessageHandler {
	private static final Log logger = LogFactory.getLog(IIFMessageHandler.class);
	private InterworkingInterfaceService jsonclient;
	@Value("${symbIoTe.interworkinginterface.url}")
	private String url;
    @Autowired
	private SecurityManager securityManager;

	
	@PostConstruct
	public void createClient() {
		logger.info("Will use "+ url +" to access to interworking interface");
		jsonclient = Feign.builder().decoder(new JacksonDecoder()).encoder(new JacksonEncoder()).target(InterworkingInterfaceService.class, url);
	}


	private Map<String, Object> getAuthHeaders(){
 	    Map<String, Object> headers = new HashMap<String, Object> ();
 	    if (securityManager.isEnabled())
 	    	headers.put(RHConstants.HEADER_TOKEN, securityManager.requestCoreToken().getToken());
 	    return headers;
	}
	
	public List<CloudResource> createResources(String platformId, List<CloudResource> cloudResources)  {
		//TODO add security
		List<Resource> resourceListReceived = null;
		try{
            logger.info("User trying to createResources in "+platformId);
     	    List<Resource> listToSend = cloudResources.stream().map(resource ->	{ return resource.getResource(); } )
 	    	.collect(Collectors.toList());
     	    resourceListReceived = jsonclient.createResources(listToSend, platformId, getAuthHeaders());
 	 
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
		List<Resource> resourceListReceived = null;
		try{
            logger.info("User trying to updateResources in "+platformId);
			
     	    List<Resource> listToSend = cloudResources.stream().map(resource ->	{ return resource.getResource(); } )
 	    	.collect(Collectors.toList());
     	    resourceListReceived = jsonclient.updateResource(listToSend, platformId, getAuthHeaders());
 	   
  	   	
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
		List<String>  result = null;
		try{
            logger.info("User trying to getResources in "+platformId);
			result = jsonclient.removeResources(resourceIds, platformId, getAuthHeaders());
		}catch(Throwable t){
			logger.error("Error accessing to AAM server at "+url, t);
		}
		return result;
	}
}
