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

import eu.h2020.symbiote.core.cci.RDFResourceRegistryRequest;
import eu.h2020.symbiote.core.cci.ResourceRegistryRequest;
import eu.h2020.symbiote.core.cci.ResourceRegistryResponse;
import eu.h2020.symbiote.core.internal.ClearDataResponse;
import eu.h2020.symbiote.rh.constants.RHConstants;

import feign.Headers;
import feign.Param;
import feign.RequestLine;

import org.springframework.web.bind.annotation.RequestBody;

public interface InterworkingInterfaceService {
	@RequestLine("POST "+RHConstants.DO_CREATE_RESOURCES)
	@Headers({"Accept: application/json", "Content-Type: application/json"})
  ResourceRegistryResponse createResources(@Param(RHConstants.PLATFORM_ID) String platformId, @RequestBody ResourceRegistryRequest resources);
	
	@RequestLine("POST "+RHConstants.DO_CREATE_RDF_RESOURCES)
	@Headers({"Accept: application/json", "Content-Type: application/json"})
	ResourceRegistryResponse createRdfResources(@Param(RHConstants.PLATFORM_ID) String platformId, @RequestBody RDFResourceRegistryRequest resources);
	
	@RequestLine("PUT "+RHConstants.DO_UPDATE_RESOURCES)
	@Headers({"Accept: application/json", "Content-Type: application/json"})
	ResourceRegistryResponse updateResource(@Param(RHConstants.PLATFORM_ID) String platformId, @RequestBody ResourceRegistryRequest resources);
	
	@RequestLine("DELETE "+RHConstants.DO_REMOVE_RESOURCES)
	@Headers({"Accept: application/json", "Content-Type: application/json"})
	ResourceRegistryResponse removeResources(@Param(RHConstants.PLATFORM_ID) String platformId, @RequestBody ResourceRegistryRequest resources);
	
	@RequestLine("POST "+RHConstants.DO_CLEAR_DATA)
	@Headers({"Accept: application/json", "Content-Type: application/json"})
	ClearDataResponse clearData(@Param(RHConstants.PLATFORM_ID) String platformId);
	
	
}

