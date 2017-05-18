package eu.h2020.symbiote.rh.service;


import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpHeaders;

import eu.h2020.symbiote.core.model.resources.Resource;
import eu.h2020.symbiote.rh.constants.RHConstants;
import eu.h2020.symbiote.core.cci.ResourceRegistryRequest;
import eu.h2020.symbiote.core.cci.ResourceRegistryResponse;

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
  
  
  @RequestMapping(method = RequestMethod.POST, path = RHConstants.DO_CREATE_RESOURCES,  produces = "application/json", consumes = "application/json")
  public ResponseEntity<?> createResources(@PathVariable(RHConstants.PLATFORM_ID) String platformId, @RequestBody ResourceRegistryRequest resources) {
    logger.info("User trying to createResources platformId"+platformId);
    List<Resource> listTosend = resources.getResources().stream().map(resource -> { resource.setId("symbiote"+i++); return resource;})
      .collect(Collectors.toList());
    ResourceRegistryResponse result = new ResourceRegistryResponse(); 
    HttpHeaders responseHeaders = new HttpHeaders();
    HttpStatus httpStatus;

    if (listTosend.get(0).getLabels().get(0).equals("invalid")) {
      logger.info("Token is invalid");
      httpStatus = HttpStatus.BAD_REQUEST;
      result.setMessage("Token invalid");
    }
    else {
      logger.info("Token is valid");
      result.setResources(listTosend);
      httpStatus = HttpStatus.OK;
    }
    return new ResponseEntity<ResourceRegistryResponse>(result, responseHeaders, httpStatus);
  }
  
  @RequestMapping(method = RequestMethod.PUT, path = RHConstants.DO_UPDATE_RESOURCES,  produces = "application/json", consumes = "application/json")
  public @ResponseBody ResourceRegistryResponse  updateResource(@PathVariable(RHConstants.PLATFORM_ID) String platformId, @RequestBody ResourceRegistryRequest resources) {
    logger.info("User trying to updateResources platformId"+platformId);
      List<Resource> listTosend = resources.getResources().stream().map(resource -> { resource.setId("symbiote"+i++); return resource;})
      .collect(Collectors.toList());
    ResourceRegistryResponse result = new ResourceRegistryResponse(); 
    result.setResources(listTosend);
    return result;
  }

  @RequestMapping(method = RequestMethod.DELETE, path = RHConstants.DO_REMOVE_RESOURCES,  produces = "application/json", consumes = "application/json")
  public @ResponseBody ResourceRegistryResponse  removeResources(@PathVariable(RHConstants.PLATFORM_ID) String platformId, @RequestBody ResourceRegistryRequest resources) {
    logger.info("User trying to removeResources platformId"+platformId);
    ResourceRegistryResponse result = new ResourceRegistryResponse(); 
    result.setResources(resources.getResources());
    return result;
  }

  

}

