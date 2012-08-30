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
        try {

            em.getTransaction().begin();

            Account acc = new Account();
            acc.setAccountType(accountType);
            acc.setPersonKey(personKey);
            acc.setBankKey(bankKey);

            em.persist(acc);

            em.getTransaction().commit();

            return acc.getAccountNumber();

        } catch (Exception e) {
            /*
             * Should log something here
             */
            e.printStackTrace();
            return 0;
        } finally {

            if(em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }

            em.close();
        }
    }
    
    public static Account find(long accountNumber) {
        EntityManager em = EMF.getEntityManager();
        try {
            return em.find(Account.class, accountNumber);
        } catch (Exception e) {
            /*
             * Log something here
             */
            return null;
        } finally {
            em.close();
        }
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
