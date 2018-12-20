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

package eu.h2020.symbiote.rh.inforeader;

import com.fasterxml.jackson.databind.ObjectMapper;

import eu.h2020.symbiote.cloud.model.internal.CloudResource;
import eu.h2020.symbiote.rh.PlatformInfoReader;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by jose on 27/09/16.
 */
/**! \class FilePlatformInfoReader
 * \brief This class extends from \class PlatformInfoReader and implements a method  \a getResourcesToRegister that reads a \a resources.json file that has to contain a 
 * list of \class CloudResource that will be returned by the method
 **/
@Component("filePlatformInfoReader")
public class FilePlatformInfoReader implements PlatformInfoReader{

    private static final Log logger = LogFactory.getLog(FilePlatformInfoReader.class);

    private static final String RESOURCES_FILE_NAME = "resources.json";

    @Value("${symbiote.platform.file.location ?: file:///var/tmp/resources}")
    String fileLocation;


    @Override
    public List<CloudResource> getResourcesToRegister() {

		ObjectMapper mapper = new ObjectMapper();
        File resourcesFile = new File(fileLocation+"/"+RESOURCES_FILE_NAME);

        List<CloudResource> list = new ArrayList<>();

       	try {
			list = mapper.readValue(new FileReader(resourcesFile),  mapper.getTypeFactory().constructCollectionType(List.class, Class.forName(CloudResource.class.getName())));
		} catch (ClassNotFoundException | IOException e) {
            logger.error("Error reading resource file", e);
		}

        return list;
    }
}
