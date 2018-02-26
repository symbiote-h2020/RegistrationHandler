package eu.h2020.symbiote.rh;

import com.fasterxml.jackson.core.type.TypeReference;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;

import eu.h2020.symbiote.cloud.model.internal.CloudResource;
import eu.h2020.symbiote.cloud.model.internal.RdfCloudResourceList;
import eu.h2020.symbiote.rh.constants.RHConstants;
import eu.h2020.symbiote.rh.db.ResourceRepository;
import eu.h2020.symbiote.rh.exceptions.ConflictException;
import eu.h2020.symbiote.rh.messaging.interworkinginterface.IIFMessageHandler;
import eu.h2020.symbiote.rh.messaging.rabbitmq.RabbitMessageHandler;
import eu.h2020.symbiote.security.commons.exceptions.custom.SecurityHandlerException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
  
  @Autowired
  private RabbitMessageHandler rabbitMessageHandler;

  @Autowired
  private ResourceRepository resourceRepository;

  @Autowired
  private IIFMessageHandler iifMessageHandler;
  
  @Autowired
  MongoTemplate mongoTemplate;

  @Value("${localRegistry.exchange.name}")
  private String registryExchangeName;

  private List<CloudResource> deleteInInternalRepository(List<String> resourceIds){
	  List<CloudResource>  result = new ArrayList<CloudResource>();

	  for (String resourceId:resourceIds) {
		  CloudResource existingResource = resourceRepository.getByInternalId(resourceId);
	      if (existingResource != null) {
	    	  result.add(existingResource);
	    	  resourceRepository.delete(existingResource.getInternalId());
	      }
	  }
	  return result;
  }
  
  private List<CloudResource> addOrUpdateResources(List<CloudResource> resources) throws SecurityHandlerException {
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
      List<CloudResource> added = iifMessageHandler.createResources(toAdd);
      result.addAll(resourceRepository.save(added));
      rabbitMessageHandler.sendMessage(RHConstants.RESOURCE_REGISTRATION_KEY_NAME, added);
    }
    
    if (!toUpdate.isEmpty()) {
      List<CloudResource> updated = iifMessageHandler.updateResources(toUpdate);
      result.addAll(resourceRepository.save(updated));
      rabbitMessageHandler.sendMessage(RHConstants.RESOURCE_UPDATED_KEY_NAME,updated);
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
  public List<CloudResource> addResources(List<CloudResource> resource) throws SecurityHandlerException {
	  return addOrUpdateResources(resource);
  }
  
  public List<CloudResource> addRdfResources(RdfCloudResourceList resources) throws SecurityHandlerException {
  
    resources.getIdMappings().values().forEach(resource ->
    {
      if (resourceRepository.getByInternalId(resource.getInternalId()) != null) {
        throw new ConflictException("Resource with id " + resource.getInternalId() + " already registered. Can't continue with register request.");
    }});
  
    List<CloudResource> newResources = iifMessageHandler.addRdfResources(resources);
    List<CloudResource> updated = resourceRepository.save(newResources);
    if (newResources != null && ! newResources.isEmpty()) {
      rabbitMessageHandler.sendMessage(RHConstants.RESOURCE_REGISTRATION_KEY_NAME, newResources);
    }

    if (updated != null && !updated.isEmpty()) {
      rabbitMessageHandler.sendMessage(RHConstants.RESOURCE_UPDATED_KEY_NAME, newResources);
    }
    return updated;
  }
  
  //! Update a resource.
/*!
 * The updateResource method updates the Liost of \a CloudResource passed as parameter into the   
 * mondodb database and sends the information to the \a Interworking Interface and \a Resource Access Proxy component.
 *
 * \param resources List of \a CloudResource to be updated within the system
 * \return \a updateResource returns the List \a CloudResource where the Symbiote id is included. 
 */
  public List<CloudResource> updateResources(List<CloudResource>  resources) throws SecurityHandlerException {
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
  public List<CloudResource> deleteResources(List<String> resourceIds) throws SecurityHandlerException {
	  List<CloudResource> result = null;  
	  List<String> resultIds;
	  
	  List<CloudResource> found = resourceIds.stream().map(resourceId -> {
	    CloudResource resource = resourceRepository.getByInternalId(resourceId);
	    if ((resource != null) && (resource.getResource() != null)) {
	      return resource;
      } else {
	      return null;
      }
    }).filter(resource -> resource != null).collect(Collectors.toList());
    
    
    resultIds = iifMessageHandler.removeResources(found);
 

    result  = deleteInInternalRepository(resultIds);
    rabbitMessageHandler.sendMessage(RHConstants.RESOURCE_UNREGISTRATION_KEY_NAME, resultIds);
    return result;
  }
  
  public Void clearResources() throws SecurityHandlerException {
    List<CloudResource> existing = resourceRepository.findAll();
    iifMessageHandler.clearData();
    DBCollection collection = mongoTemplate.getCollection(RHConstants.RESOURCE_COLLECTION);
    collection.remove(new BasicDBObject());
    rabbitMessageHandler.sendMessage(RHConstants.RESOURCE_UNREGISTRATION_KEY_NAME, existing.stream().map(
        resource -> resource.getInternalId()).collect(Collectors.toList()));
    return null;
  }
  
  public List<CloudResource> sync() throws SecurityHandlerException {
    List<CloudResource> existing = resourceRepository.findAll();
    clearResources();
    return addResources(existing);
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

  public Map<String, List<CloudResource>> shareResources(Map<String, Map<String, Boolean>> resourceMap) {
    List<CloudResource> updated = (List<CloudResource>) rabbitMessageHandler.sendAndReceive(
            registryExchangeName, RHConstants.RESOURCE_LOCAL_SHARE_KEY_NAME,
            resourceMap, new TypeReference<List<CloudResource>>(){});

    updated = resourceRepository.save(updated);

    rabbitMessageHandler.sendMessage(RHConstants.RESOURCE_LOCAL_UPDATED_KEY_NAME, updated);

    Map<String, List<CloudResource>> result = new HashMap<>();
    for (CloudResource resource : updated) {
      for (String federation : resource.getFederationInfo().keySet()) {
        if (resourceMap.keySet().contains(federation)) {
          List<CloudResource> resourceList = result.get(federation);
          if (resourceList == null) {
            resourceList = new ArrayList<>();
            result.put(federation, resourceList);
          }
          resourceList.add(resource);
        }
      }
    }

    return result;
  }

  public Map<String, List<String>> unshareResources(Map<String, List<String>> resourceMap) {
    List<CloudResource> updated = (List<CloudResource>) rabbitMessageHandler.sendAndReceive(
            registryExchangeName, RHConstants.RESOURCE_LOCAL_UNSHARE_KEY_NAME,
            resourceMap, new TypeReference<List<CloudResource>>(){});

    updated = resourceRepository.save(updated);

    Map<String, List<String>> result = new HashMap<>();
    rabbitMessageHandler.sendMessage(RHConstants.RESOURCE_LOCAL_UPDATED_KEY_NAME, updated);
    for (Map.Entry<String, List<String>> entry : resourceMap.entrySet()) {
      for (String resourceId : entry.getValue()) {
        CloudResource resource = resourceRepository.getByInternalId(resourceId);
        if (resource != null && !resource.getFederationInfo().entrySet().contains(entry.getKey())) {
          List<String> fedElems = result.get(entry.getKey());
          if (fedElems == null) {
            fedElems = new ArrayList<>();
            result.put(entry.getKey(),fedElems);
          }
          fedElems.add(resourceId);
        }
      }
    }

    return result;
  }

  public List<CloudResource> updateLocalResources(List<CloudResource> resourceList) {
    List<CloudResource> toRegiter = new ArrayList<>();
    for (CloudResource resource : resourceList) {
      CloudResource existing = resourceRepository.getByInternalId(resource.getInternalId());
      if (existing != null) {
        resource.getResource().setId(existing.getResource().getId());
        resource.setFederationInfo(existing.getFederationInfo());
      }
      toRegiter.add(resource);
    }

    List<CloudResource> registered = (List<CloudResource>) rabbitMessageHandler.sendAndReceive(
            registryExchangeName, RHConstants.RESOURCE_LOCAL_UPDATE_KEY_NAME, toRegiter,
            new TypeReference<List<CloudResource>>(){});

    registered = resourceRepository.save(registered);

    rabbitMessageHandler.sendMessage(RHConstants.RESOURCE_LOCAL_UPDATED_KEY_NAME, registered);

    return registered;
  }

  public List<String> removeLocalResources(List<String> resourceList) {
    List<String> removed = (List<String>) rabbitMessageHandler.sendAndReceive(
            registryExchangeName, RHConstants.RESOURCE_LOCAL_REMOVE_KEY_NAME, resourceList,
            new TypeReference<List<String>>(){});

    rabbitMessageHandler.sendMessage(RHConstants.RESOURCE_LOCAL_REMOVED_KEY_NAME, removed);

    return removed;
  }
}
