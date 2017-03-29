package eu.h2020.symbiote.rh.inforeader;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import eu.h2020.symbiote.cloud.model.CloudResource;
import eu.h2020.symbiote.rh.PlatformInfoReader;

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

    @Value("${symbiote.platform.file.location}")
    String fileLocation;


    @Override
    public List<CloudResource> getResourcesToRegister() {

        Gson reader = new Gson();
        File resourcesFile = new File(fileLocation+"/"+RESOURCES_FILE_NAME);

        Type listType = new TypeToken<ArrayList<CloudResource>>(){}.getType();

        try {
            return reader.fromJson(new FileReader(resourcesFile), listType);
        } catch (FileNotFoundException e) {
            logger.error("Error reading resource file", e);
        }

        return new ArrayList<>();
    }
}