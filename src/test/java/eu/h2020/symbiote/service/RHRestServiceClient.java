package eu.h2020.symbiote.service;

import java.util.List;

import eu.h2020.symbiote.cloud.model.CloudResource;
import feign.Headers;
import feign.Param;
import feign.RequestLine;

	
public interface RHRestServiceClient {
	@RequestLine("GET /resources")
    @Headers("Content-Type: application/json")
    public List<CloudResource> getResources(); 

    @RequestLine("GET /resource?resourceInternalId={resourceInternalId}")
    @Headers("Content-Type: application/json")
    public CloudResource getResource(@Param("resourceInternalId")  String resourceInternalId);

    @RequestLine("POST /resource")
    @Headers("Content-Type: application/json")
    public CloudResource addResource(CloudResource resource);

    @RequestLine("PUT /resource")
    @Headers("Content-Type: application/json")
    public CloudResource updateResource(CloudResource resource);

    @RequestLine("DELETE /resource?resourceInternalId={resourceInternalId}")
    @Headers("Content-Type: application/json")
    public CloudResource deleteResource(@Param("resourceInternalId")  String resourceInternalId); 
}
