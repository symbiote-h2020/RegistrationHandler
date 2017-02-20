package eu.h2020.symbiote.service;

import eu.h2020.symbiote.exceptions.ConflictException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Used to control the output of the exceptions
 *
 * @author: Elena Garrido
 * @version: 19/01/2017

 */
/**! \class GlobalExceptionHandler 
 * \brief Used to control the output of the exceptions from the rest controlled
 * This class implements methods to catch the exceptions thrown within the rest controlled and return the properer code and text 
 * to the invoker of the rest call 
 **/
@ControllerAdvice
public class GlobalExceptionHandler {
    private static Log logger = LogFactory.getLog(GlobalExceptionHandler.class);

    //! Makes the handling when a \a ConflictException is thrown
    /*!
     * The handleConflict handles the case when a ConflictException is thrown. The exception will be passed a paramenter within 
     * e
     *
     * \param e \a ConflictException that has been catched and will be handled
     * \return \a handleConflict returns a \a ErrorMessage bean with details about the error. The HTTP CONFLICT status will be 
     * returned within the \a errorCode field 
     */
    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler(ConflictException.class)
    public @ResponseBody
     ErrorMessage handleConflict(ConflictException e) {
        logger.error("Catched exception:", e);
         ErrorMessage errorMessage = new ErrorMessage();
         errorMessage.setErrorCode(HttpStatus.CONFLICT.name());
         errorMessage.setErrorInfo("Conflict with data: "+e.getExtraInfo());
         return errorMessage;
     }



    /**! \class ErrorMessage 
     * \brief Error message bean returned to the invocation of a rest call when an exception occurs. An error code and the correct
     * informaiton is returned to the user of the rest interfaces
     **/
    @XmlRootElement
    class ErrorMessage {
        String errorCode = "";
        String errorInfo = "";
        //! Get the code of the error
        @XmlElement
        public String getErrorCode() {    return errorCode;    }
        //! Set the code of the error
        public void setErrorCode(String errorCode) {    this.errorCode = errorCode;   }
        //! Get the information about the error
        @XmlElement
        public String getErrorInfo() {     return errorInfo;   }
        //! Set the information about the error
        public void setErrorInfo(String errorInfo) {   this.errorInfo = errorInfo;  }
    }
}
