package eu.h2020.symbiote.rh.service;

import eu.h2020.symbiote.cloud.model.internal.CloudResource;
import eu.h2020.symbiote.cloud.model.internal.ResourceSharingInformation;
import eu.h2020.symbiote.rh.constants.RHConstants;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class RegistryDummyListener {

    private Map<String, CloudResource> resourceMap = new HashMap<>();

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = FederationsTest.REGISTRY_UPDATE_QUEUE_NAME, durable = "false", autoDelete = "true", exclusive = "true"),
            exchange = @Exchange(value = FederationsTest.REGISTRY_EXCHANGE_TEST_NAME, type = ExchangeTypes.DIRECT, durable = "false", autoDelete = "true"),
            key = RHConstants.RESOURCE_LOCAL_UPDATE_KEY_NAME)
    )
    private List<CloudResource> updateResources(List<CloudResource> resourceList) {
        for (CloudResource resource : resourceList) {
            resourceMap.put(resource.getInternalId(), resource);
        }
        return resourceList;
    }

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = FederationsTest.REGISTRY_DELETE_QUEUE_NAME, durable = "false", autoDelete = "true", exclusive = "true"),
            exchange = @Exchange(value = FederationsTest.REGISTRY_EXCHANGE_TEST_NAME, type = ExchangeTypes.DIRECT, durable = "false", autoDelete = "true"),
            key = RHConstants.RESOURCE_LOCAL_REMOVE_KEY_NAME)
    )
    private List<String> deleteResources(List<String> resourceList) {
        for (String resource : resourceList) {
            resourceMap.remove(resource);
        }
        return resourceList;
    }

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = FederationsTest.REGISTRY_SHARE_QUEUE_NAME, durable = "false", autoDelete = "true", exclusive = "true"),
            exchange = @Exchange(value = FederationsTest.REGISTRY_EXCHANGE_TEST_NAME, type = ExchangeTypes.DIRECT, durable = "false", autoDelete = "true"),
            key = RHConstants.RESOURCE_LOCAL_SHARE_KEY_NAME)
    )
    private List<CloudResource> shareResources(Map<String, Map<String, Boolean>> sharingMap) {
        List<CloudResource> result = new ArrayList<>();
        for (Map.Entry<String,Map<String, Boolean>> federationInfo : sharingMap.entrySet()) {
            String federation = federationInfo.getKey();
            for (Map.Entry<String, Boolean> resourceInfo : federationInfo.getValue().entrySet()) {
                String resourceId = resourceInfo.getKey();
                CloudResource resource = resourceMap.get(resourceId);
                if (resource != null) {
                    ResourceSharingInformation sharingInformation = new ResourceSharingInformation();
                    sharingInformation.setSymbioteId(UUID.randomUUID().toString());
                    sharingInformation.setBartering(resourceInfo.getValue());
                    sharingInformation.setSharingDate(new Date());
                    resource.getFederationInfo().put(federation, sharingInformation);
                    if (!result.contains(resource)) {
                        result.add(resource);
                    }
                }
            }
        }
        return result;
    }

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = FederationsTest.REGISTRY_UNSHARE_QUEUE_NAME, durable = "false", autoDelete = "true", exclusive = "true"),
            exchange = @Exchange(value = FederationsTest.REGISTRY_EXCHANGE_TEST_NAME, type = ExchangeTypes.DIRECT, durable = "false", autoDelete = "true"),
            key = RHConstants.RESOURCE_LOCAL_UNSHARE_KEY_NAME)
    )
    private List<CloudResource> unshareResources(Map<String, List<String>> unshareMap) {
        List<CloudResource> result = new ArrayList<>();
        for (Map.Entry<String,List<String>> federationInfo : unshareMap.entrySet()) {
            String federation = federationInfo.getKey();
            for (String resourceId : federationInfo.getValue()) {
                CloudResource resource = resourceMap.get(resourceId);
                if (resource != null) {
                    resource.getFederationInfo().remove(federation);
                    if (!result.contains(resource)) {
                        result.add(resource);
                    }
                }
            }
        }
        return result;
    }

}
