package se.liu.tdp024.entity;

import java.io.Serializable;
import java.sql.Timestamp;
import javax.persistence.*;

@Entity
public class SavedTransaction implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Column(nullable = false)
    private long sender;

    @Column(nullable = false)
    private long reciever;

    @Column(updatable=false, insertable=false)
    private Timestamp datetime;

    private long amount;
    private boolean success;

    /* Getters */
    public long getID() {
        return id;
    }

    public long getSender() {
        return sender;
    }

    public long getReciever() {
        return reciever;
    }

    public Timestamp getDatetime() {
        return datetime;
    }

    public long getAmount() {
        return amount;
    }

    public boolean getSuccess() {
        return success;
    }

    /* Setters */
    public void setSender(long sender) {
        this.sender = sender;
    }

    public void setReciever(long reciever) {
        this.reciever = reciever;
    }

    public boolean setAmount(long amount) {
        if (amount > 0) {
            this.amount = amount;
            return true;
        }
        return false;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

}
