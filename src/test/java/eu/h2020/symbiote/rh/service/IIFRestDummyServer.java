package eu.h2020.symbiote.rh.service;


import eu.h2020.symbiote.core.cci.RDFResourceRegistryRequest;
import eu.h2020.symbiote.core.cci.ResourceRegistryRequest;
import eu.h2020.symbiote.core.cci.ResourceRegistryResponse;
import eu.h2020.symbiote.model.cim.Resource;
import eu.h2020.symbiote.rh.constants.RHConstants;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/*
 * @author: Elena Garrido
 * @version: 12/02/2017
 */
@RestController
@WebAppConfiguration
@RequestMapping("/testiif")
public class IIFRestDummyServer {
  
  private static final Log logger = LogFactory.getLog(IIFRestDummyServer.class);
  static int i=0;
  
  private Map<String, Resource> saveResources(@RequestBody ResourceRegistryRequest resources) {
    return resources.getBody().entrySet().stream()
               .filter(entry -> !"invalid".equals(entry.getValue().getName()))
               .collect(Collectors.toMap(
                   e -> e.getKey(),
                   e -> {
                     if (e.getValue().getId() == null) {
                       e.getValue().setId("symbiote" + i++);
                     }
                     return e.getValue();
                   }));
  }
  
  @RequestMapping(method = RequestMethod.POST, path = RHConstants.DO_CREATE_RESOURCES,  produces = "application/json", consumes = "application/json")
  public ResponseEntity<?> createResources(@PathVariable(RHConstants.PLATFORM_ID) String platformId, @RequestBody ResourceRegistryRequest resources) {
    logger.info("User trying to createResources with platformId "+platformId);
    Map<String, Resource> listTosend = saveResources(resources);
  
    ResourceRegistryResponse result = new ResourceRegistryResponse(); 
    HttpHeaders responseHeaders = new HttpHeaders();
    HttpStatus httpStatus;

    if (listTosend.size() != resources.getBody().size()) {
      logger.info("Token is invalid");
      httpStatus = HttpStatus.BAD_REQUEST;
      result.setMessage("Token invalid");
    }
    else {
      logger.info("Token is valid");
      result.setBody(listTosend);
      httpStatus = HttpStatus.OK;
    }
    return new ResponseEntity<ResourceRegistryResponse>(result, responseHeaders, httpStatus);
  }
  
  @RequestMapping(method = RequestMethod.PUT, path = RHConstants.DO_UPDATE_RESOURCES,  produces = "application/json", consumes = "application/json")
  public @ResponseBody ResourceRegistryResponse  updateResource(@PathVariable(RHConstants.PLATFORM_ID) String platformId, @RequestBody ResourceRegistryRequest resources) {
    logger.info("User trying to updateResources with platformId "+platformId);
    Map<String, Resource> listTosend = saveResources(resources);
    ResourceRegistryResponse result = new ResourceRegistryResponse(); 
    result.setBody(listTosend);
    return result;
  }

  @RequestMapping(method = RequestMethod.DELETE, path = RHConstants.DO_REMOVE_RESOURCES,  produces = "application/json", consumes = "application/json")
  public @ResponseBody ResourceRegistryResponse  removeResources(@PathVariable(RHConstants.PLATFORM_ID) String platformId, @RequestBody ResourceRegistryRequest resources) {
    logger.info("User trying to removeResources with platformId "+platformId);
    ResourceRegistryResponse result = new ResourceRegistryResponse(); 
    result.setBody(resources.getBody());
    return result;
  }
  
  private Resource createFakeResource(String id) {
    Resource resource = new Resource();
    resource.setId(id);
    return resource;
  }
  
  @RequestMapping(method = RequestMethod.POST, path = RHConstants.DO_CREATE_RDF_RESOURCES,  produces = "application/json", consumes = "application/json")
  public @ResponseBody ResourceRegistryResponse createRdfResources(@PathVariable(RHConstants.PLATFORM_ID) String platformId, @RequestBody RDFResourceRegistryRequest resources) {
    
    ResourceRegistryResponse response = new ResourceRegistryResponse();
    
    Map<String, Resource> resourceMap = new HashMap<>();
  
    resourceMap.put("http://www.testcompany.eu/customPlatform/service1234", createFakeResource("service1234"));
    resourceMap.put("http://www.testcompany.eu/customPlatform/sensor1", createFakeResource("sensor1"));
    resourceMap.put("http://www.testcompany.eu/customPlatform/actuator1", createFakeResource("actuator1"));
  
    response.setBody(resourceMap);
    
    return response;
    
  }

}

