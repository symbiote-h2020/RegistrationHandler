package eu.h2020.symbiote.rh.service;

import eu.h2020.symbiote.cloud.model.internal.CloudResource;
import eu.h2020.symbiote.rh.PlatformInformationManager;
import eu.h2020.symbiote.rh.exceptions.ConflictException;
import eu.h2020.symbiote.security.exceptions.aam.TokenValidationException;

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


/**! \class RegistrationHandlerRestService 
 * \brief RegistrationHandlerRestService Rest controller for registration handler
 * This class implements the rest interfaces.  It is the class that will receive any REST call done to the registration handler 
 **/
/*
 * @author: jose, Elena Garrido
 * @version: 27/09/2016
 */
@RestController
public class RegistrationHandlerRestService {
  private static final Log logger = LogFactory.getLog(RegistrationHandlerRestService.class);

  @Autowired
  private PlatformInformationManager infoManager;

  
  @RequestMapping(method = RequestMethod.GET, path = "/resources")
  public List<CloudResource> getResources() {
    logger.info("START OF getResources");
    List<CloudResource>result = infoManager.getResources();
    logger.info("END OF getResources, result "+ result);
    return result;
  }
//! Get a resource.
/*!
 * The getResource method retrieves \a CloudResource identified by \a resourceInternalId 
 * from the mondodb database and will return it.
 *
 * \param resourceInternalId id from the resource to be retrieved from the database
 * \return \a getResource returns the \a CloudResource, 
 * An exception can be thrown when no \a resourceInternalId is indicated
 */
  @RequestMapping(method = RequestMethod.GET, path = "/resource")
  public CloudResource getResource(@RequestParam String resourceInternalId) throws ConflictException{
    logger.info("START OF getResource, in data "+ resourceInternalId);
    if ("".equals(resourceInternalId)) throw new ConflictException("resourceInternalId parameter must be informed");
    CloudResource result = infoManager.getResource(resourceInternalId);
    logger.info("END OF getResource, result "+ result);
    return result;
  }
  
  public <T> ResponseEntity<?> cleanListResult(ResponseEntity<List<T>> result) {
    if (HttpStatus.OK.equals(result.getStatusCode()) &&
            result.hasBody() && result.getBody() != null && !result.getBody().isEmpty()) {
      return new ResponseEntity<T>(result.getBody().get(0), result.getHeaders(),
                                                  result.getStatusCode());
    } else {
      return result;
    }
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
  @RequestMapping(method = RequestMethod.POST, path = "/resource")
  public ResponseEntity<?> addResource(@RequestBody CloudResource resource) throws ConflictException{
    return cleanListResult((ResponseEntity<List<CloudResource>>)addResources(Arrays.asList(resource)));
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
  
  @RequestMapping(method = RequestMethod.POST, path = "/resources")
  public ResponseEntity<?> addResources(@RequestBody List<CloudResource> resources) throws ConflictException{
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
        return new ResponseEntity<String>("Internal Error: "+e.getMessage(), responseHeaders, httpStatus);
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
  @RequestMapping(method = RequestMethod.PUT, path = "/resource")
  public ResponseEntity<?> updateResource(@RequestBody CloudResource resource) {
    return cleanListResult((ResponseEntity<List<CloudResource>>)updateResources(Arrays.asList(resource)));
  }

//! Update a resource.
/*!
 * The updateResource method updates the \a CloudResource passed as parameter into the   
 * mondodb database and sends the information to the \a Interworking Interface and \a Resource Access Proxy component.
 *
 * \param resource \a CloudResource to be updated within the system
 * \return \a updateResource returns the \a CloudResource where the Symbiote id is included. 
 */
  @RequestMapping(method = RequestMethod.PUT, path = "/resources")
  public ResponseEntity<?> updateResources(@RequestBody List<CloudResource> resource) {
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
        return new ResponseEntity<String>("Internal Error: "+e.getMessage(), responseHeaders, httpStatus);
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
  @RequestMapping(method = RequestMethod.DELETE, path = "/resource")
  public ResponseEntity<?> deleteResource(@RequestParam String resourceInternalId) {
    return cleanListResult((ResponseEntity<List<CloudResource>>)deleteResources(Arrays.asList(resourceInternalId)));
  }
 
  @RequestMapping(method = RequestMethod.DELETE, path = "/resources")
  public ResponseEntity<?> deleteResources(@RequestParam List<String> resourceInternalId) {
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
        return new ResponseEntity<String>("Internal Error: "+e.getMessage(), responseHeaders, httpStatus);
    }

    logger.info("END OF deleteResource, result "+ result);
    return new ResponseEntity<List<CloudResource>>(result, responseHeaders, HttpStatus.OK);
  }
}
