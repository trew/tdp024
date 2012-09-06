/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package se.liu.tdp024.logic.test;

import org.junit.*;
import com.google.gson.*;
import java.util.List;
import se.liu.tdp024.entity.Account;
import se.liu.tdp024.facade.AccountFacade;
import se.liu.tdp024.logic.util.HTTPHelper;
import se.liu.tdp024.logic.bean.AccountBean;
import se.liu.tdp024.util.EMF;
import se.liu.tdp024.util.Monlog;


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
        Monlog.loggingOn = false;

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
            Monlog.loggingOn = true;
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
        JsonParser jp = new JsonParser();

        // Make sure account was created
        Assert.assertTrue(AccountBean.create(Account.SALARY, ExistingPersonKey, ExistingBankKey) != null);

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
    public void testDeposit() {
        //create an account
        long accountNumber = AccountBean.create(Account.SALARY, ExistingPersonKey, ExistingBankKey).getAccountNumber();
        Assert.assertTrue(accountNumber != 0); //make sure account was created

        // make sure 100 can be deposited
        long balance = AccountBean.balance(accountNumber);
        boolean status = AccountBean.depositCash(accountNumber, 100);
        Assert.assertTrue(status);
        Assert.assertEquals(100, AccountBean.balance(accountNumber));

        // make sure value can't exceed Long.MAX_VALUE
        // value shouldn't change
        status = AccountBean.depositCash(accountNumber, Long.MAX_VALUE);
        Assert.assertFalse(status);
        Assert.assertEquals(100, AccountBean.balance(accountNumber));
    }

    @Test
    public void testWithdraw() {
        //create an account and deposit 1000
        long accountNumber = AccountBean.create(Account.SAVINGS, ExistingPersonKey, ExistingBankKey).getAccountNumber();
        Assert.assertTrue(accountNumber != 0); //make sure account was created
        boolean status = AccountBean.depositCash(accountNumber, 1000);
        long balance = AccountBean.balance(accountNumber);
        Assert.assertEquals(1000, balance);

        // make sure we can withdraw 400
        status = AccountBean.withdrawCash(accountNumber, 400);
        Assert.assertTrue(status);
        Assert.assertEquals(600, AccountBean.balance(accountNumber));

        // make sure value can't withdraw below 0
        // value shouldn't change
        status = AccountBean.withdrawCash(accountNumber, 601);
        Assert.assertFalse(status);
        Assert.assertEquals(600, AccountBean.balance(accountNumber));
    }

    @Test
    public void testTransfer() {
        long senderAcc = AccountBean.create(Account.SALARY, ExistingPersonKey, ExistingBankKey).getAccountNumber();
        long recieverAcc = AccountBean.create(Account.SAVINGS, ExistingPersonKey, ExistingBankKey).getAccountNumber();

        AccountBean.depositCash(senderAcc, 1000);
        Assert.assertEquals(1000, AccountBean.balance(senderAcc));

        Assert.assertTrue(AccountBean.transfer(senderAcc, recieverAcc, 400));
        Assert.assertEquals(600, AccountBean.balance(senderAcc));
        Assert.assertEquals(400,AccountBean.balance(recieverAcc));
    }

    @Test
    public void testGetAccount() {
        //create an account
        long accountNumber = AccountBean.create(Account.SAVINGS, ExistingPersonKey, ExistingBankKey).getAccountNumber();
        Assert.assertTrue(accountNumber != 0); //make sure account was created

        // try retrieving the account
        Account acc = AccountBean.getAccount(accountNumber);
        Assert.assertNotNull(acc);
        Assert.assertEquals(Account.SAVINGS, acc.getAccountType());

        // try retrieving an account that doesn't exist
        Assert.assertNull(AccountBean.getAccount(123));
    }

    @Test
    public void testFindByBankKey() {
        AccountBean.create(Account.SALARY, ExistingPersonKey, ExistingBankKey);
        AccountBean.create(Account.SALARY, ExistingPersonKey, ExistingBankKey);
        AccountBean.create(Account.SAVINGS, ExistingPersonKey, ExistingBankKey);
        AccountBean.create(Account.SAVINGS, ExistingPersonKey, ExistingBankKey);

        List<Account> accounts = AccountBean.findByBankKey(ExistingBankKey);

        Assert.assertEquals(4, accounts.size());

        // Make sure an empty list returned if querying for not
        // existing bank
        List<Account> emptyAccounts = AccountBean.findByBankKey(nonExistingBankKey);
        Assert.assertEquals(0, emptyAccounts.size());
    }

    @Test
    public void testFindByPersonKey() {
        AccountBean.create(Account.SALARY, ExistingPersonKey, ExistingBankKey);
        AccountBean.create(Account.SALARY, ExistingPersonKey, ExistingBankKey);
        AccountBean.create(Account.SAVINGS, ExistingPersonKey, ExistingBankKey);
        AccountBean.create(Account.SAVINGS, ExistingPersonKey, ExistingBankKey);

        List<Account> accounts = AccountBean.findByPersonKey(ExistingPersonKey);

        Assert.assertEquals(4, accounts.size());

        // Make sure an empty list returned if querying for not
        // existing person
        List<Account> emptyAccounts = AccountBean.findByPersonKey(nonExistingPersonKey);
        Assert.assertEquals(0, emptyAccounts.size());
    }
}
