package eu.h2020.symbiote.infoReader;

import eu.h2020.symbiote.PlatformInfoReader;
import eu.h2020.symbiote.RegistrationHandlerApplication;
import eu.h2020.symbiote.beans.PlatformBean;
import eu.h2020.symbiote.beans.ResourceBean;

import feign.RequestLine;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by jose on 7/10/16.
 */
@Component("networkPlatformInfoReader")
public class NetworkPlatformInfoReader implements PlatformInfoReader {

  @Value("${symbiote.network.information.location}")
  private String remoteLocation;

  private interface RemoteFileClient {

    @RequestLine("GET /system.json")
    PlatformBean getPlatformInformation();

    @RequestLine("GET /resources.json")
    List<ResourceBean> getResources();

  }

  private RemoteFileClient getClient() {
    return RegistrationHandlerApplication.
        createFeignClient(RemoteFileClient.class, remoteLocation);
  }

    @Override
    public PlatformBean getPlatformInformation() {
    return getClient().getPlatformInformation();
  }

    @Override
    public List<ResourceBean> getResourcesToRegister() {
    return getClient().getResources();
  }

    /**
     * Created by jose on 5/10/16.
     */
    @Component
    public static class PlatformInfoReaderFactory {

      @Autowired
      private ApplicationContext ctx;

      public PlatformInfoReader getPlatformInfoReader(String type) {
        String finalType = (type != null) ? type : "filePlatformInfoReader";
        return ctx.getBean(type, PlatformInfoReader.class);
      }

    }
}
