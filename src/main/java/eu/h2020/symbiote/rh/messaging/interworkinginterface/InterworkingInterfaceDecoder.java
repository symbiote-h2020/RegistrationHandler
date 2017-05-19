package eu.h2020.symbiote.rh.messaging.interworkinginterface;

import feign.codec.ErrorDecoder;
import feign.FeignException;
import feign.Response;
import feign.Util;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonSyntaxException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import java.io.IOException;

import eu.h2020.symbiote.core.cci.ResourceRegistryResponse;
import eu.h2020.symbiote.security.exceptions.aam.TokenValidationException;

public class InterworkingInterfaceDecoder implements ErrorDecoder {
    private static Log log = LogFactory.getLog(InterworkingInterfaceDecoder.class);
    

    @Override
    public Exception decode(String methodKey, Response response) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            String body = Util.toString(response.body().asReader());
            ResourceRegistryResponse resourceRegistryResponse = mapper.readValue(body, ResourceRegistryResponse.class);
            if (response.status() == 400 && resourceRegistryResponse.getMessage().equals("Token invalid")) {
                log.info("The Token was invalid");
                return new TokenValidationException("Token invalid");

            }
        } catch (JsonSyntaxException e) {
            log.error("Unable to get ResourceRegistryResponse from Message body!", e);
            return FeignException.errorStatus(methodKey, response);
        } catch (IOException ignored) { // NOPMD
        }

        return FeignException.errorStatus(methodKey, response);
    }
}