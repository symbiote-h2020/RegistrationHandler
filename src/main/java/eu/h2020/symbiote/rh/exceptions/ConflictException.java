package eu.h2020.symbiote.rh.exceptions;

/**
 * Exception thrown when an error in the data is detected
 *
 * @author: Elena Garrido
 * @version: 19/01/2017

 */
/**! \class ConflictException 
 * \brief Used to generate a \a conflict error that will be handled by the \class GlobalExceptionHandler class  
 **/
public class ConflictException extends RuntimeException{
    /**
	 * 
	 */
	private static final long serialVersionUID = -2020303865004496068L;
	String extraInfo = "";
    public ConflictException(String info){
        super(info);
        this.extraInfo = info;
    }

    public ConflictException(String info, String extraInfo){
        super(info);
        this.extraInfo = extraInfo;
    }
    //! Get the extra information about the exception
    public String getExtraInfo() {
        return extraInfo;
    }
}
