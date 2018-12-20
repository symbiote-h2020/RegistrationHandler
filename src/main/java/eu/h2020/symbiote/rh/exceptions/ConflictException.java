/*
 *  Copyright 2018 Atos
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

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
