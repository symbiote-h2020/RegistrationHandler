/*
 *  Copyright 2018 Atos
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package eu.h2020.symbiote.rh.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import eu.h2020.symbiote.cloud.model.internal.CloudResource;
import eu.h2020.symbiote.cloud.model.internal.FederationInfoBean;
import eu.h2020.symbiote.cloud.model.internal.ResourceSharingInformation;
import eu.h2020.symbiote.rh.PlatformInformationManager;
import eu.h2020.symbiote.rh.constants.RHConstants;
import eu.h2020.symbiote.util.RabbitConstants;
import org.apache.commons.collections4.list.SetUniqueList;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class RegistryDummyListener {

    private static final Log logger = LogFactory.getLog(RegistryDummyListener.class);

    @Autowired
    private Environment env;

    private static Map<String, CloudResource> resourceMap = new HashMap<>();

    private ObjectMapper mapper = new ObjectMapper();

    String exchange;

    private <T> Message toMessage(T payload) throws JsonProcessingException {
        return MessageBuilder.withBody(mapper.writeValueAsBytes(payload)).andProperties(
                MessagePropertiesBuilder.newInstance().setContentType(MessageProperties.CONTENT_TYPE_JSON).build()
        ).build();
    }

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = RabbitConfiguration.REGISTRY_UPDATE_QUEUE_NAME, durable = "false", autoDelete = "true", exclusive = "false"),
            exchange = @Exchange(value = "${" + RabbitConstants.EXCHANGE_PLATFORM_REGISTRY_NAME_PROPERTY + "}",
                    type = ExchangeTypes.DIRECT, durable = "${" + RabbitConstants.EXCHANGE_PLATFORM_REGISTRY_DURABLE_PROPERTY + "}",
                    autoDelete = "${" + RabbitConstants.EXCHANGE_PLATFORM_REGISTRY_AUTODELETE_PROPERTY + "}"),
            key = "${" + RabbitConstants.ROUTING_KEY_PLATFORM_REGISTRY_UPDATE_PROPERTY + "}")
    )
    private List<CloudResource> updateResources(List<CloudResource> resourceList) throws IOException {
        for (CloudResource resource : resourceList) {
            if (!resourceMap.containsKey(resource.getInternalId())) {
                if (resource.getInternalId().equals("internal_trust")) {
                    logger.info("Trust resource not found. Creating federation info");
                }
                FederationInfoBean fedInfo = new FederationInfoBean();
                fedInfo.setAggregationId(UUID.randomUUID().toString());
                resource.setFederationInfo(fedInfo);
            }
            resourceMap.put(resource.getInternalId(), resource);
        }

        return resourceList;
    }

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = RabbitConfiguration.REGISTRY_DELETE_QUEUE_NAME, durable = "false", autoDelete = "true", exclusive = "false"),
            exchange = @Exchange(value = "${" + RabbitConstants.EXCHANGE_PLATFORM_REGISTRY_NAME_PROPERTY + "}",
                    type = ExchangeTypes.DIRECT, durable = "${" + RabbitConstants.EXCHANGE_PLATFORM_REGISTRY_DURABLE_PROPERTY + "}",
                    autoDelete = "${" + RabbitConstants.EXCHANGE_PLATFORM_REGISTRY_AUTODELETE_PROPERTY + "}"),
            key = "${" + RabbitConstants.ROUTING_KEY_PLATFORM_REGISTRY_DELETE_PROPERTY + "}")
    )
    private List<String> deleteResources(List<String> resourceList) throws IOException {
        for (String resource : resourceList) {
            resourceMap.remove(resource);
        }
        return resourceList;
    }

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = RabbitConfiguration.REGISTRY_SHARE_QUEUE_NAME, durable = "false", autoDelete = "true", exclusive = "false"),
            exchange = @Exchange(value = "${" + RabbitConstants.EXCHANGE_PLATFORM_REGISTRY_NAME_PROPERTY + "}",
                    type = ExchangeTypes.DIRECT, durable = "${" + RabbitConstants.EXCHANGE_PLATFORM_REGISTRY_DURABLE_PROPERTY + "}",
                    autoDelete = "${" + RabbitConstants.EXCHANGE_PLATFORM_REGISTRY_AUTODELETE_PROPERTY + "}"),
            key = "${" + RabbitConstants.ROUTING_KEY_PLATFORM_REGISTRY_SHARE_PROPERTY + "}")
    )
    private List<CloudResource> shareResources(Map<String, Map<String, Boolean>> sharingMap) throws IOException {
        List<CloudResource> result = new ArrayList<>();
        for (Map.Entry<String,Map<String, Boolean>> federationInfo : sharingMap.entrySet()) {
            String federation = federationInfo.getKey();
            for (Map.Entry<String, Boolean> resourceInfo : federationInfo.getValue().entrySet()) {
                String resourceId = resourceInfo.getKey();
                CloudResource resource = resourceMap.get(resourceId);
                if (resource != null) {
                    FederationInfoBean fedInfo = resource.getFederationInfo();
                    if (fedInfo == null) {
                        fedInfo = new FederationInfoBean();
                        fedInfo.setAggregationId(UUID.randomUUID().toString());
                        resource.setFederationInfo(fedInfo);
                    }
                    ResourceSharingInformation sharingInformation = new ResourceSharingInformation();
                    sharingInformation.setSymbioteId(UUID.randomUUID().toString());
                    sharingInformation.setBartering(resourceInfo.getValue());
                    sharingInformation.setSharingDate(new Date());
                    fedInfo.getSharingInformation().put(federation, sharingInformation);
                }
                if (! result.contains(resource)) {
                    result.add(resource);
                }
            }
        }
        return result;
    }

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = RabbitConfiguration.REGISTRY_UNSHARE_QUEUE_NAME, durable = "false", autoDelete = "true", exclusive = "false"),
            exchange = @Exchange(value = "${" + RabbitConstants.EXCHANGE_PLATFORM_REGISTRY_NAME_PROPERTY + "}",
                    type = ExchangeTypes.DIRECT, durable = "${" + RabbitConstants.EXCHANGE_PLATFORM_REGISTRY_DURABLE_PROPERTY + "}",
                    autoDelete = "${" + RabbitConstants.EXCHANGE_PLATFORM_REGISTRY_AUTODELETE_PROPERTY + "}"),
            key = "${" + RabbitConstants.ROUTING_KEY_PLATFORM_REGISTRY_UNSHARE_PROPERTY + "}")
    )
    private List<CloudResource> unshareResources(Map<String, List<String>> unshareMap) throws IOException {
        List<CloudResource> result = new ArrayList<>();
        for (Map.Entry<String,List<String>> federationInfo : unshareMap.entrySet()) {
            String federation = federationInfo.getKey();
            for (String resourceId : federationInfo.getValue()) {
                CloudResource resource = resourceMap.get(resourceId);
                if (resource != null) {
                    resource.getFederationInfo().getSharingInformation().remove(federation);
                    if (resource.getFederationInfo().getSharingInformation().isEmpty()) {
                        resource.setFederationInfo(null);
                    }
                    if (!result.contains(resource)) {
                        result.add(resource);
                    }
                }
            }
        }
        return result;
    }

}
