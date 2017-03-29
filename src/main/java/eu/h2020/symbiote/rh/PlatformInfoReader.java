package eu.h2020.symbiote.rh;

import java.util.List;

import eu.h2020.symbiote.cloud.model.CloudResource;

/**! \class PlatformInfoReader
 * \brief PlatformInfoReader interface to be implemented by the platform owners when they want to register a list of resources
 * when the RegistrationHandler starts
 **/
/*
 * @author: jose, Elena Garrido
 * @version: 27/09/2016
 */
public interface PlatformInfoReader {
	//! Retrieve list of resources to register.
	/*!
	 * The getResourcesToRegister method returns the list of \a CloudResource that a platform wants to register when
	 * the Registration Handler starts
	 *
	 * \return \a getResourcesToRegister returns the list of \a CloudResource to be registered 
	 */
    List<CloudResource> getResourcesToRegister();

}
