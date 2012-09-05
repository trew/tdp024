package se.liu.tdp024.web.service;

import com.google.gson.Gson;
import java.util.List;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import se.liu.tdp024.entity.Account;
import se.liu.tdp024.entity.Todo;
import se.liu.tdp024.logic.bean.AccountBean;
import se.liu.tdp024.logic.bean.TodoBean;

@Path("/account")
public class AccountService {

    private static final Gson GSON = new Gson();

    @GET
    @Path("/create")
    public Response createAccount(
            @QueryParam("personid") String personID,
            @QueryParam("bankid") String bankID,
            @QueryParam("type") String accountType) {

        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();

    }

    @GET
    @Path("/list.personid")
    public Response listByPersonID(@QueryParam("id") String id) {

        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();

    }

    @GET
    @Path("/list.bankid")
    public Response listByBankID(@QueryParam("id") String id) {

        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();

    }
}