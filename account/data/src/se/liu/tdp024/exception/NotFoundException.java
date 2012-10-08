package se.liu.tdp024.exception;

public class NotFoundException extends AccountException {

    public NotFoundException(String msg) {
        super(404, msg, "Not found");
    }

}
