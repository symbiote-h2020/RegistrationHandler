package eu.h2020.symbiote.rh.messaging.interworkinginterface;

import eu.h2020.symbiote.cloud.model.internal.CloudResource;
import eu.h2020.symbiote.cloud.model.internal.RdfCloudResorceList;
import eu.h2020.symbiote.core.cci.RDFResourceRegistryRequest;
import eu.h2020.symbiote.core.cci.ResourceRegistryRequest;
import eu.h2020.symbiote.core.cci.ResourceRegistryResponse;
import eu.h2020.symbiote.core.model.resources.Resource;
import eu.h2020.symbiote.rh.constants.RHConstants;
import eu.h2020.symbiote.rh.security.SecurityManager;
import eu.h2020.symbiote.security.exceptions.aam.TokenValidationException;

import feign.Feign;
import feign.jackson.JacksonDecoder;
import feign.jackson.JacksonEncoder;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

@Component
public class IIFMessageHandler {
  
  private interface IIFOperation<T> {
    ResourceRegistryResponse operation(String platformId, T request, Map<String, Object> headers) throws TokenValidationException;
  }
  
	private static final Log logger = LogFactory.getLog(IIFMessageHandler.class);
	
	private InterworkingInterfaceService jsonclient;
	@Value("${symbIoTe.interworkinginterface.url}")
	private String url;

    @Autowired
	private SecurityManager securityManager;

    //@Autowired
    //private ResourceRepository resourceRepository;
	
	@PostConstruct
	public void createClient() {
		logger.info("Will use "+ url +" to access to interworking interface");
		jsonclient = Feign.builder().errorDecoder(new InterworkingInterfaceErrorDecoder())
                                    .decoder(new JacksonDecoder())
                                    .encoder(new JacksonEncoder())
                                    .target(InterworkingInterfaceService.class, url);
	}


	private Map<String, Object> getAuthHeaders(){
 	    Map<String, Object> headers = new HashMap<String, Object> ();
    	headers.put(RHConstants.HEADER_TOKEN, securityManager.requestCoreToken().getToken());
        return headers;
	}
	
	private <T> Map<String, Resource> executeRequest(String platformId, T request, IIFOperation operation) throws TokenValidationException {
    try {
      ResourceRegistryResponse response = operation.operation(platformId, request, getAuthHeaders());
      
     return response.getResources();
     
    } catch (TokenValidationException e) {
      logger.error(e);
      securityManager.removeSavedTokens();
      throw e;
    } catch(Exception e){
      logger.error("Error accessing symbIoTe core.", e);
      throw e;
    }
  }
	
	private List<CloudResource> createOrUpdateResources(String platformId, List<CloudResource> cloudResources, IIFOperation operation) throws TokenValidationException {
	 Map<String, CloudResource> idMap = new HashMap<>();
	 for (int i=0; i < cloudResources.size(); i++) {
	   idMap.put(String.valueOf(i), cloudResources.get(i));
   }
   
   ResourceRegistryRequest request = new ResourceRegistryRequest();
   request.setResources(idMap.entrySet().stream().collect(Collectors.toMap(e -> e.getKey(), e -> e.getValue().getResource())));
    Map<String, Resource> saved = executeRequest(platformId, request, operation);
  
    return idMap.entrySet().stream().filter(entry -> saved.containsKey(entry.getKey()))
                .map(entry -> {
                  entry.getValue().setResource(saved.get(entry.getKey()));
                  return entry.getValue();
                }).collect(Collectors.toList());
  }
	
	public List<CloudResource> createResources(String platformId, List<CloudResource> cloudResources) throws TokenValidationException {
        return createOrUpdateResources(platformId, cloudResources, ((platformId1, request, headers) -> {
          return jsonclient.createResources(platformId1, (ResourceRegistryRequest) request, headers);
        }));
    }
    
    public List<CloudResource> addRdfResources(String platformId, RdfCloudResorceList resources) throws TokenValidationException {
	    Map<String, String> idMap = resources.getIdMappings();
  
      RDFResourceRegistryRequest request = new RDFResourceRegistryRequest();
      request.setRdfInfo(resources.getRdfInfo());
  
      Map<String, Resource> response = executeRequest(platformId, request, ((platformId1, request1, headers) -> {
        return jsonclient.createRdfResources(platformId1, (RDFResourceRegistryRequest) request1, headers);
      }));
	    
	    return idMap.entrySet().stream().filter(entry -> response.get(entry.getKey()) != null)
          .map(entry -> {
            CloudResource cloudResource = new CloudResource();
            cloudResource.setInternalId(entry.getValue());
            cloudResource.setResource(response.get(entry.getKey()));
            return cloudResource;
          }).collect(Collectors.toList());
    }


	public List<CloudResource> updateResources(String platformId, List<CloudResource> cloudResources) throws TokenValidationException {
    return createOrUpdateResources(platformId, cloudResources, ((platformId1, request, headers) -> {
      return jsonclient.updateResource(platformId1, (ResourceRegistryRequest) request, headers);
    }));
    }

	public List<String> removeResources(String platformId, List<CloudResource> resources) throws TokenValidationException {
    List<CloudResource> result = createOrUpdateResources(platformId, resources, ((platformId1, request, headers) -> {
      return jsonclient.removeResources(platformId1, (ResourceRegistryRequest) request, headers);
    }));
    
    return result.stream().map(resource -> resource.getResource().getId()).collect(Collectors.toList());
	}

}
