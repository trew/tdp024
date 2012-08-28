package se.liu.tdp024.entity;

import java.io.Serializable;
import javax.persistence.*;

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
    private long personKey;
    
    @Column(nullable = false)
    private long bankKey;
    
    private long amount;
    
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
    
    public long getPersonKey() {
        return personKey;
    }
    
    public void setPersonKey(long key) {
        personKey = key;
    }
    
    public long getBankKey() {
        return bankKey;
    }
    
    public void setBankKey(long key) {
        bankKey = key;
    }
    
    public long getAmount() {
        return amount;
    }
    
    public void deposit(long amount) {
        this.amount += amount;
    }
    
    public void withdraw(long amount) {
        // TODO: Error checks
        this.amount -= amount;
    }
    
}
