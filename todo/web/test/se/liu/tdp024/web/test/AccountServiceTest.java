package se.liu.tdp024.web.test;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import javax.ws.rs.core.Response;
import org.junit.*;
import se.liu.tdp024.entity.Account;
import se.liu.tdp024.logic.bean.AccountBean;
import se.liu.tdp024.logic.util.HTTPHelper;
import se.liu.tdp024.util.EMF;
import se.liu.tdp024.web.service.AccountService;

public class AccountServiceTest {
    private static String PersonAPI_URL = "http://enterprise-systems.appspot.com/person/";
    private static String BankAPI_URL =   "http://enterprise-systems.appspot.com/bank/";

    private static String ExistingBankKey = "";
    private static String ExistingPersonKey = "";

    public AccountServiceTest() {
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
    }

    @Test
    public void testCreateFailure() {
    }

    @Test
    public void testListByPersonID() {
    }

    @Test
    public void testListByBankID() {
    }

    @Test
    public void testListByPersonIDFailure() {
    }

    @Test
    public void testListByBankIDFailure() {
    }

    @Test
    public void testWithdraw() {
        AccountService as = new AccountService();
        long testAccount = AccountBean.create(Account.SALARY,
                ExistingPersonKey, ExistingBankKey);

        // Withdraw non-existing money
        Response response = as.withdraw(testAccount, 100);
        Assert.assertEquals(500, response.getStatus());

        // Deposit cash for testing purposes and try a valid withdraw
        Assert.assertTrue(AccountBean.depositCash(testAccount, 200));

        // Verify money transfered
        Assert.assertEquals(200, AccountBean.balance(testAccount));

        response = as.withdraw(testAccount, 80);
        Assert.assertEquals(200, response.getStatus());

        // Try to withdraw negative amount
        response = as.withdraw(testAccount, -50);
        Assert.assertEquals(500,response.getStatus());

        // Try to withdraw from nonexisting account.
        response = as.withdraw(999, 100);
        Assert.assertEquals(500,response.getStatus());
    }

    @Test
    public void testDeposit() {
        // Set up test-account
        AccountService as = new AccountService();
        long testAccount = AccountBean.create(Account.SALARY,
                ExistingPersonKey, ExistingBankKey);

        // Test valid deposit
        Response response = as.deposit(testAccount, 100);
        Assert.assertEquals(200, response.getStatus());

        // Test invalid amount (amount to large)
        response = as.deposit(testAccount, Long.MAX_VALUE);
        Assert.assertEquals(500, response.getStatus());

        // Test invalid amount (negative)
        response = as.deposit(testAccount, -50);
        Assert.assertEquals(500, response.getStatus());

        // Deposit
        response = as.deposit(999, 100);
        Assert.assertEquals(500, response.getStatus());
    }

    @Test
    public void testTransfer() {
        // Set up test-accounts
        AccountService as = new AccountService();
        long recieverAcc = AccountBean.create(Account.SALARY,
                ExistingPersonKey, ExistingBankKey);

        long senderAcc = AccountBean.create(Account.SAVINGS,
                ExistingPersonKey, ExistingBankKey);

        Response response = as.deposit(senderAcc, 100);
        Assert.assertEquals(200, response.getStatus());

        response = as.transfer(senderAcc, recieverAcc, 20);
        Assert.assertEquals(200, response.getStatus());

        // Verify money transfered
        Assert.assertEquals(20, AccountBean.balance(recieverAcc));

        // Test invalid transfer (not enough money)
        response = as.transfer(senderAcc, recieverAcc, 200);
        Assert.assertEquals(500, response.getStatus());

        // Test invalid transfer (negative amount)
        response = as.transfer(senderAcc, recieverAcc, -20);
        Assert.assertEquals(500, response.getStatus());

        // Test invalid transfer (unknown sender)
        response = as.transfer(999, recieverAcc, 20);
        Assert.assertEquals(500, response.getStatus());

        // Test invalid transfer (unknown reciever)
        response = as.transfer(senderAcc, 999, 20);
        Assert.assertEquals(500, response.getStatus());
    }


}
