package eu.h2020.symbiote.rh;

import com.fasterxml.jackson.core.type.TypeReference;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;

import eu.h2020.symbiote.cloud.model.ResourceLocalSharingMessage;
import eu.h2020.symbiote.cloud.model.internal.CloudResource;
import eu.h2020.symbiote.cloud.model.internal.RdfCloudResourceList;
import eu.h2020.symbiote.cloud.trust.model.TrustEntry;
import eu.h2020.symbiote.rh.constants.RHConstants;
import eu.h2020.symbiote.rh.db.ResourceRepository;
import eu.h2020.symbiote.rh.exceptions.ConflictException;
import eu.h2020.symbiote.rh.messaging.interworkinginterface.IIFMessageHandler;
import eu.h2020.symbiote.rh.messaging.rabbitmq.RabbitMessageHandler;
import eu.h2020.symbiote.rh.util.LazyListMap;
import eu.h2020.symbiote.security.commons.exceptions.custom.SecurityHandlerException;

import eu.h2020.symbiote.util.RabbitConstants;
import org.apache.commons.collections4.Factory;
import org.apache.commons.collections4.map.LazyMap;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

import java.util.*;
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

  @Value("${" + RabbitConstants.EXCHANGE_PLATFORM_REGISTRY_NAME_PROPERTY + "}")
  private String registryExchangeName;

  @Value("${" + RabbitConstants.ROUTING_KEY_RH_REGISTER_PROPERTY + "}")
  private String resourceRegistrationCoreKey;

  @Value("${" + RabbitConstants.ROUTING_KEY_RH_UPDATE_PROPERTY + "}")
  private String resourceUpdateCoreKey;

  @Value("${" + RabbitConstants.ROUTING_KEY_RH_DELETE_PROPERTY + "}")
  private String resourceDeleteCoreKey;

  @Value("${" + RabbitConstants.ROUTING_KEY_PLATFORM_REGISTRY_SHARE_PROPERTY + "}")
  private String resourceShareKey;

  @Value("${" + RabbitConstants.ROUTING_KEY_PLATFORM_REGISTRY_UNSHARE_PROPERTY + "}")
  private String resourceUnshareKey;

  @Value("${" + RabbitConstants.ROUTING_KEY_PLATFORM_REGISTRY_UPDATE_PROPERTY + "}")
  private String resourceLocalUpdateKey;

  @Value("${" + RabbitConstants.ROUTING_KEY_PLATFORM_REGISTRY_DELETE_PROPERTY + "}")
  private String resourceLocalDeleteKey;

  @Value("${" + RabbitConstants.ROUTING_KEY_RH_SHARED_PROPERTY + "}")
  private String resourceSharedNotificationKey;

  @Value("${" + RabbitConstants.ROUTING_KEY_RH_UNSHARED_PROPERTY + "}")
  private String resourceUnsharedNotificationKey;

  @Value("${" + RabbitConstants.ROUTING_KEY_RH_UPDATED_PROPERTY + "}")
  private String resourceLocalUpdatedNotificationKey;

  @Value("${" + RabbitConstants.ROUTING_KEY_RH_DELETED_PROPERTY + "}")
  private String resourceLocalDeletedNotificationKey;

  private List<CloudResource> deleteInInternalRepository(List<String> resourceIds){
	  List<CloudResource>  result = new ArrayList<CloudResource>();
      List<CloudResource> toUpdate = new ArrayList<>();
      List<CloudResource> toRemove = new ArrayList<>();

	  for (String resourceId:resourceIds) {
		  CloudResource existingResource = resourceRepository.getByInternalId(resourceId);
	      if (existingResource != null) {
	    	  result.add(existingResource);

	    	  if (existingResource.getFederationInfo() != null &&
                      existingResource.getFederationInfo().getAggregationId() != null) {

	    	    // It's registered at L2, update only SymbIoTeId
                existingResource.getResource().setId(null);
                toUpdate.add(existingResource);

              } else {
                toRemove.add(existingResource);
              }
	      }
	  }

	  resourceRepository.save(toUpdate);
	  resourceRepository.delete(toRemove);

	  return result;
  }

  private List<CloudResource> doAddOrUpdateResources(List<CloudResource> resources, boolean updateL2)
          throws SecurityHandlerException {
    List<CloudResource> toAdd = new ArrayList<>();
    List<CloudResource> toUpdate = new ArrayList<>();
    List<String> toUpdateL2 = new ArrayList<>();

    List<CloudResource> result = new ArrayList<>();

    resources.stream().forEach(resource -> {
      String internalId = resource.getInternalId();
      if (internalId == null) {
        logger.warn("No internal id provided for resource. It will be ignored");
      } else {
        if (resource.getResource() != null) {
          CloudResource existing = resourceRepository.getByInternalId(internalId);
          if (existing == null) {
            toAdd.add(resource);
          } else {
            if (existing.getResource() != null) {

              // Add L2 information just in case
              if (existing.getFederationInfo() != null) {
                resource.setFederationInfo(existing.getFederationInfo());
                toUpdateL2.add(resource.getInternalId());
              }

              if (existing.getResource().getId() != null) {
                resource.getResource().setId(existing.getResource().getId());
                toUpdate.add(resource);
              } else {
                // It might be registered at L2 but not L1
                toAdd.add(resource);
              }

            } else {
              logger.error("Found registered resource " + existing.getInternalId() + " without resource information");
            }
          }
        } else {
          logger.error("No resource information provided for resource " + resource.getInternalId() + " it will be ignored");
        }
      }
    });

    if (!toAdd.isEmpty()) {
      List<CloudResource> added = iifMessageHandler.createResources(toAdd);
      result.addAll(resourceRepository.save(added));
      rabbitMessageHandler.sendMessage(resourceRegistrationCoreKey, added);
    }

    if (!toUpdate.isEmpty()) {
      List<CloudResource> updated = iifMessageHandler.updateResources(toUpdate);
      result.addAll(resourceRepository.save(updated));
      rabbitMessageHandler.sendMessage(resourceUpdateCoreKey,updated);
    }

    if (updateL2) {
      doUpdateLocalResources(result.stream().filter(resource -> toUpdateL2.contains(resource.getInternalId()))
              .collect(Collectors.toList()), false);
    }

    return result;
  }
  
  private List<CloudResource> addOrUpdateResources(List<CloudResource> resources) throws SecurityHandlerException {
    return doAddOrUpdateResources(resources, true);
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
      rabbitMessageHandler.sendMessage(resourceRegistrationCoreKey, newResources);
    }

    if (updated != null && !updated.isEmpty()) {
      rabbitMessageHandler.sendMessage(resourceUpdateCoreKey, newResources);
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
    rabbitMessageHandler.sendMessage(resourceDeleteCoreKey, resultIds);
    return result;
  }
  
  public Void clearResources() throws SecurityHandlerException {
    List<CloudResource> existing = resourceRepository.findAll();
    iifMessageHandler.clearData();
    removeLocalResources(existing.stream().map(resource -> resource.getInternalId()).collect(Collectors.toList()));
    resourceRepository.deleteAll();
    rabbitMessageHandler.sendMessage(resourceDeleteCoreKey, existing.stream().map(
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

  private void updateL1ResourceInformation(List<CloudResource> updated) {
    updated.forEach(updatedResource -> {
      CloudResource existing = resourceRepository.getByInternalId(updatedResource.getInternalId());
      if (existing != null && existing.getResource() != null) {
        updatedResource.setResource(existing.getResource());
      }
    });
  }

  public Map<String, List<CloudResource>> shareResources(Map<String, Map<String, Boolean>> resourceMap) {

    Set<String> resources = resourceMap.values().stream().map(fedMap -> fedMap.keySet()).flatMap(Collection::stream)
            .collect(Collectors.toSet());

    //Register the ones we know but they are not in the registry yet
    updateLocalResources(resources.stream().map(resourceId -> resourceRepository.getByInternalId(resourceId))
            .filter(resource -> resource != null && resource.getFederationInfo() == null).collect(Collectors.toList()));

    List<CloudResource> updated = (List<CloudResource>) rabbitMessageHandler.sendAndReceive(
            registryExchangeName, resourceShareKey,
            resourceMap, new TypeReference<List<CloudResource>>(){});

    updateL1ResourceInformation(updated);
    updated = resourceRepository.save(updated);

    Map<String, List<CloudResource>> result = new LazyListMap<>();

    for (CloudResource resource : updated) {
      for (String federation : resource.getFederationInfo() .getSharingInformation().keySet()) {
        if (resourceMap.keySet().contains(federation)) {
          List<CloudResource> resourceList = result.get(federation);
          resourceList.add(resource);
        }
      }
    }

    rabbitMessageHandler.sendMessage(resourceSharedNotificationKey,
            new ResourceLocalSharingMessage(result));

    return result;
  }

  public Map<String, List<CloudResource>> unshareResources(Map<String, List<String>> resourceMap) {
    List<CloudResource> updated = (List<CloudResource>) rabbitMessageHandler.sendAndReceive(
            registryExchangeName, resourceUnshareKey,
            resourceMap, new TypeReference<List<CloudResource>>(){});

    updateL1ResourceInformation(updated);
    updated = resourceRepository.save(updated);

    Map<String, List<CloudResource>> result = new LazyListMap<>();
    
    for (Map.Entry<String, List<String>> entry : resourceMap.entrySet()) {
      for (String resourceId : entry.getValue()) {
        CloudResource resource = resourceRepository.getByInternalId(resourceId);
        if (resource != null && !resource.getFederationInfo().getSharingInformation().entrySet().contains(entry.getKey())) {
          List<CloudResource> fedElems = result.get(entry.getKey());
          fedElems.add(resource);
        }
      }
    }

    rabbitMessageHandler.sendMessage(resourceUnsharedNotificationKey,
            new ResourceLocalSharingMessage(result));

    return result;
  }

  public List<CloudResource> updateLocalResources(List<CloudResource> resourceList) {

    return doUpdateLocalResources(resourceList, true);

  }

  private void updateSharePartial(CloudResource source, CloudResource target, CloudResource resource,
                                  Map<String,List<CloudResource>> shareMap) {

    if (source != null && source.getFederationInfo() != null && source.getFederationInfo().getSharingInformation() != null) {
      if (target != null && target.getFederationInfo() != null && target.getFederationInfo().getSharingInformation() != null) {
        for (String fedId : source.getFederationInfo().getSharingInformation().keySet()) {
          if (!target.getFederationInfo().getSharingInformation().keySet().contains(fedId)) {
            shareMap.get(fedId).add(resource);
          }
        }
      } else {
        source.getFederationInfo().getSharingInformation().keySet()
                .forEach(fedId -> shareMap.get(fedId).add(resource));
      }
    }

  }

  private void updateSharingInformation(CloudResource existing, CloudResource resource,
                                        Map<String,List<CloudResource>> newShare,
                                        Map<String,List<CloudResource>> newUnshare) {

    updateSharePartial(existing, resource, resource, newUnshare);

    updateSharePartial(resource, existing, resource, newShare);
  }

  private List<CloudResource> doUpdateLocalResources(List<CloudResource> toRegiter, boolean updateL1) {
    List<CloudResource> registered = (List<CloudResource>) rabbitMessageHandler.sendAndReceive(
            registryExchangeName, resourceLocalUpdateKey, toRegiter,
            new TypeReference<List<CloudResource>>(){});


    Map<String, List<CloudResource>> newShare = LazyMap.lazyMap(new HashMap<>(), (fact -> new ArrayList<>()));

    Map<String, List<CloudResource>> newUnshare = LazyMap.lazyMap(new HashMap<>(), (fact -> new ArrayList<>()));


    for (CloudResource resource : registered) {
      CloudResource existing = resourceRepository.getByInternalId(resource.getInternalId());
      if (existing != null && existing.getResource() != null && existing.getResource().getId() != null) {
        resource.getResource().setId(existing.getResource().getId());
      }

      updateSharingInformation(existing, resource, newShare, newUnshare);
    }

    registered = resourceRepository.save(registered);

    rabbitMessageHandler.sendMessage(resourceLocalUpdatedNotificationKey, registered);

    if (!newShare.isEmpty()) {
      rabbitMessageHandler.sendMessage(resourceSharedNotificationKey, newShare);
    }

    if (!newUnshare.isEmpty()) {
      rabbitMessageHandler.sendMessage(resourceUnsharedNotificationKey, newUnshare);
    }

    if (updateL1) {
      try {
        doAddOrUpdateResources(registered.stream().filter(resource -> resource.getResource().getId() != null)
                .collect(Collectors.toList()), false);
      } catch (SecurityHandlerException e) {
        logger.error("Error updating L1 resources", e);
      }
    }

    return registered;
  }

  public CloudResource updateTrustValue(TrustEntry trustValue) {
    CloudResource resource = resourceRepository.getByInternalId(trustValue.getResourceId());
    if (resource != null && resource.getFederationInfo() != null) {
      resource.getFederationInfo().setResourceTrust(trustValue.getValue());
      doUpdateLocalResources(Arrays.asList(resource), false);
    }
    return resource;
  }

  public List<String> removeLocalResources(List<String> resourceList) {
    List<String> removed = (List<String>) rabbitMessageHandler.sendAndReceive(
            registryExchangeName, resourceLocalDeleteKey, resourceList,
            new TypeReference<List<String>>(){});

    for (String resourceId : removed){
      CloudResource existing = resourceRepository.getByInternalId(resourceId);
      if (existing.getResource().getId() == null) {
        resourceRepository.delete(existing);
      }
    }
    rabbitMessageHandler.sendMessage(resourceLocalDeletedNotificationKey, removed);

    return removed;
  }
}
