package se.liu.tdp024.logic.bean;

import java.util.List;
import se.liu.tdp024.entity.Todo;
import se.liu.tdp024.facade.TodoFacade;

/*
 * It's not always clear why we need this logic layer, and
 * it's almost impossible to motivate it with this short example.
 * 
 * However when you develop the code for scenario 1 you should start
 * to understand the need for the logic layer, and the difference between
 * it and the other two.
 */

public abstract class TodoBean {
    
    public static Todo create(String title, String content) {
            
        /* In a real world application we probably would have
         * been making several requests to other services here.
         */
        
        long id = TodoFacade.create(title, content);
        
        if(id == 0) {
            return null;
        }
        
        Todo todo = TodoFacade.find(id);
        return todo;
        
    }
    
    public static Todo find(long id) {
        
        /* In a real world application we probably would have
         * been making several requests to other services here.
         */
        
        return TodoFacade.find(id);
    }
    
    public static List<Todo> list() {
        
        /* In a real world application we probably would have
         * been making several requests to other services here.
         */
        
        return TodoFacade.findAll();
    }
    
    public static void open(long id) {
        
        /* In a real world application we probably would have
         * been making several requests to other services here.
         */
        
        TodoFacade.updateOpen(id, true);
        
    }
    
    public static void close(long id) {
        
        /* In a real world application we probably would have
         * been making several requests to other services here.
         */
        
        TodoFacade.updateOpen(id, false);
        
    }
    
}
