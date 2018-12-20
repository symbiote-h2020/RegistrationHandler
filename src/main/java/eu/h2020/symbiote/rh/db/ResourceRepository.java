/*
 *  Copyright 2018 Atos
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

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
