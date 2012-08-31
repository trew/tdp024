package se.liu.tdp024.logic.bean;

import java.util.*;
import se.liu.tdp024.entity.Account;
import se.liu.tdp024.logic.util.HTTPHelper;

public abstract class AccountBean {

    public static long create(int accountType,
                              String personKey,
                              String bankKey) {
        /*
         * Find the person in the person-db
         */
        String personFound = HTTPHelper.get("dataURL", personKey);

        /*
         * Find the bank in the bank-db
         */

        /*
         * Try to create a
         */
        return 0;
    }

    public static Account getAccount(long accountNumber) {
        return null;
    }

    public static List<Account> findByPersonKey(String personKey) {
        return new LinkedList<Account>();
    }

    public static List<Account> findByBankKey(String bankKey) {
        return new LinkedList<Account>();
    }

    public static boolean deposit(long accountNumber, long amount) {
        return false;
    }

    public static boolean withdraw(long accountNumber, long amount) {
        return false;
    }
}
