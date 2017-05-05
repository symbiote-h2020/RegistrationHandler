package eu.h2020.symbiote.rh.messaging.interworkinginterface;

import java.util.List;
import java.util.Map;
import org.springframework.web.bind.annotation.RequestBody;

import eu.h2020.symbiote.core.model.resources.Resource;
import eu.h2020.symbiote.rh.constants.RHConstants;
import feign.HeaderMap;
import feign.Headers;
import feign.Param;
import feign.RequestLine;

import eu.h2020.symbiote.core.cci.ResourceRegistryRequest;
import eu.h2020.symbiote.core.cci.ResourceRegistryResponse;

public interface InterworkingInterfaceService {
	@RequestLine("POST "+RHConstants.DO_CREATE_RESOURCES)
	@Headers({"Accept: application/json", "Content-Type: application/json"})
    public ResourceRegistryResponse createResources(@Param(RHConstants.PLATFORM_ID) String platformId, @RequestBody ResourceRegistryRequest resources, @HeaderMap Map<String, Object> headers);
	
	@RequestLine("PUT "+RHConstants.DO_UPDATE_RESOURCES)
	@Headers({"Accept: application/json", "Content-Type: application/json"})
    public ResourceRegistryResponse  updateResource(@Param(RHConstants.PLATFORM_ID) String platformId, @RequestBody ResourceRegistryRequest resources, @HeaderMap Map<String, Object> headers);
	
	@RequestLine("DELETE "+RHConstants.DO_REMOVE_RESOURCES)
	@Headers({"Accept: application/json", "Content-Type: application/json"})
    public ResourceRegistryResponse  removeResources(@Param(RHConstants.PLATFORM_ID) String platformId, @RequestBody ResourceRegistryRequest resources, @HeaderMap Map<String, Object> headers);

}

