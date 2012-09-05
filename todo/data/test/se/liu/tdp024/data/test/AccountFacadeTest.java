/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package se.liu.tdp024.data.test;

import java.util.*;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import org.junit.*;
import se.liu.tdp024.entity.*;
import se.liu.tdp024.facade.AccountFacade;
import se.liu.tdp024.util.EMF;
import se.liu.tdp024.util.Monlog;

/**
 *
 */
public class AccountFacadeTest {

    public AccountFacadeTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
        Monlog.LoggingOn = false;
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
        Monlog.LoggingOn = true;
    }

    @Before
    public void setUp() {

    }

    @After
    public void tearDown() {
        EMF.close();
    }

    @Test
    public void testCreate() {
        // Create a new salary account
        Account account = AccountFacade.create(Account.SALARY,
                                               "personKey",
                                               "bankKey");
        Assert.assertNotNull(account);

        // Create a second account for same user
        Account  newAccount = AccountFacade.create(Account.SALARY,
                                                   "personKey",
                                                   "bankKey");
        Assert.assertTrue("Second account couldn't be created.",
            newAccount != null &&
            account.getAccountNumber() != newAccount.getAccountNumber());

        // Create a savings account
        account = AccountFacade.create(Account.SAVINGS,
                                       "personKey",
                                       "bankKey");
        Assert.assertNotNull(account);
    }

    @Test
    public void testFind() {
        Account account = AccountFacade.create(Account.SALARY, "person", "bank");

        Account acc = AccountFacade.find(account.getAccountNumber());

        Assert.assertNotNull(acc);
        Assert.assertEquals(account.getAccountNumber(), acc.getAccountNumber());

        // Test for non-existant find
        acc = AccountFacade.find(123456);
        Assert.assertNull(acc);
    }

    @Test
    public void testFindByPersonKey() {
        AccountFacade.create(Account.SALARY, "person", "bank");
        AccountFacade.create(Account.SALARY, "person", "bank1");
        AccountFacade.create(Account.SAVINGS, "person", "bank2");
        AccountFacade.create(Account.SAVINGS, "person2", "bank2");

        List<Account> accounts = AccountFacade.findByPersonKey("person");

        Assert.assertEquals(3, accounts.size());

        // Make sure an empty list returned if querying for not
        // existing person
        List<Account> emptyAccounts = AccountFacade.findByPersonKey("nonexisting");
        Assert.assertEquals(0, emptyAccounts.size());
    }

    @Test
    public void testFindByBankKey() {
        AccountFacade.create(Account.SALARY, "person", "bank");
        AccountFacade.create(Account.SALARY, "person", "bank1");
        AccountFacade.create(Account.SAVINGS, "person", "bank2");
        AccountFacade.create(Account.SAVINGS, "person2", "bank2");

        List<Account> accounts = AccountFacade.findByBankKey("bank2");

        Assert.assertEquals(2, accounts.size());

        // Make sure an empty list returned if querying for not
        // existing bank
        List<Account> emptyAccounts = AccountFacade.findByBankKey("nonexisting");
        Assert.assertEquals(0, emptyAccounts.size());
    }

    @Test
    public void testBalanceChanges() {
        long sender   = AccountFacade.create(Account.SALARY, "person1", "bank").getAccountNumber();
        long reciever = AccountFacade.create(Account.SALARY, "person2", "bank").getAccountNumber();
        Assert.assertFalse(sender == reciever || sender == 0 || reciever == 0);

        boolean status = AccountFacade.depositCash(sender, 1000); // deposit 1000 to sender account
        Assert.assertTrue(status);
        Assert.assertEquals(1000, AccountFacade.balance(sender));
        Assert.assertEquals(0, AccountFacade.balance(reciever));

        // Make sure the transaction is saved
        EntityManager em = EMF.getEntityManager();
        Query q = em.createQuery("SELECT t FROM SavedTransaction t;");
        SavedTransaction st = null;
        List results = q.getResultList();
        if (results.isEmpty()) {
            st = null;
        } else if (results.size() == 1) {
            Object o = results.get(0);
            st = (SavedTransaction)o;
        }
        Assert.assertNotNull(st);
        Assert.assertEquals(1000, st.getAmount());

        // Transfer some money from sender to reciever
        status = AccountFacade.transfer(sender, reciever, 100);
        Assert.assertTrue(status);
        Assert.assertEquals(900, AccountFacade.balance(sender));
        Assert.assertEquals(100, AccountFacade.balance(reciever));

        // Transfer some money back
        status = AccountFacade.transfer(reciever, sender, 50);
        Assert.assertTrue(status);
        Assert.assertEquals(950, AccountFacade.balance(sender));
        Assert.assertEquals(50, AccountFacade.balance(reciever));

        // Deposit cash to reciever
        status = AccountFacade.depositCash(reciever, 50);
        Assert.assertTrue(status);
        Assert.assertEquals(100, AccountFacade.balance(reciever));

        // Deposit cash to reciever
        status = AccountFacade.withdrawCash(reciever, 10);
        Assert.assertTrue(status);
        Assert.assertEquals(90, AccountFacade.balance(reciever));

        // Try to withdraw too much
        status = AccountFacade.withdrawCash(reciever, 100);
        Assert.assertFalse(status);
        Assert.assertEquals(90, AccountFacade.balance(reciever));

        // Try to deposit too much
        status = AccountFacade.depositCash(reciever, Long.MAX_VALUE);
        Assert.assertFalse(status);
        Assert.assertEquals(90, AccountFacade.balance(reciever));

        // Try to transfer too much from sender account
        status = AccountFacade.transfer(sender, reciever, 1000);
        Assert.assertFalse(status);
        Assert.assertEquals(950, AccountFacade.balance(sender));
        Assert.assertEquals(90, AccountFacade.balance(reciever));

        // Try to transfer so much the reciever account gets overflowed
        AccountFacade.depositCash(sender, Long.MAX_VALUE - 950);
        Assert.assertEquals(Long.MAX_VALUE, AccountFacade.balance(sender));
        status = AccountFacade.transfer(sender, reciever, Long.MAX_VALUE);
        Assert.assertFalse(status);
        Assert.assertEquals(Long.MAX_VALUE, AccountFacade.balance(sender));
        Assert.assertEquals(90, AccountFacade.balance(reciever));
    }
}
