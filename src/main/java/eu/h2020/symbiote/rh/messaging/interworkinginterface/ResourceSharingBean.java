package eu.h2020.symbiote.rh.messaging.interworkinginterface;

import eu.h2020.symbiote.model.cim.Resource;

import java.util.HashMap;
import java.util.Map;

public class ResourceSharingBean {
    private String internalId;
    private Resource resource;
    private Map<String, Boolean> sharingInformation = new HashMap<>();

    public String getInternalId() {
        return internalId;
    }

    public void setInternalId(String internalId) {
        this.internalId = internalId;
    }

    public Resource getResource() {
        return resource;
    }

    public void setResource(Resource resource) {
        this.resource = resource;
    }

    public Map<String, Boolean> getSharingInformation() {
        return sharingInformation;
    }

    public void setSharingInformation(Map<String, Boolean> sharingInformation) {
        this.sharingInformation = sharingInformation;
    }
}
