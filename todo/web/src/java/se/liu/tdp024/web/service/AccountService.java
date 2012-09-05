package se.liu.tdp024.web.service;

import com.google.gson.Gson;
import java.util.List;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import se.liu.tdp024.entity.Account;
import se.liu.tdp024.facade.AccountFacade;
import se.liu.tdp024.logic.bean.AccountBean;

@Path("/account")
public class AccountService {

    private static final Gson GSON = new Gson();

    @GET
    @Path("/create")
    public Response createAccount(
            @QueryParam("personkey") String personKey,
            @QueryParam("bankkey") String bankKey,
            @QueryParam("type") int accountType) {

        Account account = AccountBean.create(accountType, personKey, bankKey);

        if (account != null) {
            String json = GSON.toJson(account);
            return Response.status(Response.Status.OK).entity(json).build();
        } else {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GET
    @Path("/list.personkey")
    public Response listByPersonKey(@QueryParam("key") String key) {
        List<Account> accounts = AccountBean.findByPersonKey(key);
        String json = GSON.toJson(accounts);
        return Response.status(Response.Status.OK).entity(json).build();
    }

    @GET
    @Path("/list.bankkey")
    public Response listByBankKey(@QueryParam("key") String key) {
        List<Account> accounts = AccountBean.findByPersonKey(key);
        String json = GSON.toJson(accounts);
        return Response.status(Response.Status.OK).entity(json).build();
    }
    /*
     * /account/withdraw          param: acc, amount
     * /account/deposit           param: acc, amount
     * /account/transfer          param: sender, reciever, amount
     */


    @GET
    @Path("/withraw")
    public Response withdraw(
            @QueryParam("acc") long acc,
            @QueryParam("amount") long amount) {

        boolean status = AccountBean.withdrawCash(acc, amount);

        if (status == true) {
            String json = GSON.toJson(status);
            return Response.status(Response.Status.OK).entity(json).build();
        } else {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }
 /*
    @GET
    @Path("/deposit")
    public Response withdraw(
            @QueryParam("acc") long acc,
            @QueryParam("amount") long amount) {

        boolean status = AccountBean.withdrawCash(acc, amount);

        if (status == true) {
            String json = GSON.toJson(status);
            return Response.status(Response.Status.OK).entity(json).build();
        } else {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }
    */

    /*
    @GET
    @Path("/transfer")
    public Response withdraw(
            @QueryParam("acc") long acc,
            @QueryParam("amount") long amount) {

        boolean status = AccountBean.withdrawCash(acc, amount);

        if (status == true) {
            String json = GSON.toJson(status);
            return Response.status(Response.Status.OK).entity(json).build();
        } else {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }
    */
}
