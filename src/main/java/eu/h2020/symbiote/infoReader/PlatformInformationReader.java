package eu.h2020.symbiote.infoReader;
import eu.h2020.symbiote.PlatformInformationManager;
import eu.h2020.symbiote.PlatformInfoReader;
import eu.h2020.symbiote.beans.PlatformBean;
import eu.h2020.symbiote.beans.ResourceBean;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;


/**
 * This class is to communicate with RabbitMQ. Initially created by mateuszl and updated by Elena
 *
 * @author: mateuszl, Elena Garrido
 * @version: 18/01/2017

 */

@Component
public class PlatformInformationReader implements CommandLineRunner {

  private static final Log logger = LogFactory.getLog(PlatformInformationReader.class);

  @Value("${reghandler.reader.impl}")
  private String readerImplementation;

  @Value("${reghandler.init.autoregister}")
  private boolean autoRegister;

  @Autowired
  private NetworkPlatformInfoReader.PlatformInfoReaderFactory platformReaderFactory;

  @Autowired
  private PlatformInformationManager platformManager;

  private PlatformInfoReader platformReader;

  @PostConstruct
  private void init() {
    platformReader = platformReaderFactory.getPlatformInfoReader(readerImplementation);
  }

  @Override
  public void run(String... args) throws Exception {
/* TODO review comment elene 18/01/2017
    PlatformBean platformInfo = platformReader.getPlatformInformation();
    List<ResourceBean> resourcesInfo = platformReader.getResourcesToRegister();

    if (platformInfo != null) {
      platformManager.updatePlatformInfo(platformInfo);
    }

    List<ResourceBean> updatedResources = new ArrayList<>();

    if (resourcesInfo != null) {
      updatedResources = platformManager.addResources(resourcesInfo);
    }

    if (autoRegister) {
      platformManager.registerPlatform();
      platformManager.registerResources(updatedResources.stream().map(
          resource -> resource.getInternalId()
      ).collect(Collectors.toList()));
    }
*/
  }
}
