package se.liu.tdp024.web.service;

import com.google.gson.Gson;
import java.util.List;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import se.liu.tdp024.entity.Account;
import se.liu.tdp024.logic.bean.AccountBean;

/**
 * A service that provides an API for creating bank accounts and
 * providing bank-related operations such as deposit, withdraw and
 * transfer.
 *
 * @author bjoek022 & saman356
 */
@Path("/account")
public class AccountService {

    private static final Gson GSON = new Gson();

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

        if (bankKey == null || personKey == null || accountType == null) {
            return missingArgumentResponse();
        }

        Account account = AccountBean.create(accountType, personKey, bankKey);

        if (account != null) {
            String json = GSON.toJson(account);
            return Response.status(Response.Status.OK).entity(json).build();
        } else {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
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
        if (key == null) {
            return missingArgumentResponse();
        }
        List<Account> accounts = AccountBean.findByPersonKey(key);
        String json = GSON.toJson(accounts);
        return Response.status(Response.Status.OK).entity(json).build();
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
        if (key == null) {
            return missingArgumentResponse();
        }
        List<Account> accounts = AccountBean.findByBankKey(key);
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
        if (acc == null || amount == null) {
            return missingArgumentResponse();
        }

        boolean status = AccountBean.withdrawCash(acc, amount);

        if (status) {
            String json = GSON.toJson(status);
            return Response.status(Response.Status.OK).entity(json).build();
        } else {
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

        if (acc == null || amount == null) {
            return missingArgumentResponse();
        }
        boolean status = AccountBean.depositCash(acc, amount);

        if (status) {
            String json = GSON.toJson(status);
            return Response.status(Response.Status.OK).entity(json).build();
        } else {
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
        if (senderAcc == null || recieverAcc == null || amount == null) {
            return missingArgumentResponse();
        }

        boolean status = AccountBean.transfer(senderAcc, recieverAcc, amount);

        if (status) {
            String json = GSON.toJson(status);
            return Response.status(Response.Status.OK).entity(json).build();
        } else {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }

}
