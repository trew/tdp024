package se.liu.tdp024.logic.bean;

import com.google.gson.*;
import java.util.*;
import se.liu.tdp024.entity.Account;
import se.liu.tdp024.facade.AccountFacade;
import se.liu.tdp024.util.HTTPHelper;
import se.liu.tdp024.util.Monlog;

public abstract class AccountBean {
    private static final Monlog LOGGER = Monlog.getLogger();

    private static String personAPI_URL = "http://enterprise-systems.appspot.com/person/";
    private static String bankAPI_URL =   "http://enterprise-systems.appspot.com/bank/";

    private static boolean personExists(String personKey) {
        String resp = HTTPHelper.get(personAPI_URL + "find.key", "key", personKey);
        JsonParser jp = new JsonParser();
        JsonElement json = jp.parse(resp);
        if (json != null) {
            return json.isJsonObject();
        } else {
            /*
             * Log the response from PersonAPI
             */
            return false;
        }
    }

    private static boolean bankExists(String bankKey) {
        String resp = HTTPHelper.get(bankAPI_URL + "find.key", "key", bankKey);
        JsonParser jp = new JsonParser();
        JsonElement json = jp.parse(resp);
        if (json != null) {
            return json.isJsonObject();
        } else {
            /*
             * Log the response from BankAPI
             */
            return false;
        }
    }

    public static Account create(int accountType,
                              String personKey,
                              String bankKey) {
        if (!personExists(personKey)) {
            LOGGER.log(Monlog.Severity.WARNING,
                    "Tried to create account without PersonKey",
                    "AccountType: "+ accountType +
                    "\nPersonKey: "+ personKey +
                    "\nBankKey: " + bankKey);
            return null;
        }
        if (!bankExists(bankKey)) {
            //log
            return null;
        }
        return AccountFacade.create(accountType, personKey, bankKey);
    }

    public static Account getAccount(long accountNumber) {
        return AccountFacade.find(accountNumber);
    }

    public static List<Account> findByPersonKey(String personKey) {
        return AccountFacade.findByPersonKey(personKey);
    }

    public static List<Account> findByBankKey(String bankKey) {
        return AccountFacade.findByBankKey(bankKey);
    }

    public static long balance(long accountNumber) {
        return AccountFacade.balance(accountNumber);
    }

    public static boolean transfer(long sender, long reciever, long amount) {
        if(amount < 0) {
            return false;
        }
        return AccountFacade.transfer(sender, reciever, amount);
    }

    public static boolean depositCash(long account, long amount) {
        if(amount < 0) {
            return false;
        }
        return AccountFacade.depositCash(account, amount);
    }

    public static boolean withdrawCash(long account, long amount) {
        if(amount < 0) {
            return false;
        }
        return AccountFacade.withdrawCash(account, amount);
    }

}
