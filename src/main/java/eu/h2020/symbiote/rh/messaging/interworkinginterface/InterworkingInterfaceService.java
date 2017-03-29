package eu.h2020.symbiote.rh.messaging.interworkinginterface;

import java.util.List;

import org.springframework.web.bind.annotation.RequestBody;

import eu.h2020.symbiote.cloud.model.CloudResource;
import eu.h2020.symbiote.rh.constants.RHConstants;
import feign.Headers;
import feign.Param;
import feign.RequestLine;

public interface InterworkingInterfaceService {
	@RequestLine("POST "+RHConstants.DO_CREATE_RESOURCES)
	@Headers({"Accept: application/json", "Content-Type: application/json"})
    public List<CloudResource> createResources(@Param(RHConstants.PLATFORM_ID) String platformId, List<CloudResource> resources);
	
	@RequestLine("PUT "+RHConstants.DO_UPDATE_RESOURCES)
	@Headers({"Accept: application/json", "Content-Type: application/json"})
    public List<CloudResource>  updateResource(@Param(RHConstants.PLATFORM_ID) String platformId, @RequestBody List<CloudResource> resources);
	
	@RequestLine("DELETE "+RHConstants.DO_REMOVE_RESOURCES)
	@Headers({"Accept: application/json", "Content-Type: application/json"})
    public List<String>  removeResources(@Param(RHConstants.PLATFORM_ID) String platformId, @RequestBody List<String> resourceIds);

}

