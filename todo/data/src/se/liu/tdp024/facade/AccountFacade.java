package se.liu.tdp024.facade;

import java.util.*;
import javax.persistence.EntityManager;
import se.liu.tdp024.util.EMF;
import se.liu.tdp024.entity.Account;

/**
 *
 */
public abstract class AccountFacade {

    public static long create(int accountType,
                              String personKey,
                              String bankKey) {
        EntityManager em = EMF.getEntityManager();
        
        return 0;
    }
    
    public static Account find(long accountNumber) {
        return null;
    }
    
    public static List<Account> findByPersonKey(String personKey) {
        return new LinkedList<Account>();
    }
    
    public static List<Account> findByBankKey(String bankKey) {
        return new LinkedList<Account>();
    }
    
    public static long balance(long accountNumber) {
        return -1;
    }

    public static boolean deposit(long accountNumber, long amount) {
        return false;
    }

    public static boolean withdraw(long accountNumber, long amount) {
        return false;
    }
}
