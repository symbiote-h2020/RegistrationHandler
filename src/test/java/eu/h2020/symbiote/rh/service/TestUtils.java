package eu.h2020.symbiote.rh.service;

import eu.h2020.symbiote.cloud.model.CloudResourceParams;
import eu.h2020.symbiote.cloud.model.internal.CloudResource;
import eu.h2020.symbiote.model.cim.Actuator;
import eu.h2020.symbiote.model.cim.WKTLocation;
import eu.h2020.symbiote.security.accesspolicies.common.AccessPolicyType;
import eu.h2020.symbiote.security.accesspolicies.common.IAccessPolicySpecifier;
import eu.h2020.symbiote.security.accesspolicies.common.singletoken.SingleTokenAccessPolicySpecifier;
import eu.h2020.symbiote.security.commons.exceptions.custom.InvalidArgumentsException;

import java.util.Arrays;

public class TestUtils {

    private static CloudResource createTestCloudResource(String internalId) {
        CloudResource resource = new CloudResource();
        resource.setInternalId(internalId);
        resource.setPluginId("plugin_"+internalId);
        resource.setCloudMonitoringHost("monitoring_"+internalId);
        try {
            IAccessPolicySpecifier testPolicy = new SingleTokenAccessPolicySpecifier(
                    AccessPolicyType.PUBLIC, null
            );
            resource.setAccessPolicy(testPolicy);
        } catch (InvalidArgumentsException e) {
            e.printStackTrace();
        }
        CloudResourceParams params = new CloudResourceParams();
        params.setType("Actuator");
        resource.setParams(params);

        return resource;
    }

    public static CloudResource getTestActuatorBean(String internalId, String name){
        Actuator actuator = new Actuator();
        WKTLocation location = new WKTLocation();
        location.setValue("location");
        actuator.setName(name);
        actuator.setInterworkingServiceURL("http://example.com/url");
        actuator.setDescription(Arrays.asList("Desc"));

        CloudResource cloudResource = createTestCloudResource(internalId);
        cloudResource.setResource(actuator);

        return cloudResource;
    }

}
