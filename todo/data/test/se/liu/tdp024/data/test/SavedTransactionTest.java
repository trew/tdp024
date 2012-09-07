package se.liu.tdp024.data.test;

import java.util.Date;
import javax.persistence.EntityManager;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import se.liu.tdp024.entity.SavedTransaction;
import se.liu.tdp024.util.EMF;
import se.liu.tdp024.util.Monlog;

public class SavedTransactionTest {

    private SavedTransaction transaction;

    public SavedTransactionTest() {}    

    @BeforeClass
    public static void setUpClass() throws Exception {
        Monlog.setLoggingOff();
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
        Monlog.setLoggingOn();
    }

    @Before
    public void setUp() {
        transaction = new SavedTransaction();
    }

    @After
    public void tearDown() {
        EMF.close();
    }

    @Test
    public void testGetID() {
        // No ID given until inserted into DB
        Assert.assertEquals(0L, transaction.getID());

        EntityManager em = EMF.getEntityManager();
        em.getTransaction().begin();
        em.persist(transaction);
        em.getTransaction().commit();

        // First account created is always 1
        Assert.assertEquals(1L, transaction.getID());
    }

    @Test
    public void testGetSetSender() {
        transaction.setSender(123L);
        Assert.assertEquals(123L, transaction.getSender());
    }

    @Test
    public void testGetSetReciever() {
        transaction.setReciever(123L);
        Assert.assertEquals(123L, transaction.getReciever());
    }

    @Test
    public void testGetSetAmount() {
        transaction.setAmount(123L);
        Assert.assertEquals(123L, transaction.getAmount());
    }

    @Test
    public void testGetSetSuccess() {
        transaction.setSuccess(true);
        Assert.assertEquals(true, transaction.getSuccess());
        transaction.setSuccess(false);
        Assert.assertEquals(false, transaction.getSuccess());
    }

    @Test
    public void testGetDateTime() {
        Date date = transaction.getDatetime();
        Assert.assertNull(date);
        
        EntityManager em = EMF.getEntityManager();
        em.getTransaction().begin();
        em.persist(transaction);
        em.getTransaction().commit();
        
        Assert.assertNotNull(transaction.getDatetime());
    }
}