package se.liu.tdp024.exception;

public class IllegalArgumentAccountException extends AccountException {

    public IllegalArgumentAccountException(String msg) {
        super(400, msg, "Illegal Argument");
    }

}
