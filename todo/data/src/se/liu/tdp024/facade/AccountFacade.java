package se.liu.tdp024.facade;

import java.util.*;
import javax.persistence.*;
import se.liu.tdp024.util.EMF;
import se.liu.tdp024.entity.Account;
import se.liu.tdp024.util.Monlog;
/**
 *
 */
public abstract class AccountFacade {
    private final static Monlog logger = Monlog.getLogger();

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
        EntityManager em = EMF.getEntityManager();
        Query query = em.createQuery("SELECT a FROM Account a WHERE a.personKey = :personkey");
        query.setParameter("personkey", personKey);
        return query.getResultList();
    }

    public static List<Account> findByBankKey(String bankKey) {
        EntityManager em = EMF.getEntityManager();
        Query query = em.createQuery("SELECT a FROM Account a WHERE a.bankKey = :bankkey");
        query.setParameter("bankkey", bankKey);
        return query.getResultList();
    }

    public static long balance(long accountNumber) {
        Account acc = find(accountNumber);
        if (acc == null)
                return -1;

        return acc.getBalance();
    }

    private static boolean changeBalance(long accountNumber, long amount) {
        EntityManager em = EMF.getEntityManager();

        try {
            em.getTransaction().begin();
            Account acc = em.find(Account.class, accountNumber, LockModeType.PESSIMISTIC_WRITE);

            if (acc == null) {
                /*
                 * Log something here
                 */
                return false;
            }

            if ((acc.getBalance() + amount) > Long.MAX_VALUE ||
                    (acc.getBalance() + amount) < 0) {
                /*
                 * Log something here
                 */
                return false;
            }

            acc.changeBalance(amount);

            em.merge(acc);
            em.getTransaction().commit();
            return true;

        } catch (Exception e) {
            /*
             * Log something here
             */
            e.printStackTrace();
            return false;

        } finally {
            if(em.getTransaction().isActive())
                em.getTransaction().rollback();
            em.close();
        }
    }

    public static boolean withdraw(long accountNumber, long amount) {
        return changeBalance(accountNumber, -amount);
    }


    public static boolean deposit(long accountNumber, long amount) {
        return changeBalance(accountNumber, amount);
    }
}
