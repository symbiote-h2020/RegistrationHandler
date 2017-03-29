package eu.h2020.symbiote.service;


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

import eu.h2020.symbiote.cloud.model.CloudResource;
import eu.h2020.symbiote.constants.RHConstants;


/*
 * @author: Elena Garrido
 * @version: 12/02/2017
 */
@RestController
@WebAppConfiguration
@RequestMapping("/testiif")
public class IIFRestDummyServer {
  private static final Log logger = LogFactory.getLog(IIFRestDummyServer.class);
  
  
  @RequestMapping(method = RequestMethod.POST, path = RHConstants.DO_CREATE_RESOURCES,  produces = "application/json", consumes = "application/json")
  public @ResponseBody List<CloudResource>  createResources(@PathVariable(RHConstants.PLATFORM_ID) String platformId, @RequestBody List<CloudResource> resources) {
	  logger.info("User trying to createResources platformId"+platformId);
      //List<CloudResource> resources = gson.fromJson(new String(message.getBody()),  new TypeToken<ArrayList<CloudResource>>(){}.getType());

      List<CloudResource> result = resources.stream().map(resource -> { resource.setId("symbiote"+resource.getName()); return resource;})
      .collect(Collectors.toList());

	  return result;
  }
  
  @RequestMapping(method = RequestMethod.PUT, path = RHConstants.DO_UPDATE_RESOURCES,  produces = "application/json", consumes = "application/json")
  public @ResponseBody List<CloudResource>  updateResources(@PathVariable(RHConstants.PLATFORM_ID) String platformId, @RequestBody List<CloudResource> resources) {
	  logger.info("User trying to ypdateResources platformId"+platformId);
      //List<CloudResource> resources = gson.fromJson(new String(message.getBody()),  new TypeToken<ArrayList<CloudResource>>(){}.getType());

      List<CloudResource> result = resources.stream().map(resource -> { resource.setId("symbiote"+resource.getName()); return resource;})
      .collect(Collectors.toList());

	  return result;
  }

  @RequestMapping(method = RequestMethod.DELETE, path = RHConstants.DO_REMOVE_RESOURCES,  produces = "application/json", consumes = "application/json")
  public @ResponseBody List<String>  removeResources(@PathVariable(RHConstants.PLATFORM_ID) String platformId, @RequestBody List<String> resources) {
	  logger.info("User trying to ypdateResources platformId"+platformId);
	  return resources;
  }

  

}

