package eu.h2020.symbiote.rh.security;

import java.security.KeyStore;

import javax.annotation.PostConstruct;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import eu.h2020.symbiote.security.SecurityHandler;
import eu.h2020.symbiote.security.certificate.CertificateVerificationException;
import eu.h2020.symbiote.security.exceptions.SecurityHandlerException;
import eu.h2020.symbiote.security.token.Token;
import eu.h2020.symbiote.security.exceptions.aam.TokenValidationException;

@Component
public class SecurityManager {
	  private static final Log logger = LogFactory.getLog(SecurityManager.class);

	  @Value("${symbiote.coreaam.url}")
	  String coreAAMUrl;

	  @Value("${rabbit.host}")
	  String rabbitMQHostIP;

	  @Value("${rabbit.username}")
	  String rabbitMQUsername;	

	  @Value("${rabbit.password}")
	  String rabbitMQPassword;
	  
	  @Value("${security.user}")
	  String userName;

	  @Value("${security.password}")
	  String password;

	  SecurityHandler securityHandler;

	  
	  @PostConstruct
	  private void init() {
		  securityHandler = new SecurityHandler(coreAAMUrl, rabbitMQHostIP, rabbitMQUsername, rabbitMQPassword); 
	  }
	  
	  public Token requestCoreToken() throws SecurityException{
		  try{
			  return securityHandler.requestCoreToken(userName, password);
		  }
		  catch (SecurityException e){
			  logger.info(e);
		  }
		  return null;
	  }

	  public boolean certificateValidation(KeyStore p12Certificate) throws SecurityException{
		  try{
			  return securityHandler.certificateValidation(p12Certificate);
		  }
          catch (CertificateVerificationException e) {
			  logger.error("error validating certificate");
		  }
		return false;
	  }
}
