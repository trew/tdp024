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
        Monlog.setLoggingOff();
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
        Monlog.setLoggingOn();
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
    public void testCreateFail() {
        // Bad params
        Account account = AccountFacade.create(0, null, "key");
        Assert.assertNull(account);
        account = AccountFacade.create(0, "key", null);
        Assert.assertNull(account);
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
    public void testFindFail() {
        // Bad param
        Account account = AccountFacade.find(-5);
        Assert.assertNull(account);
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
    public void testBalanceWrongAccount() {
        long balance = AccountFacade.balance(1);
        Assert.assertEquals(-1, balance);
    }

    /* Balance changes tests */
    long sender;
    long reciever;
    boolean status;

    /**
     *  Helper function
     */
    private void createAccountsForTransferTests() {
        sender   = AccountFacade.create(Account.SALARY, "person1", "bank").getAccountNumber();
        reciever = AccountFacade.create(Account.SALARY, "person2", "bank").getAccountNumber();
        Assert.assertFalse(sender == reciever || sender == 0 || reciever == 0);
    }

    @Test
    public void testTransferToSelf() {
        // Transfer to self not possible
        createAccountsForTransferTests();
        status = AccountFacade.transfer(sender, sender, 100);
        Assert.assertFalse(status);
    }

    @Test
    public void testTransferInvalidSender() {
        createAccountsForTransferTests();
        status = AccountFacade.transfer(3, reciever, 100);
        Assert.assertFalse(status);
    }

    @Test
    public void testTransferInvalidReciever() {
        createAccountsForTransferTests();
        status = AccountFacade.transfer(sender, 3, 100);
        Assert.assertFalse(status);
    }

    @Test
    public void testTransferNotEnoughMoneyOnSender() {
        createAccountsForTransferTests();
        Account senderAcc = AccountFacade.find(sender);
        Assert.assertEquals(0, senderAcc.getBalance());

        status = AccountFacade.transfer(sender, reciever, 1);
        Assert.assertFalse(status);
    }

    @Test
    public void testTransferRecieverOverflowed() {
        createAccountsForTransferTests();
        status = AccountFacade.depositCash(sender, Long.MAX_VALUE);
        Assert.assertTrue(status);
        status = AccountFacade.depositCash(reciever, 100);
        Assert.assertTrue(status);

        status = AccountFacade.transfer(sender, reciever, Long.MAX_VALUE);
        Assert.assertFalse(status);
    }

    @Test
    public void testTransfer() {
        createAccountsForTransferTests();
        status = AccountFacade.depositCash(sender, 1000); // deposit 1000 to sender account
        Assert.assertTrue(status);
        Assert.assertEquals(1000, AccountFacade.balance(sender));
        Assert.assertEquals(0, AccountFacade.balance(reciever));

        status = AccountFacade.transfer(sender, reciever, 300);
        Assert.assertTrue(status);
        Assert.assertEquals(700, AccountFacade.balance(sender));
        Assert.assertEquals(300, AccountFacade.balance(reciever));
    }

    @Test
    public void testSavedTransaction() {
        createAccountsForTransferTests();
        status = AccountFacade.depositCash(sender, 1000); // deposit 1000 to sender account
        Assert.assertTrue(status);

        status = AccountFacade.transfer(sender, reciever, 300);
        Assert.assertTrue(status);

        // Make sure the transaction is saved
        EntityManager em = EMF.getEntityManager();
        Query q = em.createQuery("SELECT t FROM SavedTransaction t;");
        SavedTransaction st = null;
        List results = q.getResultList();
        if (results.size() == 2) {
            st = (SavedTransaction)results.get(1);
        } // else:
          // results.size() != 2
          // for some reason two transactions wasn't logged.
        Assert.assertNotNull(st);
        Assert.assertEquals(300, st.getAmount());
    }

    @Test
    public void testDepositCash() {
        createAccountsForTransferTests();
        Assert.assertEquals(0, AccountFacade.balance(reciever));

        status = AccountFacade.depositCash(reciever, 100);
        Assert.assertTrue(status);
        Assert.assertEquals(100, AccountFacade.balance(reciever));
    }

    @Test
    public void testWithdrawCash() {
        createAccountsForTransferTests();
        AccountFacade.depositCash(reciever, 1000);
        Assert.assertEquals(1000, AccountFacade.balance(reciever));

        status = AccountFacade.withdrawCash(reciever, 100);
        Assert.assertTrue(status);
        Assert.assertEquals(900, AccountFacade.balance(reciever));
    }

    @Test
    public void testDepositFailure() {
        createAccountsForTransferTests();
        AccountFacade.depositCash(reciever, 100);

        // Try to deposit a negative number
        status = AccountFacade.depositCash(reciever, -100);
        Assert.assertFalse(status);
        Assert.assertEquals(100, AccountFacade.balance(reciever));

        // Try to deposit too much, overflow the account
        status = AccountFacade.depositCash(reciever, Long.MAX_VALUE);
        Assert.assertFalse(status);
        Assert.assertEquals(100, AccountFacade.balance(reciever));
    }

    @Test
    public void testWithdrawFailure() {
        createAccountsForTransferTests();
        AccountFacade.depositCash(reciever, 1000);

        // Try to withdraw a negative number
        status = AccountFacade.withdrawCash(reciever, -100);
        Assert.assertFalse(status);
        Assert.assertEquals(1000, AccountFacade.balance(reciever));

        // Try to withdraw too much
        status = AccountFacade.withdrawCash(reciever, 2000);
        Assert.assertFalse(status);
        Assert.assertEquals(1000, AccountFacade.balance(reciever));
    }
}
