package se.liu.tdp024.util;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public abstract class EMF {

    private static EntityManagerFactory entityManagerFactory;

    public static EntityManager getEntityManager() {

        synchronized (EMF.class) {
            if (entityManagerFactory == null || !entityManagerFactory.isOpen()) {

                entityManagerFactory = Persistence.createEntityManagerFactory("data-dataPU");

            }
        }

        return entityManagerFactory.createEntityManager();
    }

    public static void close() {
        entityManagerFactory.close();
    }
}
