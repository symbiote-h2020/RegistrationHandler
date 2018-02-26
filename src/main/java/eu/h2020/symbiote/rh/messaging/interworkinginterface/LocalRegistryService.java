package eu.h2020.symbiote.rh.messaging.interworkinginterface;

import eu.h2020.symbiote.core.cci.ResourceRegistryRequest;
import eu.h2020.symbiote.core.cci.ResourceRegistryResponse;
import eu.h2020.symbiote.rh.constants.RHConstants;
import feign.Headers;
import feign.Param;
import feign.RequestLine;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.Map;

public interface LocalRegistryService {

    @RequestLine("POST "+ RHConstants.DO_SHARE_RESOURCES)
    @Headers({"Accept: application/json", "Content-Type: application/json"})
    Map<String, Map<String, String>> shareResources(@RequestBody List<ResourceSharingBean> resources);

}
