package se.liu.tdp024.web.service;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.List;
import se.liu.tdp024.exception.*;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import se.liu.tdp024.entity.Account;
import se.liu.tdp024.logic.bean.AccountBean;
import se.liu.tdp024.util.Monlog;

/**
 * A service that provides an API for creating bank accounts and
 * providing bank-related operations such as deposit, withdraw and
 * transfer.
 *
 * @author bjoek022 & saman356
 */
@Path("/account")
public class AccountService {
    private static final Monlog LOGGER = Monlog.getLogger(Monlog.Severity.INFO);

    private static final Gson GSON = new Gson();

    private Response createExceptionResponse(AccountException e)
    {
        JsonObject errors = new JsonObject();
        errors.addProperty("type", e.getType());
        errors.addProperty("code", e.getCode());
        errors.addProperty("message", e.getMessage());

        return Response.status(e.getCode()).entity(new Gson().toJson(errors)).build();
    }

    private Response missingArgumentResponse() {
        String json = "{'error' : 'missing input parameters'}";
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(json).build();
    }

    /**
     * Creates a new account using the specified parameters.
     *
     * @param personKey     The unique key identifying the person
     * @param bankKey       The unique key identifying the bank
     * @param accountType   Type of account where Salary = 0, Savings = 1
     * @return              Returns the created account in JSON-format
     *
     */
    @GET
    @Path("/create")
    public Response createAccount(
            @QueryParam("personkey") String personKey,
            @QueryParam("bankkey") String bankKey,
            @QueryParam("type") Integer accountType) {
        String debugLongDesc = "AccountType: " + accountType + "\n" +
                          "PersonKey: " + personKey + "\n" +
                          "BankKey: "+ bankKey + "\n";

        if (bankKey == null || personKey == null || accountType == null) {
            LOGGER.log(Monlog.Severity.NOTIFY, "Missing argument", debugLongDesc);
            return createExceptionResponse(new IllegalArgumentAccountException("Missing Argument."));
        }

        Account account;
        try {
            account = AccountBean.create(accountType, personKey, bankKey);
            LOGGER.log(Monlog.Severity.INFO, "Accepted request", debugLongDesc);
            String json = GSON.toJson(account);
            return Response.status(Response.Status.OK).entity(json).build();
        } catch (AccountException e) {
            LOGGER.log(Monlog.Severity.NOTIFY, "Account couldn't be created", debugLongDesc, e);
            return createExceptionResponse(e);
        }
    }

    /**
     * Lists all accounts connected to the specified personkey.
     * If the personKey doesn't exist, return Internal Server Error.
     *
     * @param key   An unique key identifying the person
     * @return      Returns an array of accounts in JSON-format
     *              Internal server error if personKey does not
     *              exist.
     */
    @GET
    @Path("/list.personkey")
    public Response listByPersonKey(@QueryParam("key") String key) {
        String debugLongDesc = "PersonKey: " + key + "\n";

        if (key == null) {
            LOGGER.log(Monlog.Severity.NOTIFY, "Missing argument", debugLongDesc);
            return createExceptionResponse(new IllegalArgumentAccountException("Argument `Key` is missing."));
        }

        List<Account> accounts;
        try {
            accounts = AccountBean.findByPersonKey(key);
            LOGGER.log(Monlog.Severity.INFO, "Accepted request", debugLongDesc);
            String json = GSON.toJson(accounts);
            return Response.status(Response.Status.OK).entity(json).build();
        } catch (AccountException e){
            LOGGER.log(Monlog.Severity.NOTIFY, "Person not found", debugLongDesc);
            return createExceptionResponse(e);
        }

    }

    /**
     * Lists all accounts connected to the specified bankkey.
     *
     * @param key   An unique key identifying the bank
     * @return      Returns an array of accounts in JSON-format
     */
    @GET
    @Path("/list.bankkey")
    public Response listByBankKey(@QueryParam("key") String key) {
        String debugLongDesc = "BankKey: " + key + "\n";

        if (key == null) {
            LOGGER.log(Monlog.Severity.NOTIFY, "Missing argument", debugLongDesc);
            return missingArgumentResponse();
        }
        List<Account> accounts = AccountBean.findByBankKey(key);

        if(accounts == null) {
            LOGGER.log(Monlog.Severity.NOTIFY, "Bank not found", debugLongDesc);
            String json = "{'error' : 'Bank cannot be found.'}";
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(json).build();
        }

        LOGGER.log(Monlog.Severity.INFO, "Accepted request", debugLongDesc);
        String json = GSON.toJson(accounts);
        return Response.status(Response.Status.OK).entity(json).build();
    }

    /**
     * Withdraws an amount of money from an account
     *
     * @param acc       The accountnumber of the account
     * @param amount    The amount to be withdrawn
     * @return          Status-code (200 - OK / 500 - Internal server error)
     */
    @GET
    @Path("/withraw")
    public Response withdraw(
            @QueryParam("acc") Long acc,
            @QueryParam("amount") Long amount) {
        String debugLongDesc = "AccountNumber: " + acc + "\n" +
                               "Amount: " + amount + "\n";

        if (acc == null || amount == null) {
            LOGGER.log(Monlog.Severity.NOTIFY, "Missing argument", debugLongDesc);
            return missingArgumentResponse();
        }

        boolean status = AccountBean.withdrawCash(acc, amount);

        if (status) {
            LOGGER.log(Monlog.Severity.INFO, "Accepted request", debugLongDesc);
            String json = GSON.toJson(status);
            return Response.status(Response.Status.OK).entity(json).build();
        } else {
            LOGGER.log(Monlog.Severity.NOTIFY, "Withdraw couldn't be performed.", debugLongDesc);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Deposits an amount of money to an account
     *
     * @param acc       The accountnumber of the account
     * @param amount    The amount to be deposited
     * @return          Status-code (200 - OK / 500 - Internal server error)
     */
    @GET
    @Path("/deposit")
    public Response deposit(
            @QueryParam("acc") Long acc,
            @QueryParam("amount") Long amount) {
        String debugLongDesc = "AccountNumber: " + acc + "\n" +
                               "Amount: " + amount + "\n";

        if (acc == null || amount == null) {
            LOGGER.log(Monlog.Severity.NOTIFY, "Missing argument", debugLongDesc);
            return missingArgumentResponse();
        }
        boolean status = AccountBean.depositCash(acc, amount);

        if (status) {
            LOGGER.log(Monlog.Severity.INFO, "Accepted request", debugLongDesc);
            String json = GSON.toJson(status);
            return Response.status(Response.Status.OK).entity(json).build();
        } else {
            LOGGER.log(Monlog.Severity.NOTIFY, "Deposit couldn't be performed.", debugLongDesc);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Transfers an amount of money from a sender account to a reciever account
     *
     * @param senderAcc     The account that money should be transfered from
     * @param recieverAcc   The account that money should be transfered to
     * @param amount        The amount of money to transfer
     * @return              Status-code (200 - OK / 500 - Internal server error)
     */
    @GET
    @Path("/transfer")
    public Response transfer(
            @QueryParam("sender") Long senderAcc,
            @QueryParam("reciever") Long recieverAcc,
            @QueryParam("amount") Long amount) {
        String debugLongDesc = "Sender AccountNumber: " + senderAcc + "\n" +
                               "Reciever AccountNumber: " + recieverAcc + "\n" +
                               "Amount: " + amount + "\n";
        if (senderAcc == null || recieverAcc == null || amount == null) {
            LOGGER.log(Monlog.Severity.NOTIFY, "Missing argument", debugLongDesc);
            return missingArgumentResponse();
        }

        try {
            AccountBean.transfer(senderAcc, recieverAcc, amount);
            LOGGER.log(Monlog.Severity.INFO, "Accepted request", debugLongDesc);
            String json = GSON.toJson(true);
            return Response.status(Response.Status.OK).entity(json).build();
        } catch (AccountException e) {
            LOGGER.log(Monlog.Severity.NOTIFY, "Transfer couldn't be performed.", debugLongDesc);
            return createExceptionResponse(e);
        }
    }
}
