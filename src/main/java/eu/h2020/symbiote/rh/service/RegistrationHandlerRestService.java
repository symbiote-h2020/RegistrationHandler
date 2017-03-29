package eu.h2020.symbiote.rh.service;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import eu.h2020.symbiote.cloud.model.CloudResource;
import eu.h2020.symbiote.rh.PlatformInformationManager;
import eu.h2020.symbiote.rh.exceptions.ConflictException;


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
 * The getResource method retrieves \a CloudResource identified by \a resourceInternalId 
 * from the mondodb database and will return it.
 *
 * \param resourceInternalId id from the resource to be retrieved from the database
 * \return \a getResource returns the \a CloudResource, 
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
 * The addResource method stores \a CloudResource passed as parameter in the  
 * mondodb database and send the information to the \a Interworking Interface, \a Resource Access Proxy component and \a Monitoring components.
 *
 * \param resource \a CloudResource to be created within the system
 * \return \a addResource returns the \a CloudResource where the Symbiote id is included. 
 * An exception can be thrown when no \a internalId is indicated within the \a CloudResource 
 */
  @RequestMapping(method = RequestMethod.POST, path = "/resource")
  public CloudResource addResource(@RequestBody CloudResource resource) throws ConflictException{
    logger.info("START OF addResource, in data "+ resource);
    if (resource.getInternalId()==null) throw new ConflictException("internalId field must be informed");
    List<CloudResource> list = new ArrayList<CloudResource>();
    list.add(resource);
    List<CloudResource> result = infoManager.addResources(list);
    logger.info("END OF addResource, result "+ result);
    return result.get(0);
    
 }

//! Create a resource.
/*!
 * The addResources method stores List of \a CloudResource passed as parameter in the  
 * mondodb database and send the information to the \a Interworking Interface, \a Resource Access Proxy component and \a Monitoring components.
 *
 * \param resource \a CloudResource to be created within the system
 * \return \a addResource returns the \a CloudResource where the Symbiote id is included. 
 * An exception can be thrown when no \a internalId is indicated within the \a CloudResource 
 */
  
  @RequestMapping(method = RequestMethod.POST, path = "/resources")
  public List<CloudResource> addResources(@RequestBody List<CloudResource> resources) throws ConflictException{
    logger.info("START OF addResource, in data "+ resources);
    List<CloudResource> result = infoManager.addResources(resources);
    logger.info("END OF addResources, result "+ result);
    return null;
    
 }
  
//! Update a resource.
/*!
 * The updateResource method updates the \a CloudResource passed as parameter into the   
 * mondodb database and sends the information to the \a Interworking Interface and \a Resource Access Proxy component.
 *
 * \param resource \a CloudResource to be updated within the system
 * \return \a updateResource returns the \a CloudResource where the Symbiote id is included. 
 */
  @RequestMapping(method = RequestMethod.PUT, path = "/resource")
  public CloudResource updateResource(@RequestBody CloudResource resource) {
    logger.info("START OF updateResource, in data "+ resource);
    if (resource.getInternalId()==null) throw new ConflictException("internalId field must be informed");
    List<CloudResource> list = new ArrayList<CloudResource>();
    list.add(resource);
    List<CloudResource>  result = infoManager.updateResource(list);
    logger.info("END OF updateResource, result "+ result);
    return result.get(0);
  }

//! Update a resource.
/*!
 * The updateResource method updates the \a CloudResource passed as parameter into the   
 * mondodb database and sends the information to the \a Interworking Interface and \a Resource Access Proxy component.
 *
 * \param resource \a CloudResource to be updated within the system
 * \return \a updateResource returns the \a CloudResource where the Symbiote id is included. 
 */
  @RequestMapping(method = RequestMethod.PUT, path = "/resources")
  public List<CloudResource> updateResources(@RequestBody List<CloudResource> resource) {
    logger.info("START OF updateResource, in data "+ resource);
    List<CloudResource>  result = infoManager.updateResource(resource);    
    logger.info("END OF updateResource, result "+ result);
    return result;
  }

//! Delete a resource.
/*!
 * The deleteResource method removes the \a CloudResource identified by the id passed as a parameter in the \a resourceInternalId variable.   
 * It removes it from the mondodb database and request the removal of the information to the \a Interworking Interface and the \a Resource Access Proxy component.
 *
 * \param resourceInternalId \a internalId to the resource to be removed 
 * \return \a deleteResource returns the \a CloudResource that has been just removed 
 */
  @RequestMapping(method = RequestMethod.DELETE, path = "/resource")
  public CloudResource deleteResource(@RequestParam String resourceInternalId) {
    logger.info("START OF deleteResource, in data "+ resourceInternalId);
    List<String> list = new ArrayList<String>();
    list.add(resourceInternalId);
    List<CloudResource>  result = infoManager.deleteResources(list);
    if (result.size()>0){
    	logger.info("END OF deleteResource, result "+ result.get(0));
    	return result.get(0);
    }else{
    	logger.info("END OF deleteResource, the resource didn't exist");
    	return null;
    }
  }
 
  @RequestMapping(method = RequestMethod.DELETE, path = "/resources")
  public List<CloudResource> deleteResources(@RequestParam List<String> resourceInternalId) {
    logger.info("START OF deleteResource, in data "+ resourceInternalId);
    List<CloudResource> result = infoManager.deleteResources(resourceInternalId);
    logger.info("END OF deleteResource, result "+ result);
    return result;
  }
}