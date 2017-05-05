package eu.h2020.symbiote.rh.messaging.interworkinginterface;

import java.util.List;
import java.util.Map;

import eu.h2020.symbiote.core.model.resources.Resource;
import eu.h2020.symbiote.rh.constants.RHConstants;
import feign.HeaderMap;
import feign.Headers;
import feign.Param;
import feign.RequestLine;


public interface InterworkingInterfaceService {
	@RequestLine("POST "+RHConstants.DO_CREATE_RESOURCES)
	@Headers({"Accept: application/json", "Content-Type: application/json"})
    public List<Resource> createResources(List<Resource> resources, @Param(RHConstants.PLATFORM_ID) String platformId, @HeaderMap Map<String, Object> headers);
	
	@RequestLine("PUT "+RHConstants.DO_UPDATE_RESOURCES)
	@Headers({"Accept: application/json", "Content-Type: application/json"})
    public List<Resource>  updateResource(List<Resource> resources, @Param(RHConstants.PLATFORM_ID) String platformId, @HeaderMap Map<String, Object> headers);
	
	@RequestLine("DELETE "+RHConstants.DO_REMOVE_RESOURCES)
	@Headers({"Accept: application/json", "Content-Type: application/json"})
    public List<String>  removeResources( List<String> resourceIds, @Param(RHConstants.PLATFORM_ID) String platformId, @HeaderMap Map<String, Object> headers);

}

