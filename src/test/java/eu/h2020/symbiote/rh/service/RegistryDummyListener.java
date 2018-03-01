package eu.h2020.symbiote.rh.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import eu.h2020.symbiote.cloud.model.internal.CloudResource;
import eu.h2020.symbiote.cloud.model.internal.ResourceSharingInformation;
import eu.h2020.symbiote.rh.constants.RHConstants;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;

@Service
public class RegistryDummyListener {

    private Map<String, CloudResource> resourceMap = new HashMap<>();

    private ObjectMapper mapper = new ObjectMapper();

    private <T> Message toMessage(T payload) throws JsonProcessingException {
        return MessageBuilder.withBody(mapper.writeValueAsBytes(payload)).andProperties(
                MessagePropertiesBuilder.newInstance().setContentType(MessageProperties.CONTENT_TYPE_JSON).build()
        ).build();
    }

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = RabbitConfiguration.REGISTRY_UPDATE_QUEUE_NAME, durable = "false", autoDelete = "true", exclusive = "false"),
            exchange = @Exchange(value = RabbitConfiguration.REGISTRY_EXCHANGE_TEST_NAME, type = ExchangeTypes.DIRECT, durable = "false", autoDelete = "true"),
            key = RHConstants.RESOURCE_LOCAL_UPDATE_KEY_NAME)
    )
    private List<CloudResource> updateResources(List<CloudResource> resourceList) throws IOException {
        for (CloudResource resource : resourceList) {
            resourceMap.put(resource.getInternalId(), resource);
        }

        return resourceList;
    }

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = RabbitConfiguration.REGISTRY_DELETE_QUEUE_NAME, durable = "false", autoDelete = "true", exclusive = "false"),
            exchange = @Exchange(value = RabbitConfiguration.REGISTRY_EXCHANGE_TEST_NAME, type = ExchangeTypes.DIRECT, durable = "false", autoDelete = "true"),
            key = RHConstants.RESOURCE_LOCAL_REMOVE_KEY_NAME)
    )
    private List<String> deleteResources(List<String> resourceList) throws IOException {
        for (String resource : resourceList) {
            resourceMap.remove(resource);
        }
        return resourceList;
    }

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = RabbitConfiguration.REGISTRY_SHARE_QUEUE_NAME, durable = "false", autoDelete = "true", exclusive = "false"),
            exchange = @Exchange(value = RabbitConfiguration.REGISTRY_EXCHANGE_TEST_NAME, type = ExchangeTypes.DIRECT, durable = "false", autoDelete = "true"),
            key = RHConstants.RESOURCE_LOCAL_SHARE_KEY_NAME)
    )
    private List<CloudResource> shareResources(Map<String, Map<String, Boolean>> sharingMap) throws IOException {
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
            value = @Queue(value = RabbitConfiguration.REGISTRY_UNSHARE_QUEUE_NAME, durable = "false", autoDelete = "true", exclusive = "false"),
            exchange = @Exchange(value = RabbitConfiguration.REGISTRY_EXCHANGE_TEST_NAME, type = ExchangeTypes.DIRECT, durable = "false", autoDelete = "true"),
            key = RHConstants.RESOURCE_LOCAL_UNSHARE_KEY_NAME)
    )
    private List<CloudResource> unshareResources(Map<String, List<String>> unshareMap) throws IOException {
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
