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
