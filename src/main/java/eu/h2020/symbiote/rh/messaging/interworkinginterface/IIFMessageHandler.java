package eu.h2020.symbiote.rh.messaging.interworkinginterface;

import eu.h2020.symbiote.cloud.model.internal.CloudResource;
import eu.h2020.symbiote.cloud.model.internal.RdfCloudResourceList;
import eu.h2020.symbiote.core.cci.RDFResourceRegistryRequest;
import eu.h2020.symbiote.core.cci.ResourceRegistryRequest;
import eu.h2020.symbiote.core.cci.ResourceRegistryResponse;
import eu.h2020.symbiote.core.model.resources.Resource;
import eu.h2020.symbiote.security.ComponentSecurityHandlerFactory;
import eu.h2020.symbiote.security.commons.exceptions.custom.SecurityHandlerException;
import eu.h2020.symbiote.security.communication.SymbioteAuthorizationClient;
import eu.h2020.symbiote.security.handler.IComponentSecurityHandler;

import feign.Client;
import feign.Feign;
import feign.jackson.JacksonDecoder;
import feign.jackson.JacksonEncoder;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

@Component
public class IIFMessageHandler {
  
  @Value("${symbIoTe.coreaam.url}")
  private String coreAAMAddress;
  
  @Value("${symbIoTe.component.keystore.password}")
  private String keystorePassword;
  
  @Value("${symbIoTe.component.keystore.path}")
  private String keystorePath;
  
  @Value("${symbIoTe.component.clientId}")
  private String clientId;
  
  @Value("${symbIoTe.component.registry.id}")
  private String registryId;
  
  @Value("${symbIoTe.localaam.url}")
  private String localAAMAddress;
  
  @Value("${symbIoTe.component.username}")
  private String username;
  
  @Value("${symbIoTe.component.password}")
  private String password;
  
  @Value("${platform.id}")
  private String platformId;
  
  @Value("${symbIoTe.targetaam.id}")
  private String targetAAMId;
  
  @Value("${symbIoTe.aam.integration}")
  private boolean useSecurity;
  
  @Value("${symbIoTe.core.cloud.interface.url}")
  private String url;
  
  @Value("${symbIoTe.interworking.interface.url}")
  private String interworkingUrl;
  
  private interface IIFOperation<T> {
    ResourceRegistryResponse operation(String platformId, T request) throws SecurityHandlerException;
  }
  
  private static final Log logger = LogFactory.getLog(IIFMessageHandler.class);
  
  private InterworkingInterfaceService jsonclient;
  
  @PostConstruct
  public void createClient() throws SecurityHandlerException {
    
    Feign.Builder builder = Feign.builder()
                                .decoder(new JacksonDecoder())
                                .encoder(new JacksonEncoder());
    if (useSecurity) {
      IComponentSecurityHandler secHandler = ComponentSecurityHandlerFactory
                                                 .getComponentSecurityHandler(
                                                     coreAAMAddress, keystorePath, keystorePassword,
                                                     clientId, localAAMAddress, false,
                                                     username, password
                                                 );
      
      Client client = new SymbioteAuthorizationClient(
                                                         secHandler, "registry", targetAAMId,
                                                         new Client.Default(null, null));
      
      logger.info("Will use " + url + " to access to interworking interface");
      builder = builder.client(client);
    }
    jsonclient = builder.target(InterworkingInterfaceService.class, url);
  }
  
  private <T> Map<String, Resource> executeRequest(T request, IIFOperation operation) throws SecurityHandlerException {
    try {
      ResourceRegistryResponse response = operation.operation(platformId, request);
      
      return response.getBody();
      
    } catch (SecurityHandlerException e) {
      logger.error("Error accessing symbIoTe core.", e);
      throw e;
    }
  }
  
  private List<CloudResource> createOrUpdateResources(List<CloudResource> cloudResources, IIFOperation operation) throws SecurityHandlerException {
    Map<String, CloudResource> idMap = new HashMap<>();
    for (int i = 0; i < cloudResources.size(); i++) {
      idMap.put(String.valueOf(i), cloudResources.get(i));
    }
    
    ResourceRegistryRequest request = new ResourceRegistryRequest();
    request.setBody(idMap.entrySet().stream().collect(Collectors.toMap(e -> e.getKey(), e -> e.getValue().getResource())));
    Map<String, Resource> saved = executeRequest(request, operation);
    
    return idMap.entrySet().stream().filter(entry -> saved.containsKey(entry.getKey()))
               .map(entry -> {
                 entry.getValue().setResource(saved.get(entry.getKey()));
                 return entry.getValue();
               }).collect(Collectors.toList());
  }
  
  public List<CloudResource> createResources(List<CloudResource> cloudResources) throws SecurityHandlerException {
    return createOrUpdateResources(cloudResources, ((platformId1, request) -> {
      return jsonclient.createResources(platformId1, (ResourceRegistryRequest) request);
    }));
  }
  
  public List<CloudResource> addRdfResources(RdfCloudResourceList resources) throws SecurityHandlerException {
    Map<String, CloudResource> idMap = resources.getIdMappings();
    
    RDFResourceRegistryRequest request = new RDFResourceRegistryRequest();
    request.setInterworkingServiceUrl(interworkingUrl);
    request.setBody(resources.getRdfInfo());
    
    Map<String, Resource> response = executeRequest(request, ((platformId1, request1) -> {
      return jsonclient.createRdfResources(platformId1, (RDFResourceRegistryRequest) request1);
    }));
    
    return idMap.entrySet().stream().filter(entry -> response.get(entry.getKey()) != null)
               .map(entry -> {
                 CloudResource cloudResource = entry.getValue();
                 cloudResource.setResource(response.get(entry.getKey()));
                 return cloudResource;
               }).collect(Collectors.toList());
  }
  
  
  public List<CloudResource> updateResources(List<CloudResource> cloudResources) throws SecurityHandlerException {
    return createOrUpdateResources(cloudResources, ((platformId1, request) -> {
      return jsonclient.updateResource(platformId1, (ResourceRegistryRequest) request);
    }));
  }
  
  public List<String> removeResources(List<CloudResource> resources) throws SecurityHandlerException {
    List<CloudResource> result = createOrUpdateResources(resources, ((platformId1, request) -> {
      return jsonclient.removeResources(platformId1, (ResourceRegistryRequest) request);
    }));
    
    return result.stream().map(resource -> resource.getResource().getId()).collect(Collectors.toList());
  }
  
}
