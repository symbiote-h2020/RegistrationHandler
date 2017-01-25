package eu.h2020.symbiote.service;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import eu.h2020.symbiote.PlatformInformationManager;
import eu.h2020.symbiote.beans.ResourceBean;
import eu.h2020.symbiote.beans.ResourceListBean;
import eu.h2020.symbiote.exceptions.ConflictException;

/**
 * This class implements the rest interfaces. Initially created by jose
 *
 * @author: jose, Elena Garrido
 * @version: 27/09/2016
 */
@RestController
public class RegistrationHandlerRestService {
  private static final Log logger = LogFactory.getLog(RegistrationHandlerRestService.class);

  @Autowired
  private PlatformInformationManager infoManager;

  @RequestMapping(method = RequestMethod.GET, path = "/resources")
  public List<ResourceBean> getResources() {
    logger.info("START OF getResources");
    List<ResourceBean>result = infoManager.getResources();
    logger.info("END OF getResources, result "+ result);
    return result;
  }

  @RequestMapping(method = RequestMethod.GET, path = "/resource")
  public ResourceBean getResource(@RequestParam String resourceInternalId) throws ConflictException{
    logger.info("START OF getResource, in data "+ resourceInternalId);
    if ("".equals(resourceInternalId)) throw new ConflictException("resourceInternalId parameter must be informed");
    ResourceBean result = infoManager.deleteResource(resourceInternalId);
    logger.info("END OF getResource, result "+ result);
    return result;
  }

  @RequestMapping(method = RequestMethod.POST, path = "/resources")
  public ResourceBean addResource(@RequestBody ResourceListBean resourceList) throws ConflictException{
    logger.info("START OF addResource, in data size"+ ((resourceList == null)?0:resourceList.getResources().size()));
//    if (resource.getInternalId()==null) throw new ConflictException("internalId field must be informed");
    /*return resources.stream().map(resource -> addResource(resource))
            .filter(resource -> resource != null).collect(Collectors.toList());*/
    /*ResourceBean result = infoManager.addResources(resourceList);
    logger.info("END OF addResource, result "+ result);
    return result;*/
    return null;
  }

  @RequestMapping(method = RequestMethod.PUT, path = "/resource")
  public ResourceBean updateResource(@RequestBody ResourceBean resource) {
    logger.info("START OF updateResource, in data "+ resource);
    ResourceBean result = infoManager.updateResource(resource);
    logger.info("END OF updateResource, result "+ result);
    return result;
  }

  @RequestMapping(method = RequestMethod.DELETE, path = "/resource")
  public ResourceBean deleteResource(@RequestParam String resourceInternalId) {
    logger.info("START OF deleteResource, in data "+ resourceInternalId);
    ResourceBean result = infoManager.deleteResource(resourceInternalId);
    logger.info("END OF deleteResource, result "+ result);
    return result;
  }

}
