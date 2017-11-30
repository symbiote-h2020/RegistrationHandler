package eu.h2020.symbiote.rh.db;

import eu.h2020.symbiote.cloud.model.internal.CloudResource;
import eu.h2020.symbiote.rh.constants.RHConstants;

import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * Created by jose on 27/09/16.
 */
/**! \class ResourceRepository 
 * \brief ResourceRepository interface to connect with the mongodb database where the registered resources will be stored
 * within the platform
 **/
@Document(collection = RHConstants.RESOURCE_COLLECTION)
public interface ResourceRepository extends MongoRepository<CloudResource, String> {

	//! Retrieves a \a CloudResource.
	/*!
	 * The getByInternalId method retrieves \a CloudResource identified by the \a resourceId parameter from the   
	 * mondodb database 
	 *
	 * \param resourceId id from the resource to be retrieved 
	 * \return \a getByInternalId returns the \a CloudResource identified by  \a resourceId
	 */
	CloudResource getByInternalId(String resourceId);
	
	/**
	 * Retrieves a colud resource using the symbiote id
	 * @param resourceId symbiote id of the resource to delete
	 * @return the resource if found
	 */
	CloudResource getByResourceId(String resourceId);

}
