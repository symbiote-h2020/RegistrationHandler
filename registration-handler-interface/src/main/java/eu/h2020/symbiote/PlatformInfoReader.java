package eu.h2020.symbiote;

import java.util.List;

import eu.h2020.symbiote.beans.ResourceBean;

/**
 * Created by jose on 26/09/16.
 */
public interface PlatformInfoReader {

    List<ResourceBean> getResourcesToRegister();

}
