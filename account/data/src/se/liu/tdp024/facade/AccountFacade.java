package se.liu.tdp024.facade;

import java.util.*;
import javax.persistence.*;
import se.liu.tdp024.entity.Account;
import se.liu.tdp024.entity.SavedTransaction;
import se.liu.tdp024.util.EMF;
import se.liu.tdp024.util.Monlog;

public abstract class AccountFacade {
    private static final Monlog LOGGER = Monlog.getLogger(Monlog.Severity.INFO);

    /**
     * Creates a new account and stores it to the database. AccountType is
     * SALARY or SAVINGS.
     *
     * @param accountType Account.SALARY or Account.SAVINGS
     * @param personKey A string representing the personKey
     * @param bankKey A string representing the bankKey
     * @return the created Account
     * @see Account
     */
    public static Account create(int accountType,
                              String personKey,
                              String bankKey) {
        String debugLongDesc = "AccountType: " + accountType + "\n" +
                          "PersonKey: " + personKey + "\n" +
                          "BankKey: "+ bankKey + "\n";

        EntityManager em = EMF.getEntityManager();
        try {

            LOGGER.log(Monlog.Severity.DEBUG, "Beginning Transaction", debugLongDesc);
            em.getTransaction().begin();

            Account acc = new Account();
            if(!acc.setAccountType(accountType)) {
                LOGGER.log(Monlog.Severity.WARNING, "Tried to create account of unknown type", debugLongDesc);
                return null;
            }
            acc.setPersonKey(personKey);
            acc.setBankKey(bankKey);

            em.persist(acc);

            LOGGER.log(Monlog.Severity.DEBUG, "Committing Transaction", debugLongDesc);
            em.getTransaction().commit();

            return acc;

        } catch (Exception e) {
            String shortDesc = "Exception trying to create account.";
            LOGGER.log(Monlog.Severity.WARNING, shortDesc, debugLongDesc, e);
            return null;
        } finally {
            if(em.getTransaction().isActive()) {
                LOGGER.log(Monlog.Severity.DEBUG, "Rolling back Account creation", debugLongDesc);
                em.getTransaction().rollback();
            }
            em.close();
        }
    }

    public static Account find(long accountNumber) {
        String debugLongDesc = "AccountNumber: " + accountNumber + "\n";

        EntityManager em = EMF.getEntityManager();
        try {
            Account acc = em.find(Account.class, accountNumber);
            if (acc == null) {
                LOGGER.log(Monlog.Severity.INFO, "Account with accountnumber \"" +
                            accountNumber + "\" not found.", debugLongDesc);
            }
            return acc;
        } catch (IllegalArgumentException e) {
            String longDesc = "IllegalArgumentException\n" +
                "if the first argument does not denote an entity type or the " +
                "second argument is is not a valid type for that entity's " +
                "primary key or is null\n\n";
            longDesc += "accountNumber: " + accountNumber + "\n";
            longDesc += "class: " + Account.class.getName() + "\n";

            LOGGER.log(Monlog.Severity.ERROR, e.getMessage(), longDesc, e);
            return null;
        } finally {
            em.close();
        }
    }

    public static List<Account> findByPersonKey(String personKey) {
        String debugLongDesc = "PersonKey: " + personKey + "\n";

        EntityManager em = EMF.getEntityManager();
        Query query = em.createQuery("SELECT a FROM Account a WHERE a.personKey = :personkey");
        query.setParameter("personkey", personKey);

        List resultList = query.getResultList();
        if (resultList.isEmpty()) {
            LOGGER.log(Monlog.Severity.INFO, "Accounts for person with PersonKey \"" +
                        personKey + "\" not found.", debugLongDesc);
        }
        return resultList;
    }

    public static List<Account> findByBankKey(String bankKey) {
        String debugLongDesc = "BankKey: " + bankKey + "\n";

        EntityManager em = EMF.getEntityManager();
        Query query = em.createQuery("SELECT a FROM Account a WHERE a.bankKey = :bankkey");
        query.setParameter("bankkey", bankKey);

        List resultList = query.getResultList();
        if (resultList.isEmpty()) {
            LOGGER.log(Monlog.Severity.INFO, "Accounts for bank with BankKey \"" +
                        bankKey + "\" not found.", debugLongDesc);
        }
        return resultList;
    }

    public static long balance(long accountNumber) {
        String debugLongDesc = "AccountNumber: " + accountNumber + "\n";

        Account acc = find(accountNumber);
        if (acc == null) {
            LOGGER.log(Monlog.Severity.INFO, "Couldn't find account when trying to access balance.", debugLongDesc);
            return -1;
        }

        return acc.getBalance();
    }

    public static boolean transfer(long sender, long reciever, long amount) {
        String debugLongDesc = "Sender AccountNumber: " + sender + "\n" +
                               "Reciever AccountNumber: " + reciever + "\n" +
                               "Amount: " + amount + "\n";

        EntityManager em = EMF.getEntityManager();

        try {
            LOGGER.log(Monlog.Severity.DEBUG, "Starting transaction", debugLongDesc);
            em.getTransaction().begin();
            if (sender == reciever) {
                LOGGER.log(Monlog.Severity.ERROR, "Cannot transfer to self. (Sender == Reciever)", debugLongDesc);
                return false;
            }

            // Check validity of sender account
            Account senderAcc = em.find(Account.class, sender, LockModeType.PESSIMISTIC_WRITE);
            if (senderAcc == null) {
                LOGGER.log(Monlog.Severity.INFO, "Sender account not found.", debugLongDesc);
                return false;
            }

            if ((senderAcc.getBalance() - amount) < 0) {
                LOGGER.log(Monlog.Severity.INFO, "Not enough money on sender account. Aborting transfer.", debugLongDesc);
                return false;
            }

            Account recieverAcc = em.find(Account.class, reciever, LockModeType.PESSIMISTIC_WRITE);
            if (recieverAcc == null) {
                LOGGER.log(Monlog.Severity.INFO, "Reciever account not found.", debugLongDesc);
                return false;
            }
            if ((recieverAcc.getBalance() + amount) < 0) {
                LOGGER.log(Monlog.Severity.NOTIFY, "Reciever account overflowed. Aborting transfer.", debugLongDesc);
                return false;
            }

            senderAcc.changeBalance(-amount);
            recieverAcc.changeBalance(amount);

            em.merge(senderAcc); // Commit account changes
            em.merge(recieverAcc); // Commit account changes

            logTransaction(em, sender, reciever, amount, true);

            LOGGER.log(Monlog.Severity.DEBUG, "Committing transaction.", debugLongDesc);
            em.getTransaction().commit();

            return true;

        } catch (Exception e) {
            String shortDesc = "Exception trying to transfer.";
            LOGGER.log(Monlog.Severity.WARNING, shortDesc, debugLongDesc, e);
            return false;

        } finally {
            /* If the transaction is still active, the commit never happened. */
            /* Do a barrel rollback. */
            if(em.getTransaction().isActive()) {
                LOGGER.log(Monlog.Severity.DEBUG, "Rolling back transfer", debugLongDesc);
                em.getTransaction().rollback();
                try {
                    logTransaction(em, sender, reciever, amount, false);
                } catch (Exception e) {
                    LOGGER.log(Monlog.Severity.CRITICAL, "Transaction couldn't be logged to database!", debugLongDesc);
                }
            }
            em.close();
        }
    }

    private static boolean changeBalanceCash(long account, long amount) {
        String debugLongDesc = "Changing balance of AccountNumber: " + account + "\n" +
                               "Amount: " + amount + "\n";

        EntityManager em = EMF.getEntityManager();

        try {
            em.getTransaction().begin();

            Account acc = em.find(Account.class, account, LockModeType.PESSIMISTIC_WRITE);
            if (acc == null) {
                return false;
            }
            if ((acc.getBalance() + amount) < 0) {
                // Value out of range
                // This also works for when value goes above
                // Long.MAX_VALUE
                LOGGER.log(Monlog.Severity.INFO, "Balance lower than 0 or higher than Long.MAX_VALUE. Aborting changeBalance.", debugLongDesc);
                return false;
            }

            acc.changeBalance(amount);

            em.merge(acc); // Commit account changes
            logTransaction(em, account, 0, amount, true);

            LOGGER.log(Monlog.Severity.DEBUG, "Committing transaction.", debugLongDesc);
            em.getTransaction().commit();

            return true;

        } catch (Exception e) {
            String shortDesc = "Exception trying to change balance of account.";
            LOGGER.log(Monlog.Severity.WARNING, shortDesc, debugLongDesc, e);
            return false;
        } finally {
            /* If the transaction is still active, the commit never happened. */
            /* Do a barrel rollback. */
            if(em.getTransaction().isActive()) {
                LOGGER.log(Monlog.Severity.DEBUG, "Rolling back transfer", debugLongDesc);
                em.getTransaction().rollback();
                try {
                    logTransaction(em, account, 0, amount, false);
                } catch (Exception e) {
                    /* Couldn't log Transaction */
                    LOGGER.log(Monlog.Severity.CRITICAL, "Transaction couldn't be logged to database!", debugLongDesc);
                }
            }
            em.close();
        }
    }
    public static boolean withdrawCash(long account, long amount) {
        if (amount < 0) { return false; }
        return changeBalanceCash(account, -amount);
    }

    public static boolean depositCash(long account, long amount) {
        if (amount < 0) { return false; }
        return changeBalanceCash(account, amount);
    }

    private static void logTransaction(EntityManager em, long sender, long reciever, long amount, boolean success) {
        SavedTransaction st = new SavedTransaction();
        st.setSender(sender);
        st.setReciever(reciever);
        st.setAmount(amount);
        st.setSuccess(success);

        em.persist(st);
    }
}
