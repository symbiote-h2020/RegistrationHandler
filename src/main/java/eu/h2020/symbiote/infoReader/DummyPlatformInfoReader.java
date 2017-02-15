package eu.h2020.symbiote.inforeader;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import eu.h2020.symbiote.PlatformInfoReader;
import eu.h2020.symbiote.beans.ResourceBean;

/**
 * Created by a141976 on 20/12/2016.
 */
/**! \class DummyPlatformInfoReader
 * \brief This class extends from \class PlatformInfoReader and implements a method  \a getResourcesToRegister that returns and empty list of \class ResourceBean
 **/
@Component ("dummyPlatformInfoReader")
public class DummyPlatformInfoReader implements PlatformInfoReader {
    @Override
    public List<ResourceBean> getResourcesToRegister() {
        return new ArrayList<ResourceBean>();
    }
}
