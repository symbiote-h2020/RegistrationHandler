package eu.h2020.symbiote.rh.service;

import java.util.ArrayList;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpHeaders;

import eu.h2020.symbiote.cloud.model.internal.CloudResource;
import eu.h2020.symbiote.rh.PlatformInformationManager;
import eu.h2020.symbiote.rh.exceptions.ConflictException;
import eu.h2020.symbiote.security.exceptions.aam.TokenValidationException;
import io.swagger.annotations.*;

/**! \class RegistrationHandlerRestService 
 * \brief RegistrationHandlerRestService Rest controller for registration handler
 * This class implements the rest interfaces.  It is the class that will receive any REST call done to the registration handler 
 **/
/*
 * @author: jose, Elena Garrido, Fernando Campos
 * @version: 27/09/2016
 */
@RestController
@Api(tags = "RegistrationHandlerRestService", description = "Operations of Registration Handler")
public class RegistrationHandlerRestService {
  private static final Log logger = LogFactory.getLog(RegistrationHandlerRestService.class);
  
  @Autowired
  private PlatformInformationManager infoManager;

  /**
   * Endpoint form querying a List of all CloudResource. Not need passed any parameters 
   * 
   * @return list of CloudResources
   */
  @RequestMapping(method = RequestMethod.GET, path = "/resources")
  @ApiOperation(value = "HTTP GET getResources",
  			notes = "Search for resources List using HTTP GET. Without parameters",
  			response = List.class)
  @ApiResponses(value = {
		  @ApiResponse(code = 500, message = "Resource Not Found") })
  public List<CloudResource> getResources() {
    logger.info("START OF getResources");
    List<CloudResource>result = infoManager.getResources();
    logger.info("END OF getResources, result "+ result);
    return result;
  }
  
  //! get a resource.
  /**
   * Endpoint for getResource method retrieves a CloudResource identified by a resourceInternalId param 
   * from the mongodb database and will return it.
   *
   * @param resourceInternalId symbiote ID of a resource to be retrieved from the database
   * @return CloudResource
   * @throws ConflictException An exception can be thrown when no resourceInternalId is indicated
   */
  @RequestMapping(method = RequestMethod.GET, path = "/resource")
  @ApiOperation(value = "HTTP GET getResources",
	notes = "Search for one resources using HTTP GET. The parameters that can be used are defined below",
	response = CloudResource.class)
  @ApiResponses(value = {
		@ApiResponse(code = 500, message = "Resource Not Found") })
  public CloudResource getResource(@ApiParam(value = "symbiote ID of a resource") @RequestParam(value = "resourceInternalId", required = false) String resourceInternalId) throws ConflictException{
    logger.info("START OF getResource, in data "+ resourceInternalId);
    if ("".equals(resourceInternalId)) throw new ConflictException("resourceInternalId parameter must be informed");
    CloudResource result = infoManager.getResource(resourceInternalId);
    logger.info("END OF getResource, result "+ result);
    return result;
  }

//! Create a resource.
/*!
 * The addResource method stores \a CloudResource passed as parameter in the  
 * mondodb database and send the information to the \a Interworking Interface, \a Resource Access Proxy component and \a Monitoring components.
 *
 * \param resource \a CloudResource to be created within the system
 * \return \a addResource returns the \a CloudResource where the Symbiote id is included. 
 * An exception can be thrown when no \a internalId is indicated within the \a CloudResource 
 */
  
  /**
   * Endpoint for create a Resource
   * The addResource method stores \a CloudResource passed as parameter in the  
   * monngodb database and send the information to the \a Interworking Interface, \a Resource Access Proxy component and \a Monitoring components.
   *
   * @param resource  CloudResource to be created within the system
   * @return CloudResource where the Symbiote id is included.
   * @throws ConflictException An exception can be thrown when no \a internalId is indicated within the \a CloudResource 
   */
  @RequestMapping(method = RequestMethod.POST, path = "/resource")
  @ApiOperation(value = "HTTP POST addResource",
	notes = "Add for one resources using HTTP POST. The parameters that can be used are defined below",
	response = ResponseEntity.class)
  @ApiResponses(value = {
		@ApiResponse(code = 500, message = "Resource Not Found"),
		@ApiResponse(code = 401, message = "Resource UNAUTHORIZED"), 
		@ApiResponse(code = 400, message = "Resource BAD_REQUEST") 
		})
  public ResponseEntity<?> addResource(@ApiParam(value = "resource to add") @RequestParam(value = "resource", required = false) @RequestBody CloudResource resource) throws ConflictException{
    logger.info("START OF addResource, in data "+ resource);
    if (resource.getInternalId()==null) 
      throw new ConflictException("internalId field must be informed");

    List<CloudResource> list = new ArrayList<CloudResource>();
    List<CloudResource> result;
    HttpHeaders responseHeaders = new HttpHeaders();
    HttpStatus httpStatus;

    list.add(resource);
    try {
        result = infoManager.addResources(list);
    } catch (TokenValidationException e) {
        httpStatus = HttpStatus.UNAUTHORIZED;
        return new ResponseEntity<String>("Stored core token was invalid, so it was cleared. Reissue your request and you will automatically get a new core token", responseHeaders, httpStatus);
    } catch (Exception e) {
        httpStatus = HttpStatus.BAD_REQUEST;
        return new ResponseEntity<String>("Internal Error", responseHeaders, httpStatus);
    }

    logger.info("END OF addResource, result "+ result);
    return new  ResponseEntity<CloudResource>(result.get(0), responseHeaders, HttpStatus.OK);
    
 }

//! Create a resource.
/*!
 * The addResources method stores List of \a CloudResource passed as parameter in the  
 * mondodb database and send the information to the \a Interworking Interface, \a Resource Access Proxy component and \a Monitoring components.
 *
 * \param resource \a CloudResource to be created within the system
 * \return \a addResource returns the \a CloudResource where the Symbiote id is included. 
 * An exception can be thrown when no \a internalId is indicated within the \a CloudResource 
 */

  /**
  * 
  * Endpoint for create a List of \a CloudResource passed as parameter in the  
  * mongodb database and send the information to the \a Interworking Interface, \a Resource Access Proxy component and \a Monitoring components.
  *
  * @param resources List of CloudResource to be created within the system
  * @return CloudResource where the Symbiote id is included. 
  * @throws ConflictException An exception can be thrown when no \a internalId is indicated within the \a CloudResource 
  */
  @RequestMapping(method = RequestMethod.POST, path = "/resources")
  @ApiOperation(value = "HTTP POST addResources",
	notes = "Add a list of resources using HTTP POST. The parameters that can be used are defined below",
	response = ResponseEntity.class)
  @ApiResponses(value = {
		@ApiResponse(code = 500, message = "Resource Not Found"),
		@ApiResponse(code = 401, message = "Resource UNAUTHORIZED"), 
		@ApiResponse(code = 400, message = "Resource BAD_REQUEST") 
		})
  public ResponseEntity<?> addResources(@ApiParam(value = "list of resource to add") @RequestParam(value = "resource", required = false) @RequestBody List<CloudResource> resources) throws ConflictException{
	logger.info("START OF addResource, in data "+ resources);
    List<CloudResource> result;
    HttpHeaders responseHeaders = new HttpHeaders();
    HttpStatus httpStatus;

    try {
        result = infoManager.addResources(resources);
    } catch (TokenValidationException e) {
         httpStatus = HttpStatus.UNAUTHORIZED;
        return new ResponseEntity<String>("Stored core token was invalid, so it was cleared. Reissue your request and you will automatically get a new core token", responseHeaders, httpStatus);
    } catch (Exception e) {
        httpStatus = HttpStatus.BAD_REQUEST;
        return new ResponseEntity<String>("Internal Error", responseHeaders, httpStatus);
    }

    logger.info("END OF addResources, result "+ result);
    return new  ResponseEntity<List<CloudResource>>(result, responseHeaders, HttpStatus.OK); 
 }
  
  
//! Update a resource.
/*!
 * The updateResource method updates the \a CloudResource passed as parameter into the   
 * mondodb database and sends the information to the \a Interworking Interface and \a Resource Access Proxy component.
 *
 * \param resource \a CloudResource to be updated within the system
 * \return \a updateResource returns the \a CloudResource where the Symbiote id is included. 
 */
  /**
   * Endpoint to updates \a CloudResource passed as parameter into the   
   * mondodb database and sends the information to the \a Interworking Interface and \a Resource Access Proxy component.
   *
   * @param resource CloudResource to be updated within the system
   * @return returns the \a CloudResource where the Symbiote id is included. 
   */
  @RequestMapping(method = RequestMethod.PUT, path = "/resource")
  @ApiOperation(value = "HTTP PUT updateResource",
	notes = "Update for one resource using HTTP PUT. The parameters that can be used are defined below",
	response = ResponseEntity.class)
  @ApiResponses(value = {
		@ApiResponse(code = 500, message = "Resource Not Found"),
		@ApiResponse(code = 401, message = "Resource UNAUTHORIZED"), 
		@ApiResponse(code = 400, message = "Resource BAD_REQUEST") 
		})
  public ResponseEntity<?> updateResource(@ApiParam(value = "Id for one resource to update") @RequestParam(value = "resource", required = false) @RequestBody CloudResource resource) {
    logger.info("START OF updateResource, in data "+ resource);
    if (resource.getInternalId()==null)
     throw new ConflictException("internalId field must be informed");
    List<CloudResource> list = new ArrayList<CloudResource>();
    list.add(resource);
    List<CloudResource> result;
    HttpHeaders responseHeaders = new HttpHeaders();
    HttpStatus httpStatus;

    try {
        result = infoManager.updateResources(list);
    } catch (TokenValidationException e) {
        httpStatus = HttpStatus.UNAUTHORIZED;
        return new ResponseEntity<String>("Stored core token was invalid, so it was cleared. Reissue your request and you will automatically get a new core token", responseHeaders, httpStatus);
    } catch (Exception e) {
        httpStatus = HttpStatus.BAD_REQUEST;
        return new ResponseEntity<String>("Internal Error", responseHeaders, httpStatus);
    }

    logger.info("END OF updateResource, result "+ result);
    return new  ResponseEntity<CloudResource>(result.get(0), responseHeaders, HttpStatus.OK);
  }

//! Update a resource.
/*!
 * The updateResource method updates the \a CloudResource passed as parameter into the   
 * mondodb database and sends the information to the \a Interworking Interface and \a Resource Access Proxy component.
 *
 * \param resource \a CloudResource to be updated within the system
 * \return \a updateResource returns the \a CloudResource where the Symbiote id is included. 
 */
  /**
   * 
   * Endpoint to updates to updates a list of CloudResource passed as parameter into the   
   * mondodb database and sends the information to the \a Interworking Interface and \a Resource Access Proxy component.
   *
   * @param resource a list of CloudResource to be updated within the system
   * @return a updateResource returns the \a CloudResource where the Symbiote id is included.  
   */
  @RequestMapping(method = RequestMethod.PUT, path = "/resources")
  @ApiOperation(value = "HTTP PUT updateResources",
	notes = "Update for a list of resources using HTTP PUT. The parameters that can be used are defined below",
	response = ResponseEntity.class)
  @ApiResponses(value = {
		@ApiResponse(code = 500, message = "Resource Not Found"),
		@ApiResponse(code = 401, message = "Resource UNAUTHORIZED"), 
		@ApiResponse(code = 400, message = "Resource BAD_REQUEST") 
		})
  public ResponseEntity<?> updateResources(@ApiParam(value = "list of resources to update") @RequestParam(value = "resource", required = false) @RequestBody List<CloudResource> resource) {
    logger.info("START OF updateResource, in data "+ resource);
    List<CloudResource> result;
    HttpHeaders responseHeaders = new HttpHeaders();
    HttpStatus httpStatus;

    try {
        result = infoManager.updateResources(resource); 
    } catch (TokenValidationException e) {
        httpStatus = HttpStatus.UNAUTHORIZED;
        return new ResponseEntity<String>("Stored core token was invalid, so it was cleared. Reissue your request and you will automatically get a new core token", responseHeaders, httpStatus);
    } catch (Exception e) {
        httpStatus = HttpStatus.BAD_REQUEST;
        return new ResponseEntity<String>("Internal Error", responseHeaders, httpStatus);
    }

    logger.info("END OF updateResource, result "+ result);
    return new  ResponseEntity<List<CloudResource>>(result, responseHeaders, HttpStatus.OK);
  }

//! Delete a resource.
/*!
 * The deleteResource method removes the \a CloudResource identified by the id passed as a parameter in the \a resourceInternalId variable.   
 * It removes it from the mondodb database and request the removal of the information to the \a Interworking Interface and the \a Resource Access Proxy component.
 *
 * \param resourceInternalId \a internalId to the resource to be removed 
 * \return \a deleteResource returns the \a CloudResource that has been just removed 
 */
  /**
   *  Endpoint to removes the \a CloudResource identified by the id passed as a parameter in the \a resourceInternalId variable.   
   * It removes it from the mongodb database and request the removal of the information to the \a Interworking Interface and the \a Resource Access Proxy component.
   * 
   * @param resourceInternalId internalId to the resource to be removed 
   * @return deleteResource returns the \a CloudResource that has been just removed 
   */
  @RequestMapping(method = RequestMethod.DELETE, path = "/resource")
  @ApiOperation(value = "HTTP DELETE deleteResource",
	notes = "Delete for one resource using HTTP DELETE. The parameters that can be used are defined below",
	response = ResponseEntity.class)
  @ApiResponses(value = {
		@ApiResponse(code = 500, message = "Resource Not Found"),
		@ApiResponse(code = 401, message = "Resource UNAUTHORIZED"), 
		@ApiResponse(code = 400, message = "Resource BAD_REQUEST") 
		})
  public ResponseEntity<?> deleteResource(@ApiParam(value = "Id for one resource to delete") @RequestParam(value = "resourceInternalId", required = false) String resourceInternalId) {
    logger.info("START OF deleteResource, in data "+ resourceInternalId);
    List<String> list = new ArrayList<String>();
    list.add(resourceInternalId);
    List<CloudResource> result;
    HttpHeaders responseHeaders = new HttpHeaders();
    HttpStatus httpStatus;

    try {
        result = infoManager.deleteResources(list);
    } catch (TokenValidationException e) {
        httpStatus = HttpStatus.UNAUTHORIZED;
        return new ResponseEntity<String>("Stored core token was invalid, so it was cleared. Reissue your request and you will automatically get a new core token", responseHeaders, httpStatus);
    } catch (Exception e) {
        httpStatus = HttpStatus.BAD_REQUEST;
        return new ResponseEntity<String>("Internal Error", responseHeaders, httpStatus);
    }

    if (result.size()>0){
    	logger.info("END OF deleteResource, result "+ result.get(0));
      return new ResponseEntity<CloudResource>(result.get(0), responseHeaders, HttpStatus.OK);
    }  else {
    	logger.info("END OF deleteResource, the resource didn't exist");
      return new ResponseEntity<String>("The resource didn't exist", responseHeaders, HttpStatus.BAD_REQUEST);
    }
  }
 
  /**
   * 
   * Endpoint to removes a list of CloudResource identified by the id passed as a parameter in the \a resourceInternalId variable.   
   * It removes it from the mongodb database and request the removal of the information to the \a Interworking Interface and the \a Resource Access Proxy component. 
   * 
   * @param resourceInternalId List of internalId resources to be removed 
   * @return the CloudResource that has been just removed 
   */
  @RequestMapping(method = RequestMethod.DELETE, path = "/resources")
  @ApiOperation(value = "HTTP DELETE deleteResource",
	notes = "Delete for a list of resources using HTTP DELETE. The parameters that can be used are defined below",
	response = ResponseEntity.class)
  @ApiResponses(value = {
		@ApiResponse(code = 500, message = "Resource Not Found"),
		@ApiResponse(code = 401, message = "Resource UNAUTHORIZED"), 
		@ApiResponse(code = 400, message = "Resource BAD_REQUEST") 
		})
  public ResponseEntity<?> deleteResources(@ApiParam(value = "List of Id for resources to delete") @RequestParam(value = "resourceInternalId", required = false) List<String> resourceInternalId) {
    logger.info("START OF deleteResource, in data "+ resourceInternalId);
    List<CloudResource> result;
    HttpHeaders responseHeaders = new HttpHeaders();
    HttpStatus httpStatus;

    try {
        result = infoManager.deleteResources(resourceInternalId);
    } catch (TokenValidationException e) {
        httpStatus = HttpStatus.UNAUTHORIZED;
        return new ResponseEntity<String>("Stored core token was invalid, so it was cleared. Reissue your request and you will automatically get a new core token", responseHeaders, httpStatus);
    } catch (Exception e) {
        httpStatus = HttpStatus.BAD_REQUEST;
        return new ResponseEntity<String>("Internal Error", responseHeaders, httpStatus);
    }

    logger.info("END OF deleteResource, result "+ result);
    return new ResponseEntity<List<CloudResource>>(result, responseHeaders, HttpStatus.OK);
  }
}
