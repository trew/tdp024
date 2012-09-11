package se.liu.tdp024.data.test;

import javax.persistence.EntityManager;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import se.liu.tdp024.entity.Account;
import se.liu.tdp024.util.EMF;
import se.liu.tdp024.util.Monlog;

public class AccountTest {

    private Account acc;

    public AccountTest() {}    

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
        acc = new Account();
        acc.setPersonKey(""); // cannot be null
        acc.setBankKey("");   // cannot be null
    }

    @After
    public void tearDown() {
        EMF.close();
    }

    @Test
    public void testSetAccountTypeFailure() {
        Assert.assertFalse(acc.setAccountType(-1));
        Assert.assertFalse(acc.setAccountType(2));
    }

    @Test
    public void testGetAccountNumber() {
        // No ID given until inserted into DB
        Assert.assertEquals(0L, acc.getAccountNumber());

        EntityManager em = EMF.getEntityManager();
        em.getTransaction().begin();
        em.persist(acc);
        em.getTransaction().commit();

        // First account created is always 1
        Assert.assertEquals(1L, acc.getAccountNumber());
        
    }

    @Test
    public void testGetSetAccountType() {
        acc.setAccountType(Account.SALARY);
        Assert.assertEquals(Account.SALARY, acc.getAccountType());
    }

    @Test
    public void testGetSetPersonKey() {
        acc.setPersonKey("testkey");
        Assert.assertEquals("testkey", acc.getPersonKey());
    }

    @Test
    public void testGetSetBankKey() {
        acc.setBankKey("testkey");
        Assert.assertEquals("testkey", acc.getBankKey());
    }

    @Test
    public void testGetAndChangeBalance() {
        acc.changeBalance(10);
        Assert.assertEquals(10, acc.getBalance());
        
        acc.changeBalance(-5);
        Assert.assertEquals(5, acc.getBalance());
    }
}