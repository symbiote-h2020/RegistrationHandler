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

import eu.h2020.symbiote.cloud.model.internal.CloudResource;
import eu.h2020.symbiote.model.cim.Actuator;
import eu.h2020.symbiote.model.cim.WKTLocation;
import eu.h2020.symbiote.security.accesspolicies.common.AccessPolicyType;
import eu.h2020.symbiote.security.accesspolicies.common.IAccessPolicySpecifier;
import eu.h2020.symbiote.security.accesspolicies.common.singletoken.SingleTokenAccessPolicySpecifier;
import eu.h2020.symbiote.security.commons.exceptions.custom.InvalidArgumentsException;

import java.util.Arrays;

public class TestUtils {

    public static final String DEFAULT_INTERWORKING_URL = "http://example.com/url";

    private static CloudResource createTestCloudResource(String internalId) {
        CloudResource resource = new CloudResource();
        resource.setInternalId(internalId);
        resource.setPluginId("plugin_"+internalId);
        try {
            IAccessPolicySpecifier testPolicy = new SingleTokenAccessPolicySpecifier(
                    AccessPolicyType.PUBLIC, null
            );
            resource.setAccessPolicy(testPolicy);
        } catch (InvalidArgumentsException e) {
            e.printStackTrace();
        }

        return resource;
    }

    public static CloudResource getTestActuatorBean(String internalId, String name){
        Actuator actuator = new Actuator();
        WKTLocation location = new WKTLocation();
        location.setValue("location");
        actuator.setName(name);
        actuator.setInterworkingServiceURL(DEFAULT_INTERWORKING_URL);
        actuator.setDescription(Arrays.asList("Desc"));

        CloudResource cloudResource = createTestCloudResource(internalId);
        cloudResource.setResource(actuator);

        return cloudResource;
    }

}
