package eu.h2020.symbiote.rh.security;

import java.security.KeyStore;

import javax.annotation.PostConstruct;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import eu.h2020.symbiote.commons.security.SecurityHandler;
import eu.h2020.symbiote.commons.security.certificate.CertificateVerificationException;
import eu.h2020.symbiote.commons.security.exception.DisabledException;
import eu.h2020.symbiote.commons.security.token.SymbIoTeToken;

@Component
public class SecurityManager {
	  private static final Log logger = LogFactory.getLog(SecurityManager.class);

	  @Value("${security.coreAAM.url}")
	  String coreAAMUrl;
	  @Value("${security.rabbitMQ.ip}")
	  String rabbitMQHostIP;
	  @Value("${security.enabled}")
	  boolean enabled;
	  
	  @Value("${security.user}")
	  String userName;

	  @Value("${security.password}")
	  String password;

	  SecurityHandler securityHandler;

	  
	  @PostConstruct
	  private void init() {
		  securityHandler = new SecurityHandler(coreAAMUrl, rabbitMQHostIP, enabled); 
	  }
	  public boolean isEnabled(){
		  return enabled;
	  }
	  
	  public SymbIoTeToken requestCoreToken() throws SecurityException{
		  try{
			  return securityHandler.requestCoreToken(userName, password);
		  }catch (DisabledException e){
			  logger.debug("security handler is disabled");
		  }
		  return null;
	  }

	  public boolean certificateValidation(KeyStore p12Certificate) throws SecurityException{
		  try{
			  return securityHandler.certificateValidation(p12Certificate);
		  }catch (DisabledException e){
			  logger.debug("security handler is disabled");
		  } catch (CertificateVerificationException e) {
			  logger.error("error validating certificate");
		}
		return false;
	  }
}
