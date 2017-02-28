package eu.h2020.symbiote.service;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import eu.h2020.symbiote.PlatformInformationManager;
import eu.h2020.symbiote.cloud.model.CloudResource;
import eu.h2020.symbiote.exceptions.ConflictException;


/**! \class RegistrationHandlerRestService 
 * \brief RegistrationHandlerRestService Rest controller for registration handler
 * This class implements the rest interfaces.  It is the class that will receive any REST call done to the registration handler 
 **/
/*
 * @author: jose, Elena Garrido
 * @version: 27/09/2016
 */
@RestController
public class RegistrationHandlerRestService {
  private static final Log logger = LogFactory.getLog(RegistrationHandlerRestService.class);

  @Autowired
  private PlatformInformationManager infoManager;

  
  @RequestMapping(method = RequestMethod.GET, path = "/resources")
  public List<CloudResource> getResources() {
    logger.info("START OF getResources");
    List<CloudResource>result = infoManager.getResources();
    logger.info("END OF getResources, result "+ result);
    return result;
  }
//! Get a resource.
/*!
 * The getResource method retrieves \a ResourceBean identified by \a resourceInternalId 
 * from the mondodb database and will return it.
 *
 * \param resourceInternalId id from the resource to be retrieved from the database
 * \return \a getResource returns the \a ResourceBean, 
 * An exception can be thrown when no \a resourceInternalId is indicated
 */
  @RequestMapping(method = RequestMethod.GET, path = "/resource")
  public CloudResource getResource(@RequestParam String resourceInternalId) throws ConflictException{
    logger.info("START OF getResource, in data "+ resourceInternalId);
    if ("".equals(resourceInternalId)) throw new ConflictException("resourceInternalId parameter must be informed");
    CloudResource result = infoManager.getResource(resourceInternalId);
    logger.info("END OF getResource, result "+ result);
    return result;
  }

//! Create a resource.
/*!
 * The addResource method stores \a ResourceBean passed as parameter in the  
 * mondodb database and send the information to the \a Interworking Interface and \a Resource Access Proxy component.
 *
 * \param resource \a ResourceBean to be created within the system
 * \return \a addResource returns the \a ResourceBean where the Symbiote id is included. 
 * An exception can be thrown when no \a internalId is indicated within the \a ResourceBean 
 */
  @RequestMapping(method = RequestMethod.POST, path = "/resource")
  public CloudResource addResource(@RequestBody CloudResource resource) throws ConflictException{
    logger.info("START OF addResource, in data "+ resource);
    System.out.println("START OF addResource, in data "+ resource);
    if (resource.getInternalId()==null) throw new ConflictException("internalId field must be informed");
    /*return resources.stream().map(resource -> addResource(resource))
            .filter(resource -> resource != null).collect(Collectors.toList());*/
    CloudResource result = infoManager.addResource(resource);
    logger.info("END OF addResource, result "+ result);
    return result;
    
 }

//! Update a resource.
/*!
 * The updateResource method updates the \a ResourceBean passed as parameter into the   
 * mondodb database and sends the information to the \a Interworking Interface and \a Resource Access Proxy component.
 *
 * \param resource \a ResourceBean to be updated within the system
 * \return \a updateResource returns the \a ResourceBean where the Symbiote id is included. 
 */
  @RequestMapping(method = RequestMethod.PUT, path = "/resource")
  public CloudResource updateResource(@RequestBody CloudResource resource) {
    logger.info("START OF updateResource, in data "+ resource);
    CloudResource result = infoManager.updateResource(resource);
    logger.info("END OF updateResource, result "+ result);
    return result;
  }

//! Delete a resource.
/*!
 * The deleteResource method removes the \a ResourceBean identified by the id passed as a parameter in the \a resourceInternalId variable.   
 * It removes it from the mondodb database and request the removal of the information to the \a Interworking Interface and the \a Resource Access Proxy component.
 *
 * \param resourceInternalId \a internalId to the resource to be removed 
 * \return \a deleteResource returns the \a ResourceBean that has been just removed 
 */
  @RequestMapping(method = RequestMethod.DELETE, path = "/resource")
  public CloudResource deleteResource(@RequestParam String resourceInternalId) {
    logger.info("START OF deleteResource, in data "+ resourceInternalId);
    CloudResource result = infoManager.deleteResource(resourceInternalId);
    logger.info("END OF deleteResource, result "+ result);
    return result;
  }

}
