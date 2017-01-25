package eu.h2020.symbiote.inforeader;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import eu.h2020.symbiote.PlatformInfoReader;
import eu.h2020.symbiote.PlatformInformationManager;
import eu.h2020.symbiote.beans.PlatformBean;
import eu.h2020.symbiote.beans.ResourceBean;


/**
 * This class is to communicate with RabbitMQ. Initially created by mateuszl and updated by Elena
 *
 * @author: mateuszl, Elena Garrido
 * @version: 18/01/2017

 */

@Component
public class PlatformInformationReader implements CommandLineRunner {


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
    if (autoRegister) {
/* TODO uncomment when clear RPC / direct / bulk or not        PlatformBean platformInfo = platformReader.getPlatformInformation();
        List<ResourceBean> resourcesInfo = platformReader.getResourcesToRegister();
        if (platformInfo != null) {
            platformManager.updatePlatformInfoInInternalRepository(platformInfo);
        }
 
        if (resourcesInfo != null) {
        	platformManager.addResources(resourcesInfo);
        }*/
    }
  }
}
