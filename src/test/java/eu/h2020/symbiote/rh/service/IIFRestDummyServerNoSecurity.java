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

import eu.h2020.symbiote.core.model.resources.Resource;
import eu.h2020.symbiote.rh.constants.RHConstants;


/*
 * @author: Elena Garrido
 * @version: 12/02/2017
 */
@RestController
@WebAppConfiguration
@RequestMapping("/testiifnosec")
public class IIFRestDummyServerNoSecurity {
  private static final Log logger = LogFactory.getLog(IIFRestDummyServerNoSecurity.class);
  static int i=0;
  
  
  @RequestMapping(method = RequestMethod.POST, path = RHConstants.DO_CREATE_RESOURCES,  produces = "application/json", consumes = "application/json")
  public @ResponseBody List<Resource>  createResources(@RequestBody List<Resource> resources, @PathVariable(RHConstants.PLATFORM_ID) String platformId) {
	  logger.info("User trying to createResources platformId"+platformId);
      List<Resource> result = resources.stream().map(resource -> { resource.setId("symbiote"+i++); return resource;})
      .collect(Collectors.toList());

	  return result;
  }
  
  @RequestMapping(method = RequestMethod.PUT, path = RHConstants.DO_UPDATE_RESOURCES,  produces = "application/json", consumes = "application/json")
  public @ResponseBody List<Resource>  updateResources(@PathVariable(RHConstants.PLATFORM_ID) String platformId, @RequestBody List<Resource> resources) {
	  logger.info("User trying to updateResources platformId"+platformId);
      List<Resource> result = resources.stream().map(resource -> { resource.setId("symbiote"+i++); return resource;})
      .collect(Collectors.toList());

	  return result;
  }

  @RequestMapping(method = RequestMethod.DELETE, path = RHConstants.DO_REMOVE_RESOURCES,  produces = "application/json", consumes = "application/json")
  public @ResponseBody List<String>  removeResources(@PathVariable(RHConstants.PLATFORM_ID) String platformId, @RequestBody List<String> resources) {
	  logger.info("User trying to removeResources platformId"+platformId);
	  return resources;
  }

  

}

