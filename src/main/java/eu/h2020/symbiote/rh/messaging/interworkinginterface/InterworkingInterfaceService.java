package eu.h2020.symbiote.rh.messaging.interworkinginterface;

import eu.h2020.symbiote.core.cci.RDFResourceRegistryRequest;
import eu.h2020.symbiote.core.cci.ResourceRegistryRequest;
import eu.h2020.symbiote.core.cci.ResourceRegistryResponse;
import eu.h2020.symbiote.rh.constants.RHConstants;
import eu.h2020.symbiote.security.exceptions.aam.TokenValidationException;

import feign.HeaderMap;
import feign.Headers;
import feign.Param;
import feign.RequestLine;

import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;

public interface InterworkingInterfaceService {
	@RequestLine("POST "+RHConstants.DO_CREATE_RESOURCES)
	@Headers({"Accept: application/json", "Content-Type: application/json"})
  ResourceRegistryResponse createResources(@Param(RHConstants.PLATFORM_ID) String platformId, @RequestBody ResourceRegistryRequest resources, @HeaderMap Map<String, Object> headers) throws TokenValidationException;
	
	@RequestLine("POST "+RHConstants.DO_CREATE_RDF_RESOURCES)
	@Headers({"Accept: application/json", "Content-Type: application/json"})
	ResourceRegistryResponse createRdfResources(@Param(RHConstants.PLATFORM_ID) String platformId, @RequestBody RDFResourceRegistryRequest resources, @HeaderMap Map<String, Object> headers) throws TokenValidationException;
	
	@RequestLine("PUT "+RHConstants.DO_UPDATE_RESOURCES)
	@Headers({"Accept: application/json", "Content-Type: application/json"})
	ResourceRegistryResponse updateResource(@Param(RHConstants.PLATFORM_ID) String platformId, @RequestBody ResourceRegistryRequest resources, @HeaderMap Map<String, Object> headers) throws TokenValidationException;
	
	@RequestLine("DELETE "+RHConstants.DO_REMOVE_RESOURCES)
	@Headers({"Accept: application/json", "Content-Type: application/json"})
	ResourceRegistryResponse removeResources(@Param(RHConstants.PLATFORM_ID) String platformId, @RequestBody ResourceRegistryRequest resources, @HeaderMap Map<String, Object> headers) throws TokenValidationException;

	
}

