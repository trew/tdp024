package se.liu.tdp024.exception;

public class ValidationException extends AccountException {

    public ValidationException(String msg) {
        super(500, msg, "Error validating data");
    }

}
