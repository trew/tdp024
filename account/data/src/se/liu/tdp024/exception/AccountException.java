package se.liu.tdp024.exception;

public abstract class AccountException extends Exception {

    private int _code;
    public int getCode() {
        return _code;
    }

    private String _type;
    public String getType() {
        return _type;
    }

    public AccountException(int code, String msg, String type) {
        super(msg);
        _code = code;
        _type = type;
    }

}
