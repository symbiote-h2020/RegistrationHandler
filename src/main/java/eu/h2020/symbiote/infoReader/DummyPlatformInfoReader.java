package eu.h2020.symbiote.inforeader;

import eu.h2020.symbiote.PlatformInfoReader;
import eu.h2020.symbiote.beans.PlatformBean;
import eu.h2020.symbiote.beans.ResourceBean;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by a141976 on 20/12/2016.
 */
@Component ("dummyPlatformInfoReader")
public class DummyPlatformInfoReader implements PlatformInfoReader {
    @Override
    public PlatformBean getPlatformInformation() {
        return null;
    }

    @Override
    public List<ResourceBean> getResourcesToRegister() {
        return new ArrayList<ResourceBean>();
    }
}
