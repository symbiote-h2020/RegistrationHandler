package eu.h2020.symbiote.service;

import java.util.List;

import eu.h2020.symbiote.beans.ResourceBean;
import feign.Headers;
import feign.Param;
import feign.RequestLine;

	
public interface RHRestServiceClient {
	@RequestLine("GET /resources")
    @Headers("Content-Type: application/json")
    public List<ResourceBean> getResources(); 

    @RequestLine("GET /resource?resourceInternalId={resourceInternalId}")
    @Headers("Content-Type: application/json")
    public ResourceBean getResource(@Param("resourceInternalId")  String resourceInternalId);

    @RequestLine("POST /resource")
    @Headers("Content-Type: application/json")
    public ResourceBean addResource(ResourceBean resource);

    @RequestLine("PUT /resource")
    @Headers("Content-Type: application/json")
    public ResourceBean updateResource(ResourceBean resource);

    @RequestLine("DELETE /resource?resourceInternalId={resourceInternalId}")
    @Headers("Content-Type: application/json")
    public ResourceBean deleteResource(@Param("resourceInternalId")  String resourceInternalId); 
}
