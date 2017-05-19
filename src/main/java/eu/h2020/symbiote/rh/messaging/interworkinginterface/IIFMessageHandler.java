package eu.h2020.symbiote.rh.messaging.interworkinginterface;

import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import eu.h2020.symbiote.core.cci.ResourceRegistryRequest;
import eu.h2020.symbiote.core.cci.ResourceRegistryResponse;
import eu.h2020.symbiote.cloud.model.internal.CloudResource;
import eu.h2020.symbiote.core.model.resources.Resource;
import eu.h2020.symbiote.rh.constants.RHConstants;
import eu.h2020.symbiote.rh.security.SecurityManager;
import eu.h2020.symbiote.rh.db.ResourceRepository;
import eu.h2020.symbiote.security.exceptions.aam.TokenValidationException;

import feign.Feign;
import feign.FeignException;
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

    @Autowired
    private ResourceRepository resourceRepository;
	
	@PostConstruct
	public void createClient() {
		logger.info("Will use "+ url +" to access to interworking interface");
		jsonclient = Feign.builder().errorDecoder(new InterworkingInterfaceDecoder())
                                    .decoder(new JacksonDecoder())
                                    .encoder(new JacksonEncoder())
                                    .target(InterworkingInterfaceService.class, url);
	}


	private Map<String, Object> getAuthHeaders(){
 	    Map<String, Object> headers = new HashMap<String, Object> ();
    	headers.put(RHConstants.HEADER_TOKEN, securityManager.requestCoreToken().getToken());
        return headers;
	}
	
	public List<CloudResource> createResources(String platformId, List<CloudResource> cloudResources) throws TokenValidationException {
        ArrayList<Resource> resourceListReceived = new ArrayList<Resource>();
        try{
            logger.info("User trying to createResources in "+platformId);
            List<Resource> listToSend = cloudResources.stream().map(resource ->	{ return resource.getResource(); } )
            .collect(Collectors.toList());
			
            ResourceRegistryRequest request = new ResourceRegistryRequest();
            request.setResources(listToSend);
            ResourceRegistryResponse response = jsonclient.createResources(platformId, request, getAuthHeaders());

            for (Iterator<Resource> iter = response.getResources().iterator(); iter.hasNext();){
                Resource resource= (Resource) iter.next();
                resourceListReceived.add(resource);
            }
            
     	    //be aware that the list must returned in the same order that it has been send
            int i = 0;
            for (CloudResource cloudResource:cloudResources)
                cloudResource.setResource(resourceListReceived.get(i++));
        } catch (FeignException e) {
            logger.error("Exception thrown: ", e);
            logger.info(e.getClass().getCanonicalName());
            securityManager.refreshCoreToken();
            throw e;
        } catch(Exception e){
            logger.error("Error accessing symbIoTe core.", e);
            throw e;
        }
		return cloudResources;
    }


	public List<CloudResource> updateResources(String platformId, List<CloudResource> cloudResources) throws TokenValidationException {
        ArrayList<Resource> resourceListReceived = new ArrayList<Resource>();
        try{
            logger.info("User trying to updateResources in "+platformId);
			
            List<Resource> listToSend = cloudResources.stream().map(resource ->	{ return resource.getResource(); } )
            .collect(Collectors.toList());

            ResourceRegistryRequest request = new ResourceRegistryRequest();
            request.setResources(listToSend);
            ResourceRegistryResponse response = jsonclient.updateResource(platformId, request, getAuthHeaders());

            for (Iterator<Resource> iter = response.getResources().iterator(); iter.hasNext();){
                Resource resource= (Resource) iter.next();
                resourceListReceived.add(resource);
            }   
  	   	
  	   	//	be aware that the list must returned in the same order that it has been send
            int i = 0;
            for (CloudResource cloudResource:cloudResources)
                cloudResource.setResource(resourceListReceived.get(i++));
			
        } catch (FeignException e) {
            logger.error(e);
            securityManager.refreshCoreToken();
            throw e;
        } catch(Exception e){
            logger.error("Error accessing symbIoTe core.", e);
            throw e;
        }
        return cloudResources;
    }

	public List<String> removeResources(String platformId, List<String> resourceIds) throws TokenValidationException {
        ArrayList<String>  result = new ArrayList<String>();
        ArrayList<String>  debug = new ArrayList<String>();
        HashMap<String,String> symbioteToInternalIds = new HashMap<String,String>();

        try{
            logger.info("User trying to removeResources in "+platformId);
            ArrayList<Resource> listToSend = new ArrayList<Resource>();

            for (Iterator<String> iter = resourceIds.iterator(); iter.hasNext();){
                String resourceId = (String) iter.next();
                Resource resource = new Resource();
                CloudResource existingResource = resourceRepository.getByInternalId(resourceId);
		        
                if (existingResource != null) {
                    resource.setId(existingResource.getResource().getId());
                    listToSend.add(resource);
                    debug.add(existingResource.getResource().getId());
                    symbioteToInternalIds.put(existingResource.getResource().getId(),resourceId);
                }

            } 

            ResourceRegistryRequest request = new ResourceRegistryRequest();
            request.setResources(listToSend);
            ResourceRegistryResponse response = jsonclient.removeResources(platformId, request, getAuthHeaders());

            for (Iterator<Resource> iter = response.getResources().iterator(); iter.hasNext();){
                Resource resource= (Resource) iter.next();
                result.add(symbioteToInternalIds.get(resource.getId()));
            } 
        } catch (FeignException e) {
            logger.error(e);
            securityManager.refreshCoreToken();
            throw e;
        } catch(Exception e){
			logger.error("Error accessing symbIoTe core.", e);
            throw e;
        }
        return result;
	}

}
