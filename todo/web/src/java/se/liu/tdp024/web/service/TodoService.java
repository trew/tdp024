package se.liu.tdp024.web.service;

import com.google.gson.Gson;
import java.util.List;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import se.liu.tdp024.entity.Todo;
import se.liu.tdp024.logic.bean.TodoBean;

@Path("/todo")
public class TodoService {

    private static final Gson GSON = new Gson();

    @GET
    @Path("/create")
    public Response create(
            @QueryParam("title") String title,
            @QueryParam("content") String content) {

        Todo todo = TodoBean.create(title, content);

        if (todo != null) {
            String json = GSON.toJson(todo);
            return Response.status(Response.Status.OK).entity(json).build();
        } else {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }

    }

    @GET
    @Path("/find.id")
    public Response findById(@QueryParam("id") long id) {


        Todo todo = TodoBean.find(id);

        if (todo != null) {
            String json = GSON.toJson(todo);
            return Response.status(Response.Status.OK).entity(json).build();
        } else {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }

    }
    
    @GET
    @Path("/list")
    public Response list() {
        
        List<Todo> todos = TodoBean.list();
     
        if (todos != null) {
            String json = GSON.toJson(todos);
            return Response.status(Response.Status.OK).entity(json).build();
        } else {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
        
    }
}
