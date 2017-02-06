package eu.h2020.symbiote;

import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import eu.h2020.symbiote.beans.ResourceBean;
import eu.h2020.symbiote.db.ResourceRepository;
import eu.h2020.symbiote.messaging.interworkinginterface.IIResourceMessageHandler;
import eu.h2020.symbiote.messaging.rap.RAPResourceMessageHandler;

/**
 * This class handles the initialization from the platform. Initially created by jose
 *
 * @author: jose, Elena Garrido
 * @version: 06/10/2016

 */
@Component
public class PlatformInformationManager {

  private static final Log logger = LogFactory.getLog(PlatformInformationManager.class);


  @Autowired
  private IIResourceMessageHandler ifresourceRegistrationMessageHandler;

  @Autowired
  private RAPResourceMessageHandler rapresourceRegistrationMessageHandler;

  @Autowired
  private ResourceRepository resourceRepository;

  @PostConstruct
  private void init() {
/* COMMENTED EG    coreClient = RegistrationHandlerApplication.
        createFeignClient(CoreRegistryClient.class, coreUrl);*/
  }

  private ResourceBean addOrUpdateInInternalRepository(ResourceBean resource){
     ResourceBean existingResource = resourceRepository.getByInternalId(resource.getInternalId());
      if (existingResource != null) {
    	  logger.info("update will be done");
      }
      return resourceRepository.save(resource);
  }

  private ResourceBean deleteInInternalRepository(String resourceId){
    if (!"".equals(resourceId)) {
        ResourceBean existingResource = resourceRepository.getByInternalId(resourceId);
        if (existingResource != null) {
          resourceRepository.delete(resourceId);
          return existingResource;
        }
    }
    return null;
  }

  public ResourceBean addResource(ResourceBean resource) {
    ResourceBean result  = null;
    ResourceBean beanWithStmbioteId = ifresourceRegistrationMessageHandler.sendResourceRegistrationMessage(resource);
    if (beanWithStmbioteId != null){
    	result  = addOrUpdateInInternalRepository(beanWithStmbioteId);
    }
    rapresourceRegistrationMessageHandler.sendResourceRegistrationMessage(result);
    return result;
  }

  public ResourceBean updateResource(ResourceBean resource) {
    ResourceBean result  = null;
    ResourceBean beanWithStmbioteId = ifresourceRegistrationMessageHandler.sendResourceUpdateMessage(resource);
    if (beanWithStmbioteId != null){
    	result  = addOrUpdateInInternalRepository(beanWithStmbioteId);
    }
    rapresourceRegistrationMessageHandler.sendResourceUpdateMessage(result);
    return result;
  }

  public ResourceBean deleteResource(String resourceId) {
	ResourceBean result = null;  
    String id = ifresourceRegistrationMessageHandler.sendResourceUnregistrationMessage(resourceId);
    if (id!=null)
        result  = deleteInInternalRepository(resourceId);
    rapresourceRegistrationMessageHandler.sendResourceUnregistrationMessage(id);
    
    return result;
  }

  public List<ResourceBean> addResources(List<ResourceBean> resources) {
    return resources.stream().map(resource -> addResource(resource))
        .filter(resource -> resource != null).collect(Collectors.toList());
  }

  public List<ResourceBean> updateResources(List<ResourceBean> resources) {
    return resources.stream().map(resource -> updateResource(resource))
            .filter(resource -> resource != null).collect(Collectors.toList());
  }


  public List<ResourceBean> getResources() {
    return resourceRepository.findAll();
  }

	public ResourceBean getResource(String resourceId) {
	    if (!"".equals(resourceId)) {
	        return resourceRepository.getByInternalId(resourceId);
	    }
		return null;
	}

/*
  public List<ResourceBean> registerResources(List<String> resourceIds) {

    PlatformBean platformInfo = getPlatformInfo();
    List<ResourceBean> resources = new ArrayList<>();

    if (platformInfo != null) {
      if (platformInfo.getSymbioteId() != null) {

        for (String id : resourceIds) {
          ResourceBean resource = resourceRepository.findOne(id);
          if (resource != null) {
            resources.add(resource);
          }
        }

        resources = coreClient.registerResource(platformInfo.getSymbioteId(), resources);

        return addResources(resources);
      }
    }

    return resources;
  }
*/
}
