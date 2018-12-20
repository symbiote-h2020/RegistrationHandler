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

package eu.h2020.symbiote.rh.constants;

public interface RHConstants {

    String PLATFORM_ID = "platformId";
    String DO_CREATE_RESOURCES = "/platforms/{platformId}/resources";
    String DO_CREATE_RDF_RESOURCES = "/platforms/{platformId}/rdfResources";
    String DO_UPDATE_RESOURCES = "/platforms/{platformId}/resources";
    String DO_REMOVE_RESOURCES = "/platforms/{platformId}/resources";

    String DO_CLEAR_DATA = "/platforms/{platformId}/clearData";

    String RESOURCE_COLLECTION = "cloudResource";
    String DO_SHARE_RESOURCES = "/sharing";

    String RH_RESOURCE_TRUST_UPDATE_QUEUE_NAME = "symbIoTe.trust.rh.resource.update";
}
