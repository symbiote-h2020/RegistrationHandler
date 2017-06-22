package eu.h2020.symbiote.rh;

import eu.h2020.symbiote.cloud.model.internal.CloudResource;
import eu.h2020.symbiote.rh.db.ResourceRepository;
import eu.h2020.symbiote.rh.messaging.cloud.RAPResourceMessageHandler;
import eu.h2020.symbiote.rh.messaging.interworkinginterface.IIFMessageHandler;
import eu.h2020.symbiote.security.exceptions.aam.TokenValidationException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**! \class PlatformInformationManager
 * \brief PlatformInformationManager handles the registration of the resources within the platform
 **/

/**
 * This class handles the initialization from the platform. Initially created by jose
 *
 * @author: jose, Elena Garrido
 * @version: 06/10/2016

 */
@Component
public class PlatformInformationManager {

  private static final Log logger = LogFactory.getLog(PlatformInformationManager.class);
  @Value("${platform.id}")
  String platformId;

  
  @Autowired
  private RAPResourceMessageHandler rapresourceRegistrationMessageHandler;

  @Autowired
  private ResourceRepository resourceRepository;

  @Autowired
  private IIFMessageHandler iifMessageHandler;


  /*private List<CloudResource>  addOrUpdateInInternalRepository(List<CloudResource>  resources){
	  return resources.stream().map(resource -> {
		  CloudResource existingResource = resourceRepository.getByInternalId(resource.getInternalId());
	      if (existingResource != null) {
	    	  logger.info("update will be done");
	      }
	      return resourceRepository.save(resource);
	 })
     .collect(Collectors.toList());	 
  }*/

  private List<CloudResource> deleteInInternalRepository(List<String> resourceIds){
	  List<CloudResource>  result = new ArrayList<CloudResource>();

	  for (String resourceId:resourceIds) {
		  CloudResource existingResource = resourceRepository.getByInternalId(resourceId);
	      if (existingResource != null) {
	    	  result.add(existingResource);
	    	  resourceRepository.delete(resourceId);
	      }
	  }
	  return result;
  }
  
  private List<CloudResource> addOrUpdateResources(List<CloudResource> resources) throws TokenValidationException {
    List<CloudResource> toAdd = new ArrayList<>();
    List<CloudResource> toUpdate = new ArrayList<>();
    
    List<CloudResource> result = new ArrayList<>();
    
    resources.stream().forEach(resource -> {
      String internalId = resource.getInternalId();
      if (internalId == null) {
        logger.warn("No internal id provided for resource. It will be ignored");
      } else {
        CloudResource existing = resourceRepository.getByInternalId(internalId);
        if (existing == null) {
          toAdd.add(resource);
        } else {
          if (resource.getResource() != null) {
            if (existing.getResource() != null) {
              if (existing.getResource().getId() != null) {
                resource.getResource().setId(existing.getResource().getId());
                toUpdate.add(resource);
              } else {
                logger.error("Found registered resource " + existing.getInternalId() + " without symbiote id");
              }
            } else {
              logger.error("Found registered resource " + existing.getInternalId() + " without resource information");
            }
          } else {
            logger.error("No resource information provided for resource " + resource.getInternalId());
          }
        }
      }
    });
    
    if (!toAdd.isEmpty()) {
      List<CloudResource> added = iifMessageHandler.createResources(platformId, toAdd);
      result.addAll(resourceRepository.save(added));
      rapresourceRegistrationMessageHandler.sendResourcesRegistrationMessage(added);
    }
    
    if (!toUpdate.isEmpty()) {
      List<CloudResource> updated = iifMessageHandler.updateResources(platformId, toUpdate);
      result.addAll(resourceRepository.save(updated));
      rapresourceRegistrationMessageHandler.sendResourcesUpdateMessage(updated);
    }
    
    return result;
  }

//! Create a resource.
/*!
 * The addResource method stores \a List of \a CloudResource passed as parameter in the  
 * mondodb database and send the information to the \a Interworking Interface and \a Resource Access Proxy component.
 *
 * \param resources List of \a CloudResource to be created within the system
 * \return \a addResource returns the List of \a CloudResource where the Symbiote id is included. 
 * An exception can be thrown when no \a internalId is indicated within the \a CloudResource 
 */
  public List<CloudResource> addResources(List<CloudResource> resource) throws TokenValidationException {
	  return addOrUpdateResources(resource);
  }

//! Update a resource.
/*!
 * The updateResource method updates the Liost of \a CloudResource passed as parameter into the   
 * mondodb database and sends the information to the \a Interworking Interface and \a Resource Access Proxy component.
 *
 * \param resources List of \a CloudResource to be updated within the system
 * \return \a updateResource returns the List \a CloudResource where the Symbiote id is included. 
 */
  public List<CloudResource> updateResources(List<CloudResource>  resources) throws TokenValidationException {
	  return addOrUpdateResources(resources);
  }

//! Delete a resource.
/*!
 * The deleteResource method removes a \a List of \class CloudResource identified by the id passed as a parameter in the \a internalId variable.   
 * It removes it from the mondodb database and request the removal of the information to the \a Interworking Interface and the \a Resource Access Proxy component.
 *
 * \param resourceId \a internalId to the resource to be removed 
 * \return \a deleteResource returns the \a CloudResource that has been just removed 
 */
  public List<CloudResource> deleteResources(List<String> resourceIds) throws TokenValidationException {
	  List<CloudResource> result = null;  
	  List<String> resultIds;

    try {
      resultIds = iifMessageHandler.removeResources(platformId, resourceIds);
    } catch (TokenValidationException e){
      throw e;
    } catch (Exception e){
      throw e;
    }

    result  = deleteInInternalRepository(resultIds);
    rapresourceRegistrationMessageHandler.sendResourcesUnregistrationMessage(resultIds);
    return result;
  }


  public List<CloudResource> getResources() {
    return resourceRepository.findAll();
  }

//! Get a resource.
/*!
 * The getResource method retrieves \a CloudResource identified by \a resourceId 
 * from the mondodb database and will return it.
 *
 * \param resourceId id from the resource to be retrieved from the database
 * \return \a getResource returns the \a CloudResource, 
 */
  public CloudResource getResource(String resourceId) {
	if (!"".equals(resourceId)) {
	     return resourceRepository.getByInternalId(resourceId);
	}
	return null;
  }

}
