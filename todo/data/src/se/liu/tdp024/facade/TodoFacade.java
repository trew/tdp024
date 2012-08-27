package se.liu.tdp024.facade;

import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.Query;
import se.liu.tdp024.entity.Todo;
import se.liu.tdp024.util.EMF;

public abstract class TodoFacade {

    public static long create(String title, String content) {
        
        EntityManager em = EMF.getEntityManager();
        
        try {
            
            em.getTransaction().begin();
            
            Todo todo = new Todo();
            todo.setTitle(title);
            todo.setContent(content);
            
            em.persist(todo);
            
            em.getTransaction().commit();
            
            return todo.getId();
            
        } catch (Exception e) {
            /*
             * Should log something here
             */
            return 0;
        } finally {
            
            if(em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            
            em.close();
        }
        
    }
    
    public static long updateOpen(long id, boolean open) {
        
        EntityManager em = EMF.getEntityManager();
        
        try {
            
            em.getTransaction().begin();
            
            Todo todo = em.find(Todo.class, id, LockModeType.PESSIMISTIC_WRITE);
            todo.setOpen(open);
            
            em.merge(todo);
            
            em.getTransaction().commit();
            
            return todo.getId();
            
        } catch (Exception e) {
            /*
             * Should log something here
             */
            return 0;
        } finally {
            
            if(em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            
            em.close();
        }
        
    }

    public static Todo find(long id) {
        
        EntityManager em = EMF.getEntityManager();
        
        try {
            return em.find(Todo.class, id);
        } catch (Exception e) {
            /*
             * Should log something here
             */
            return null;
        } finally {
            em.close();
        }
    }

    public static Todo findByTitle(String title) {
        
        EntityManager em = EMF.getEntityManager();
        
        try {
            
            Query query = em.createQuery("SELECT t FROM Todo t WHERE t.title = :title");
            query.setParameter("title", title);
            return (Todo)query.getSingleResult();
            
        } catch (Exception e) {
            /*
             * Should log something here
             */
            return null;
        } finally {
            em.close();
        }
    }

    public static List<Todo> findAll() {
        
        EntityManager em = EMF.getEntityManager();
        
        try {
            
            Query query = em.createQuery("SELECT t FROM Todo t");
            return query.getResultList();
            
        } catch (Exception e) {
            /*
             * Should log something here
             */
            return null;
        } finally {
            em.close();
        }
        
    }
}
