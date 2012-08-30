package se.liu.tdp024.entity;

import java.io.Serializable;
import javax.persistence.*;
import se.liu.tdp024.util.EMF;

/**
 *
 */
@Entity
public class Account implements Serializable {

    public static final int SALARY = 0;
    public static final int SAVINGS = 1;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long accountNumber;

    @Column(nullable = false)
    private String personKey;

    @Column(nullable = false)
    private String bankKey;

    private long balance;

    private int accountType;


    public long getAccountNumber() {
        return accountNumber;
    }

    public int getAccountType() {
        return accountType;
    }

    public boolean setAccountType(int type) {
        if (type < 0 || type > 1)
            return false;
        accountType = type;
        return true;
    }

    public String getPersonKey() {
        return personKey;
    }

    public void setPersonKey(String key) {
        personKey = key;
    }

    public String getBankKey() {
        return bankKey;
    }

    public void setBankKey(String key) {
        bankKey = key;
    }

    public long getBalance() {
        return balance;
    }

    public void changeBalance(long amount) {
        this.balance += amount;
    }
}
