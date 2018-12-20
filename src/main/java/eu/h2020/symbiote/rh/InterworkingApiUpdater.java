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

package eu.h2020.symbiote.rh;

import eu.h2020.symbiote.cloud.model.internal.CloudResource;
import eu.h2020.symbiote.rh.db.ResourceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class InterworkingApiUpdater implements ApplicationRunner {

    @Value("${symbIoTe.interworking.interface.url}")
    private String interworkingUrl;

    @Autowired
    private ResourceRepository resourceRepository;

    @Autowired
    private PlatformInformationManager infoManager;

    @Override
    public void run(ApplicationArguments args) throws Exception {

        List<CloudResource> updateL1 = new ArrayList<>();
        List<CloudResource> updateL2 = new ArrayList<>();

        List<CloudResource> resources = resourceRepository.findAll();
        if (resources != null && !resources.isEmpty()) {
            resources.stream().filter(resource -> !resource.equals(interworkingUrl)).forEach(resource -> {
                if (resource.getResource() != null && resource.getResource().getId() != null) {
                    updateL1.add(resource);
                } else {
                    if (resource.getFederationInfo() != null && resource.getFederationInfo().getAggregationId() != null) {
                        updateL2.add(resource);
                    }
                }
            });

            if (!updateL1.isEmpty()) {
                infoManager.updateResources(updateL1);
            }

            if (!updateL2.isEmpty()) {
                infoManager.updateLocalResources(updateL2);
            }
        }
    }
}
