package org.tricol.supplierchain.exception;

public class InsufficientPermissionsException extends RuntimeException{
    public InsufficientPermissionsException(String message){
        super(message);
    }
}
