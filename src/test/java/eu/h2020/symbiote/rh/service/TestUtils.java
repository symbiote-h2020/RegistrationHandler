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
