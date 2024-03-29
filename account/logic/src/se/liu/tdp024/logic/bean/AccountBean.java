package se.liu.tdp024.logic.bean;

import com.google.gson.*;
import java.util.*;
import se.liu.tdp024.entity.Account;
import se.liu.tdp024.facade.AccountFacade;
import se.liu.tdp024.util.HTTPHelper;
import se.liu.tdp024.util.Monlog;

public abstract class AccountBean {
    private static final Monlog LOGGER = Monlog.getLogger(Monlog.Severity.INFO);

    private static String personApiUrl = "http://enterprise-systems.appspot.com/person/";
    private static String bankApiUrl =   "http://enterprise-systems.appspot.com/bank/";

    private static boolean personExists(String personKey) {
        if (personKey == null) { return false; }
        String resp = HTTPHelper.get(personApiUrl + "find.key", "key", personKey);
        JsonParser jp = new JsonParser();
        JsonElement json = jp.parse(resp);
        return json.isJsonObject();
    }

    private static boolean bankExists(String bankKey) {
        if (bankKey == null) { return false; }
        String resp = HTTPHelper.get(bankApiUrl + "find.key", "key", bankKey);
        JsonParser jp = new JsonParser();
        JsonElement json = jp.parse(resp);
        return json.isJsonObject();
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
        if(!personExists(personKey)) { return null; }
        return AccountFacade.findByPersonKey(personKey);
    }

    public static List<Account> findByBankKey(String bankKey) {
        if(!bankExists(bankKey)) { return null; }
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
