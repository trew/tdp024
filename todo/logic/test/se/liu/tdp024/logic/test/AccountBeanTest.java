/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package se.liu.tdp024.logic.test;

import com.google.gson.*;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import junit.framework.Assert;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import se.liu.tdp024.entity.Account;
import se.liu.tdp024.entity.SavedTransaction;
import se.liu.tdp024.logic.bean.AccountBean;
import se.liu.tdp024.util.EMF;
import se.liu.tdp024.util.HTTPHelper;

public class AccountBeanTest {
    private static String PersonAPI_URL = "http://enterprise-systems.appspot.com/person/";
    private static String BankAPI_URL =   "http://enterprise-systems.appspot.com/bank/";

    private static String ExistingBankKey = "";
    private static String ExistingPersonKey = "";
    private static String nonExistingPersonKey = "thispersondoesnotexist";
    private static String nonExistingBankKey = "thisbankdoesnotexist";

    public AccountBeanTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
        JsonParser jp = new JsonParser();
        JsonObject jo;
        JsonElement je;

        // Get an existing Person for testing
        String resp = HTTPHelper.get(PersonAPI_URL + "find.name", "name", "Zorro");
        Assert.assertFalse("Couldn't connect to PersonAPI", resp == null);
        je = jp.parse(resp); Assert.assertTrue(je.isJsonObject());
        jo = je.getAsJsonObject();
        Assert.assertTrue("Person \"Zorro\" not found", jo.isJsonObject());
        ExistingPersonKey = jo.get("key").getAsString();

        // Get an existing Bank for testing
        resp = HTTPHelper.get(BankAPI_URL + "find.name", "name", "SWEDBANK");
        Assert.assertFalse("Couldn't connect to BankAPI", resp == null);
        je = jp.parse(resp); Assert.assertTrue(je.isJsonObject());
        jo = je.getAsJsonObject();
        Assert.assertTrue("Bank \"SWEDBANK\" not found", jo.isJsonObject());
        ExistingBankKey = jo.get("key").getAsString();
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
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
        // Make sure account was created
        Assert.assertTrue(AccountBean.create(Account.SALARY, ExistingPersonKey, ExistingBankKey) != null);
        Assert.assertTrue(AccountBean.create(Account.SAVINGS, ExistingPersonKey, ExistingBankKey) != null);
    }

    @Test
    public void testCreateFailure() {
        JsonParser jp = new JsonParser();

        // Bad accounts type
        Assert.assertTrue(AccountBean.create(5, ExistingPersonKey, ExistingBankKey) == null);
        Assert.assertTrue(AccountBean.create(-1, ExistingPersonKey, ExistingBankKey) == null);

        // Test that account cannot be created if Person does not exist, but bank does.
        String responseP = HTTPHelper.get(PersonAPI_URL + "find.key", "key", nonExistingPersonKey);
        Assert.assertFalse(responseP == null);
        Assert.assertTrue(jp.parse(responseP).isJsonNull()); // Make sure the response was empty

        // Make sure creation couldn't happen with a non-existing Person
        Assert.assertTrue(AccountBean.create(Account.SALARY, nonExistingPersonKey, ExistingBankKey) == null);

        // Test that account cannot be created if Bank does not exist, but Person does.
        responseP = HTTPHelper.get(BankAPI_URL + "find.key", "key", nonExistingBankKey);
        Assert.assertFalse(responseP == null);
        Assert.assertTrue(jp.parse(responseP).isJsonNull()); // Make sure the response was empty

        // Make sure creation couldn't happen with a non-existing Bank
        Assert.assertTrue(AccountBean.create(Account.SALARY, ExistingPersonKey, nonExistingBankKey) == null);
    }

    @Test
    public void testFindByBankKey() {
        AccountBean.create(Account.SALARY, ExistingPersonKey, ExistingBankKey);
        AccountBean.create(Account.SALARY, ExistingPersonKey, ExistingBankKey);
        AccountBean.create(Account.SAVINGS, ExistingPersonKey, ExistingBankKey);
        AccountBean.create(Account.SAVINGS, ExistingPersonKey, ExistingBankKey);

        List<Account> accounts = AccountBean.findByBankKey(ExistingBankKey);

        Assert.assertEquals(4, accounts.size());

    }

    @Test
    public void testFindByBankKeyFailure() {
        // Null is returned if querying for not existing bank
        List<Account> emptyAccounts = AccountBean.findByBankKey(nonExistingBankKey);
        Assert.assertNull(emptyAccounts);
    }

    @Test
    public void testFindByPersonKey() {
        AccountBean.create(Account.SALARY, ExistingPersonKey, ExistingBankKey);
        AccountBean.create(Account.SALARY, ExistingPersonKey, ExistingBankKey);
        AccountBean.create(Account.SAVINGS, ExistingPersonKey, ExistingBankKey);
        AccountBean.create(Account.SAVINGS, ExistingPersonKey, ExistingBankKey);

        List<Account> accounts = AccountBean.findByPersonKey(ExistingPersonKey);

        Assert.assertEquals(4, accounts.size());
    }

    @Test
    public void testFindByPersonKeyFailure() {
        // Null is returned if querying for not existing person
        List<Account> emptyAccounts = AccountBean.findByPersonKey(nonExistingPersonKey);
        Assert.assertNull(emptyAccounts);
    }


    long sender;
    long reciever;
    boolean status;

    private void createAccountsForTransferTests() {
        sender   = AccountBean.create(Account.SALARY, ExistingPersonKey, ExistingBankKey).getAccountNumber();
        reciever = AccountBean.create(Account.SAVINGS, ExistingPersonKey, ExistingBankKey).getAccountNumber();
        Assert.assertFalse(sender == reciever || sender == 0 || reciever == 0);
    }

    @Test
    public void testTransferToSelf() {
        // Transfer to self not possible
        createAccountsForTransferTests();
        status = AccountBean.transfer(sender, sender, 100);
        Assert.assertFalse(status);
    }

    @Test
    public void testTransferInvalidSender() {
        createAccountsForTransferTests();
        status = AccountBean.transfer(3, reciever, 100);
        Assert.assertFalse(status);
    }

    @Test
    public void testTransferInvalidReciever() {
        createAccountsForTransferTests();
        status = AccountBean.transfer(sender, 3, 100);
        Assert.assertFalse(status);
    }

    @Test
    public void testTransferNotEnoughMoneyOnSender() {
        createAccountsForTransferTests();
        Account senderAcc = AccountBean.getAccount(sender);
        Assert.assertEquals(0, senderAcc.getBalance());

        status = AccountBean.transfer(sender, reciever, 1);
        Assert.assertFalse(status);
    }

    @Test
    public void testTransferRecieverOverflowed() {
        createAccountsForTransferTests();
        status = AccountBean.depositCash(sender, Long.MAX_VALUE);
        Assert.assertTrue(status);
        status = AccountBean.depositCash(reciever, 100);
        Assert.assertTrue(status);

        status = AccountBean.transfer(sender, reciever, Long.MAX_VALUE);
        Assert.assertFalse(status);
    }

    @Test
    public void testBadTransfer() {
        createAccountsForTransferTests();
        Assert.assertFalse(AccountBean.transfer(sender, reciever, -50));
    }

    @Test
    public void testTransfer() {
        createAccountsForTransferTests();
        status = AccountBean.depositCash(sender, 1000); // deposit 1000 to sender account
        Assert.assertTrue(status);
        Assert.assertEquals(1000, AccountBean.balance(sender));
        Assert.assertEquals(0, AccountBean.balance(reciever));

        status = AccountBean.transfer(sender, reciever, 300);
        Assert.assertTrue(status);
        Assert.assertEquals(700, AccountBean.balance(sender));
        Assert.assertEquals(300, AccountBean.balance(reciever));
    }

    @Test
    public void testSavedTransaction() {
        createAccountsForTransferTests();
        status = AccountBean.depositCash(sender, 1000); // deposit 1000 to sender account
        Assert.assertTrue(status);

        status = AccountBean.transfer(sender, reciever, 300);
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
        Assert.assertEquals(0, AccountBean.balance(reciever));

        status = AccountBean.depositCash(reciever, 100);
        Assert.assertTrue(status);
        Assert.assertEquals(100, AccountBean.balance(reciever));
    }

    @Test
    public void testWithdrawCash() {
        createAccountsForTransferTests();
        AccountBean.depositCash(reciever, 1000);
        Assert.assertEquals(1000, AccountBean.balance(reciever));

        status = AccountBean.withdrawCash(reciever, 100);
        Assert.assertTrue(status);
        Assert.assertEquals(900, AccountBean.balance(reciever));
    }

    @Test
    public void testDepositFailure() {
        createAccountsForTransferTests();
        AccountBean.depositCash(reciever, 100);

        // Try to deposit a negative number
        status = AccountBean.depositCash(reciever, -100);
        Assert.assertFalse(status);
        Assert.assertEquals(100, AccountBean.balance(reciever));

        // Try to deposit too much, overflow the account
        status = AccountBean.depositCash(reciever, Long.MAX_VALUE);
        Assert.assertFalse(status);
        Assert.assertEquals(100, AccountBean.balance(reciever));

        // Try to deposit to non-existing Account
        status = AccountBean.depositCash(3L, 2000);
        Assert.assertFalse(status);
    }

    @Test
    public void testWithdrawFailure() {
        createAccountsForTransferTests();
        AccountBean.depositCash(reciever, 1000);

        // Try to withdraw a negative number
        status = AccountBean.withdrawCash(reciever, -100);
        Assert.assertFalse(status);
        Assert.assertEquals(1000, AccountBean.balance(reciever));

        // Try to withdraw too much
        status = AccountBean.withdrawCash(reciever, 2000);
        Assert.assertFalse(status);
        Assert.assertEquals(1000, AccountBean.balance(reciever));

        // Try to withdraw from non-existing Account
        status = AccountBean.withdrawCash(3L, 2000);
        Assert.assertFalse(status);
    }
}
