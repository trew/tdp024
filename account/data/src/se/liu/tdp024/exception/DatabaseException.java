package se.liu.tdp024.exception;

public class DatabaseException extends AccountException {

    public DatabaseException(String msg)
    {
        super(500, msg, "Database Error");
    }

}
