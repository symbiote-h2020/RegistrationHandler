package eu.h2020.symbiote.rh.messaging.interworkinginterface;

import com.google.gson.JsonSyntaxException;

import com.fasterxml.jackson.databind.ObjectMapper;

import eu.h2020.symbiote.core.cci.ResourceRegistryResponse;
import eu.h2020.symbiote.security.commons.exceptions.custom.SecurityHandlerException;

import feign.FeignException;
import feign.Response;
import feign.Util;
import feign.codec.ErrorDecoder;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.IOException;

public class InterworkingInterfaceErrorDecoder implements ErrorDecoder {
    private static Log log = LogFactory.getLog(InterworkingInterfaceErrorDecoder.class);
    

    @Override
    public Exception decode(String methodKey, Response response) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            String body = Util.toString(response.body().asReader());
            log.info("Status: " + response.status() + " and responseBody: " + body);
            ResourceRegistryResponse resourceRegistryResponse = mapper.readValue(body, ResourceRegistryResponse.class);
            if (response.status() == 400 && resourceRegistryResponse.getMessage().equals("Token invalid")) {
                log.info("The Token was invalid");
                return new SecurityHandlerException("Token invalid");

            }
        } catch (JsonSyntaxException e) {
            log.error("Unable to get ResourceRegistryResponse from Message body!", e);
            return FeignException.errorStatus(methodKey, response);
        } catch (IOException ignored) { // NOPMD
        }

        return FeignException.errorStatus(methodKey, response);
    }
}