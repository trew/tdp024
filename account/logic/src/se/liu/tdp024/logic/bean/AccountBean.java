package se.liu.tdp024.logic.bean;

import com.google.gson.*;
import java.util.*;
import javax.security.auth.login.AccountException;
import se.liu.tdp024.entity.Account;
import se.liu.tdp024.exception.*;
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
                              String bankKey) throws IllegalArgumentAccountException, DatabaseException {
        if (accountType < 0 || accountType > 1) {
            throw new IllegalArgumentAccountException("Account type is of unknown type.");
        }

        if (!personExists(personKey)) {
            throw new IllegalArgumentAccountException("Person with provided key does not exist.");
        }
        if (!bankExists(bankKey)) {
            throw new IllegalArgumentAccountException("Bank with provided key does not exist.");
        }
        return AccountFacade.create(accountType, personKey, bankKey);
    }

    public static Account getAccount(long accountNumber) throws NotFoundException {
        return AccountFacade.find(accountNumber);
    }

    public static List<Account> findByPersonKey(String personKey) throws IllegalArgumentAccountException {
        if(!personExists(personKey)) {
            throw new IllegalArgumentAccountException("PersonKey missing.");
        }
        return AccountFacade.findByPersonKey(personKey);
    }

    public static List<Account> findByBankKey(String bankKey) {
        if(!bankExists(bankKey)) { return null; }
        return AccountFacade.findByBankKey(bankKey);
    }

    public static long balance(long accountNumber) throws NotFoundException {
        return AccountFacade.balance(accountNumber);
    }

    public static void transfer(long sender, long reciever, long amount)
            throws IllegalArgumentAccountException, NotFoundException,
                    DatabaseException, ValidationException {
        if(amount < 0) {
            throw new IllegalArgumentAccountException("Amount cannot be less than 0");
        }
        AccountFacade.transfer(sender, reciever, amount);
    }

    public static void depositCash(long account, long amount)
            throws DatabaseException, NotFoundException,
            ValidationException, IllegalArgumentAccountException {
        if (amount < 0) {
            throw new IllegalArgumentAccountException("Amount cannot be less than 0.");
        }
        AccountFacade.depositCash(account, amount);
    }

    public static void withdrawCash(long account, long amount)
            throws DatabaseException, NotFoundException,
            ValidationException, IllegalArgumentAccountException {
        if (amount < 0) {
            throw new IllegalArgumentAccountException("Amount cannot be less than 0.");
        }
        AccountFacade.withdrawCash(account, amount);
    }

}
