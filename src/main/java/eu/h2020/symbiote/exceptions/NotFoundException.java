package eu.h2020.symbiote.exceptions;

/**
 * Exception thrown when an error in the data is detected
 *
 * @author: Jose, Elena Garrido
 * @version: 27/09/16

 */
public class NotFoundException extends RuntimeException{
    /**
	 * 
	 */
	private static final long serialVersionUID = 3307799213369579188L;
	String extraInfo = "";
    public NotFoundException(String info){
        super(info);
        this.extraInfo = info;
    }

    public NotFoundException(String info, String extraInfo){
        super(info);
        this.extraInfo = extraInfo;
    }
    public String getExtraInfo() {
        return extraInfo;
    }
}