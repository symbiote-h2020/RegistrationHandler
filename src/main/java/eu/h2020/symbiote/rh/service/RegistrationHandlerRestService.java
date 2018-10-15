package eu.h2020.symbiote.rh.service;

import eu.h2020.symbiote.client.ClientConstants;
import eu.h2020.symbiote.cloud.model.internal.CloudResource;
import eu.h2020.symbiote.cloud.model.internal.RdfCloudResourceList;
import eu.h2020.symbiote.rh.PlatformInformationManager;
import eu.h2020.symbiote.rh.exceptions.ConflictException;
import eu.h2020.symbiote.security.commons.exceptions.custom.SecurityHandlerException;

import feign.FeignException;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;
import java.util.Map;


/**
 * REST service to allow registration of resources both in the SymbIoTe core and the local registry
 **/
@RestController
public class RegistrationHandlerRestService {

    private interface ValidatedOperation<T,R> {
        R execute(T input) throws SecurityHandlerException;
    }

    private static final Log logger = LogFactory.getLog(RegistrationHandlerRestService.class);

    @Autowired
    private PlatformInformationManager infoManager;

    /**
     * Gets the whole set of resources that have been registered
     * @return The complete list of resources
     */
    @ApiOperation(value = "Gets the whole set of resources that have been registered" ,
            response = CloudResource.class, responseContainer = "List")
    @RequestMapping(method = RequestMethod.GET, path = "/rh" + ClientConstants.RH_RESOURCES_PATH)
    public List<CloudResource> getResources() {
        logger.info("START OF getResources");
        List<CloudResource>result = infoManager.getResources();
        logger.info("END OF getResources, result "+ result);
        return result;
    }

    /**
     * Get a resource providing its internal ID
     * @param resourceInternalId The internal ID of the resource
     * @return The resource metadata if found
     */
    @ApiOperation(value = "Get a resource providing its internal ID" ,
            response = CloudResource.class)
    @RequestMapping(method = RequestMethod.GET, path = "/rh" + ClientConstants.RH_RESOURCE_PATH)
    public CloudResource getResource(@ApiParam(value = "Internal id of the resource") @RequestParam String resourceInternalId) {
        logger.info("START OF getResource, in data "+ resourceInternalId);
        if ("".equals(resourceInternalId)) throw new ConflictException("resourceInternalId parameter must be informed");
        CloudResource result = infoManager.getResource(resourceInternalId);
        logger.info("END OF getResource, result "+ result);
        return result;
    }

    /**
     * When wanting a result of just one element but using an operation that acts over a list of resources,
     * we just want to return the first of them. This method gets the first one in a safe way.
     * @param result The result of the list operation
     * @param <T> The type of the list
     * @return The result containing just one element
     */
    public <T> ResponseEntity<?> cleanListResult(ResponseEntity<List<T>> result) {
        if (HttpStatus.OK.equals(result.getStatusCode()) &&
                result.hasBody() && result.getBody() != null && !result.getBody().isEmpty()) {
            return new ResponseEntity<T>(result.getBody().get(0), result.getHeaders(),
                    result.getStatusCode());
        } else {
            return result;
        }
    }

    /**
     * Act upon a list of resources calling the Interworking interface API to the function passed as parameter.
     * @param input The input list to pass to the Interworking API
     * @param function The Interworking API method to apply to the input
     * @return The response of the operation
     */
    private <T,R> ResponseEntity<?> modifyResources(T input, ValidatedOperation<T,R> function){
        R result;
        HttpHeaders responseHeaders = new HttpHeaders();
        HttpStatus httpStatus;

        try {
            result = function.execute(input);
        } catch (SecurityHandlerException e) {
            httpStatus = HttpStatus.UNAUTHORIZED;
            return new ResponseEntity<String>("Stored core token was invalid, so it was cleared. Reissue your request and you will automatically get a new core token", responseHeaders, httpStatus);
        } catch (Exception e) {
            if (e instanceof FeignException) {
                httpStatus = HttpStatus.valueOf(((FeignException)e).status());
            } else {
                httpStatus = HttpStatus.BAD_REQUEST;
            }
            String stacktrace = ExceptionUtils.getStackTrace(e);
            return new ResponseEntity<String>("Internal Error: "+stacktrace, responseHeaders, httpStatus);
        }

        logger.info("END OF addResources, result "+ result);
        return new  ResponseEntity<R>(result, responseHeaders, HttpStatus.OK);
    }

    /**
     * Register a resource into the SymbIoTe Core. If the resource is already registered, its metadata will be updated with the input information.
     * @param resource The resource description in JSON
     * @return The resource description with a SymbIoTe ID
     */
    @ApiOperation(value = "Register a resource into the SymbIoTe Core. If the resource is already registered," +
            " its metadata will be updated with the input information." ,
            response = CloudResource.class)
    @RequestMapping(method = RequestMethod.POST, path = "/rh" + ClientConstants.RH_RESOURCE_PATH)
    public ResponseEntity<?> addResource(@ApiParam(value = "The resource description", required = true) @RequestBody CloudResource resource) {
        return cleanListResult((ResponseEntity<List<CloudResource>>)addResources(Arrays.asList(resource)));
    }

    /**
     * Register several resources into the SymbIote Core. If the resources are already registered, their metadata will be updated with the input information.
     * @param resources A JSON array with the description of the resources metadata
     * @return The lis of resources with their assigned SymbIoTe ID
     */
    @ApiOperation(value = "Register several resources into the SymbIote Core. If the resources are already registered," +
            " their metadata will be updated with the input information." ,
            response = CloudResource.class, responseContainer = "List")
    @RequestMapping(method = RequestMethod.POST, path = "/rh" + ClientConstants.RH_RESOURCES_PATH)
    public ResponseEntity<?> addResources(@ApiParam(value = "An array with the description of the resources metadata" ) @RequestBody List<CloudResource> resources) {
        logger.info("START OF addResource, in data "+ resources);
        return modifyResources(resources, (resourceList -> infoManager.addResources(resources)));
    }

    /**
     * Register RDF resources into the core
     * @param resources List of resources to register
     * @return A list of the resources registered in JSON
     * @throws ConflictException If some resources have already been registered
     */
    @ApiOperation(value = "Register RDF resources into the core" ,
            response = CloudResource.class, responseContainer = "List")
    @RequestMapping(method = RequestMethod.POST, path = "/rh" + ClientConstants.RH_RDF_RESOURCES_PATH)
    public ResponseEntity<?> addRdfResources(@ApiParam(value = "List of resources to be registered ") @RequestBody RdfCloudResourceList resources) throws ConflictException{
        return modifyResources(resources, (input -> infoManager.addRdfResources((RdfCloudResourceList) input)));
    }

    /**
     * Update the metadata of a previously registered resource in the core. If the resource is not registered, it will be done by this operation.
     * @param resource The resource description to update.
     * @return The updated description with a new SymbIoTe ID if it was not previously registered.
     */
    @ApiOperation(value = "Update the metadata of a previously registered resource in the core. If the resource is not registered, it will be done by this operation" ,
            response = CloudResource.class)
    @RequestMapping(method = RequestMethod.PUT, path = "/rh" + ClientConstants.RH_RESOURCE_PATH)
    public ResponseEntity<?> updateResource(@ApiParam(value = "The resource description to be updated") @RequestBody CloudResource resource) {
        return cleanListResult((ResponseEntity<List<CloudResource>>)updateResources(Arrays.asList(resource)));
    }

    /**
     * Update the metadata of previously registered resources in the core. If the resources are not registered, they will be registered by this operation.
     * @param resources Te list of resources description to update.
     * @return The updated descriptions with a new SymbIoTe ID if they were not previously registered.
     */
    @ApiOperation(value = "Update the metadata of previously registered resources in the core. If the resources are not registered, they will be registered by this operation" ,
            response = CloudResource.class, responseContainer = "List")
    @RequestMapping(method = RequestMethod.PUT, path = "/rh" + ClientConstants.RH_RESOURCES_PATH)
    public ResponseEntity<?> updateResources(@ApiParam(value = "The list of resource descriptions to be updated") @RequestBody List<CloudResource> resources) {
        logger.info("START OF updateResource, in data "+ resources);
        return modifyResources(resources, (resourceList -> infoManager.updateResources(resources)));
    }

    /**
     * Execute a sync operation. This operation will delete all resources of this platform in the core and re-register the local ones in the database.
     * @return This operation does not return anything.
     */
    @ApiOperation(value = "Execute a sync operation. This operation will delete all resources of this platform in the core and re-register the local ones in the database" ,
            response = CloudResource.class, responseContainer = "List")
    @RequestMapping(method = RequestMethod.PUT, path = "/rh" + "/sync")
    public ResponseEntity<?> sync() {
        logger.info("START OF core synchronization");
        return modifyResources(Void.TYPE, (voidObject -> infoManager.sync()));
    }

    /**
     * Delete a single resource from the core providing its internal ID.
     * @param resourceInternalId The internal ID of the resource to delete.
     * @return The deleted resource metadata.
     */
    @ApiOperation(value = "Delete a single resource from the core providing its internal ID" ,
            response = CloudResource.class)
    @RequestMapping(method = RequestMethod.DELETE, path = "/rh" + ClientConstants.RH_RESOURCE_PATH)
    public ResponseEntity<?> deleteResource(@ApiParam(value = "Internal id of the resource to be deleted") @RequestParam String resourceInternalId) {
        return cleanListResult((ResponseEntity<List<CloudResource>>)deleteResources(Arrays.asList(resourceInternalId)));
    }

    /**
     * Delete several resources from the core prividing their internal IDs
     * @param resourceInternalIds A comma separated list of internal IDs of the resources to delete.
     * @return The resource metadata of the deleted ones.
     */
    @ApiOperation(value = "Delete several resources from the core prividing their internal IDs" ,
            response = CloudResource.class, responseContainer = "List")
    @RequestMapping(method = RequestMethod.DELETE, path = "/rh" + ClientConstants.RH_RESOURCES_PATH)
    public ResponseEntity<?> deleteResources(@ApiParam(value = "Internal ids of the resources to be deleted") @RequestParam List<String> resourceInternalIds) {
        logger.info("START OF deleteResource, in data "+ resourceInternalIds);
        return modifyResources(resourceInternalIds, (resourceList -> infoManager.deleteResources(resourceInternalIds)));
    }

    /**
     * Delete all resources in the local database and the core. USE WITH CAUTION!!!
     * @return This method does not return anything.
     */
    @ApiOperation(value = "Delete all resources in the local database and the core. USE WITH CAUTION!!!" ,
            response = CloudResource.class, responseContainer = "List")
    @RequestMapping(method = RequestMethod.DELETE, path = "/rh" + ClientConstants.RH_CLEAR_PATH)
    public ResponseEntity<?> clearResources() {
        logger.info("START OF clear resources");
        return modifyResources(Void.TYPE, (voidObject -> infoManager.clearResources()));
    }

    /**
     * Update the metadata of resources in the local registry. This operation will register this metadata if it's not already registered but it won't change how it's shared with different federations.
     * @param input The list of resources metadata to update
     * @return The list of updated resources
     */
    @ApiOperation(value = "Update the metadata of resources in the local registry. This operation will register this " +
            "metadata if it's not already registered but it won't change how it's shared with different federations" ,
            response = CloudResource.class, responseContainer = "List")
    @RequestMapping(method = {RequestMethod.POST, RequestMethod.PUT}, path = "/rh" + ClientConstants.RH_LOCAL_RESOURCES_PATH)
    public ResponseEntity<?> updateLocalResources(@ApiParam(value = "The list of resources metadata to be updated") @RequestBody  List<CloudResource> input) {
        return modifyResources(input, (resourceList -> infoManager.updateLocalResources(resourceList)));
    }

    /**
     * Remove resource metadata from the local registry. If this resources were shared with some federations, they will be removed from those federations as well.
     * @param resourceIds The list of resource internal IDs to remove
     * @return The list of resources that were removed
     */
    @ApiOperation(value = "Remove resource metadata from the local registry. If these resources were shared with some " +
            "federations, they will be removed from those federations as well" ,
            response = CloudResource.class, responseContainer = "List")
    @RequestMapping(method = RequestMethod.DELETE, path = "/rh" + ClientConstants.RH_LOCAL_RESOURCES_PATH)
    public ResponseEntity<?> removeLocalResources(@ApiParam(value = "The list of resource internal IDs to be removed") @RequestParam List<String> resourceIds) {
        return modifyResources(resourceIds, (resourceList -> infoManager.removeLocalResources(resourceList)));
    }

    /**
     * Share resources with federations.
     * @param input A JSON object whose keys are the federation Ids. As value, there's another object with the resource internal Id and if the resource should be shared by bartering for that federation.
     * @return A JSON object whose key is the federation Id and the value is a list of the resource's metadata which have been shared to that federation.
     */
    @ApiOperation(value = "Share resources with federations. The return value is a JSON object whose key is the federation" +
            " Id and the value is a list of the resource's metadata which have been shared to that federation" ,
            response = CloudResource.class, responseContainer = "List")
    @RequestMapping(method = RequestMethod.PUT, path = "/rh" + ClientConstants.RH_LOCAL_RESOURCES_SHARE_PATH)
    public ResponseEntity<?> shareResources(@ApiParam(value = "A JSON object whose keys are the federation Ids. As value," +
            " there's another object with the resource internal Id and if the resource should be shared by bartering for that federation")
                                                @RequestBody Map<String, Map<String, Boolean>> input) {
        return modifyResources(input, (resourceMap -> infoManager.shareResources(resourceMap)));
    }

    /**
     * Remove resources previously shared in a federation.
     * @param input A JSON object whose keys are the federation Ids and each value is a list of resource internal id's to remove from that federation.
     * @return A JSON object whose keys are federation Ids and each value is a lis of resource metadata from the resources that were removed from that federation.
     */
    @ApiOperation(value = "Remove resources previously shared in a federation. The return value is a JSON object whose" +
            " keys are federation Ids and each value is a lis of resource metadata from the resources that were removed" +
            " from that federation" ,
            response = CloudResource.class, responseContainer = "List")
    @RequestMapping(method = RequestMethod.DELETE, path = "/rh" + ClientConstants.RH_LOCAL_RESOURCES_SHARE_PATH)
    public ResponseEntity<?> unshareResources(@ApiParam(value = "A JSON object whose keys are the federation Ids and each" +
            " value is a list of resource internal id's to remove from that federation") @RequestBody Map<String, List<String>> input) {
        return modifyResources(input, (resourceMap -> infoManager.unshareResources(resourceMap)));
    }
}
