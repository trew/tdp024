package se.liu.tdp024.web.test;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.List;
import javax.ws.rs.core.Response;
import org.junit.*;
import se.liu.tdp024.entity.Account;
import se.liu.tdp024.logic.bean.AccountBean;
import se.liu.tdp024.util.EMF;
import se.liu.tdp024.util.HTTPHelper;
import se.liu.tdp024.util.Monlog;
import se.liu.tdp024.web.service.AccountService;

public class AccountServiceTest {
    private static String ExistingBankKey = "";
    private static String ExistingPersonKey = "";
    AccountService accountService;
    Response response;

    public AccountServiceTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
        Monlog.setLoggingOff();

        String personApiUrl = "http://enterprise-systems.appspot.com/person/";
        String bankApiUrl =   "http://enterprise-systems.appspot.com/bank/";

        JsonParser jp = new JsonParser();
        JsonObject jo;
        JsonElement je;

        // Get an existing Person for testing
        String resp = HTTPHelper.get(personApiUrl + "find.name", "name", "Zorro");
        Assert.assertFalse("Couldn't connect to PersonAPI", resp == null);
        je = jp.parse(resp); Assert.assertTrue(je.isJsonObject());
        jo = je.getAsJsonObject();
        Assert.assertTrue("Person \"Zorro\" not found", jo.isJsonObject());
        ExistingPersonKey = jo.get("key").getAsString();

        // Get an existing Bank for testing
        resp = HTTPHelper.get(bankApiUrl + "find.name", "name", "SWEDBANK");
        Assert.assertFalse("Couldn't connect to BankAPI", resp == null);
        je = jp.parse(resp); Assert.assertTrue(je.isJsonObject());
        jo = je.getAsJsonObject();
        Assert.assertTrue("Bank \"SWEDBANK\" not found", jo.isJsonObject());
        ExistingBankKey = jo.get("key").getAsString();
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
        Monlog.setLoggingOn();
    }

    @Before
    public void setUp() {
        accountService = new AccountService();
    }

    @After
    public void tearDown() {
        EMF.close();
    }

    @Test
    public void testCreate() {
        response = accountService.createAccount(ExistingPersonKey,
                                                  ExistingBankKey,
                                                  Account.SALARY);
        Assert.assertEquals(200, response.getStatus());
        Object entity = response.getEntity();
        Account account = new Gson().fromJson((String)entity, Account.class);
        Assert.assertEquals(ExistingPersonKey, account.getPersonKey());
    }

    @Test
    public void testCreateFailure() {
        // Bad account type
        response = accountService.createAccount(ExistingPersonKey, ExistingBankKey, 5);
        Assert.assertEquals(500, response.getStatus());

        response = accountService.createAccount("", "", Account.SALARY);
        Assert.assertEquals(500, response.getStatus());

        response = accountService.createAccount(null, ExistingBankKey, Account.SALARY);
        Assert.assertEquals(500, response.getStatus());

        response = accountService.createAccount(ExistingPersonKey, null, Account.SALARY);
        Assert.assertEquals(500, response.getStatus());

        response = accountService.createAccount(ExistingPersonKey, ExistingBankKey, null);
        Assert.assertEquals(500, response.getStatus());
    }

    @Test
    public void testListByPersonID() {
        // Make sure response is valid even if person doesn't have any
        // accounts yet.
        response = accountService.listByPersonKey(ExistingPersonKey);
        Assert.assertEquals(200, response.getStatus());

        // Create two accounts
        AccountBean.create(Account.SALARY, ExistingPersonKey, ExistingBankKey);
        AccountBean.create(Account.SALARY, ExistingPersonKey, ExistingBankKey);

        response = accountService.listByPersonKey(ExistingPersonKey);
        Assert.assertEquals(200, response.getStatus());

        Object entity = response.getEntity();
        Type listType = new TypeToken<List<Account>>(){}.getType();
        List<Account> accounts = new Gson().fromJson((String)entity, listType);
        Assert.assertEquals(2, accounts.size());
    }

    @Test
    public void testListByPersonIDFailure() {
        // Missing args
        response = accountService.listByPersonKey(null);
        Assert.assertEquals(500, response.getStatus());

        // Invalid personKey
        response = accountService.listByPersonKey("nonExisting");
        Assert.assertEquals(500, response.getStatus());
    }

    @Test
    public void testListByBankID() {
        // Make sure response is valid even if bank doesn't have any
        // accounts yet.
        response = accountService.listByBankKey(ExistingPersonKey);
        Assert.assertEquals(200, response.getStatus());

        AccountBean.create(Account.SALARY, ExistingPersonKey, ExistingBankKey);
        AccountBean.create(Account.SALARY, ExistingPersonKey, ExistingBankKey);

        response = accountService.listByBankKey(ExistingBankKey);
        Assert.assertEquals(200, response.getStatus());

        Object entity = response.getEntity();
        Type listType = new TypeToken<List<Account>>(){}.getType();
        List<Account> accounts = new Gson().fromJson((String)entity, listType);
        Assert.assertEquals(2, accounts.size());
    }

    @Test
    public void testListByBankIDFailure() {
        // Missing args
        response = accountService.listByBankKey(null);
        Assert.assertEquals(500, response.getStatus());

        // Invalid personKey
        response = accountService.listByBankKey("nonExisting");
        Assert.assertEquals(500, response.getStatus());
    }

    /* Transfer functions */

    long sender;
    long reciever;

    private void setUpAccounts() {
        sender = AccountBean.create(Account.SALARY,
                ExistingPersonKey, ExistingBankKey).getAccountNumber();
        reciever = AccountBean.create(Account.SAVINGS,
                ExistingPersonKey, ExistingBankKey).getAccountNumber();

    }

    @Test
    public void testWithdraw() {
        setUpAccounts();
        // Withdraw non-existing money
        response = accountService.withdraw(sender, 100L);
        Assert.assertEquals(500, response.getStatus());

        // Deposit cash for testing purposes and try a valid withdraw
        Assert.assertTrue(AccountBean.depositCash(sender, 200));
        Assert.assertEquals(200, AccountBean.balance(sender));

        response = accountService.withdraw(sender, 80L);
        Assert.assertEquals(200, response.getStatus());

    }

    @Test
    public void testWithdrawFailure() {
        setUpAccounts();

        // Try to withdraw negative amount
        response = accountService.withdraw(sender, -50L);
        Assert.assertEquals(500,response.getStatus());

        // Try to withdraw from nonexisting account.
        response = accountService.withdraw(999L, 100L);
        Assert.assertEquals(500,response.getStatus());

        // Try to withdraw with invalid input values (null)
        response = accountService.withdraw(null, 100L);
        Assert.assertEquals(500,response.getStatus());

        response = accountService.withdraw(50L, null);
        Assert.assertEquals(500, response.getStatus());
    }

    @Test
    public void testDeposit() {
        setUpAccounts();

        // Test valid deposit
        response = accountService.deposit(sender, 100L);
        Assert.assertEquals(200, response.getStatus());
    }

    @Test
    public void testDepositFailure() {
        setUpAccounts();
        accountService.deposit(sender, 100L);

        // Test invalid amount (amount to large)
        response = accountService.deposit(sender, Long.MAX_VALUE);
        Assert.assertEquals(500, response.getStatus());

        // Test invalid amount (negative)
        response = accountService.deposit(sender, -50L);
        Assert.assertEquals(500, response.getStatus());

        // Try to deposit from nonexisting account.
        response = accountService.deposit(999L, 100L);
        Assert.assertEquals(500, response.getStatus());

        // Try to deposit with invalid input values (null)
        response = accountService.deposit(null, 100L);
        Assert.assertEquals(500,response.getStatus());

        response = accountService.deposit(50L, null);
        Assert.assertEquals(500, response.getStatus());
    }

    @Test
    public void testTransferToSelf() {
        // Transfer to self not possible
        setUpAccounts();
        response = accountService.transfer(sender, sender, 100L);
        Assert.assertEquals(500, response.getStatus());
    }

    @Test
    public void testTransferInvalidSender() {
        setUpAccounts();
        response = accountService.transfer(3L, reciever, 100L);
        Assert.assertEquals(500, response.getStatus());
    }

    @Test
    public void testTransferInvalidReciever() {
        setUpAccounts();
        response = accountService.transfer(sender, 3L, 100L);
        Assert.assertEquals(500, response.getStatus());
    }

    @Test
    public void testTransferNotEnoughMoneyOnSender() {
        setUpAccounts();
        Account senderAcc = AccountBean.getAccount(sender);
        Assert.assertEquals(0, senderAcc.getBalance());

        response = accountService.transfer(sender, reciever, 1L);
        Assert.assertEquals(500, response.getStatus());
    }

    @Test
    public void testTransferRecieverOverflowed() {
        setUpAccounts();
        AccountBean.depositCash(sender, Long.MAX_VALUE);
        AccountBean.depositCash(reciever, 100);

        response = accountService.transfer(sender, reciever, Long.MAX_VALUE);
        Assert.assertEquals(500, response.getStatus());
    }

    @Test
    public void testBadTransfer() {
        setUpAccounts();
        AccountBean.depositCash(sender, 100);

        response = accountService.transfer(null, reciever, 100L);
        Assert.assertEquals(500, response.getStatus());

        response = accountService.transfer(sender, null, 100L);
        Assert.assertEquals(500, response.getStatus());

        response = accountService.transfer(sender, reciever, null);
        Assert.assertEquals(500, response.getStatus());
    }

    @Test
    public void testTransfer() {
        setUpAccounts();
        AccountBean.depositCash(sender, 1000); // deposit 1000 to sender account
        Assert.assertEquals(1000, AccountBean.balance(sender));
        Assert.assertEquals(0, AccountBean.balance(reciever));

        response = accountService.transfer(sender, reciever, 300L);
        Assert.assertEquals(200, response.getStatus());
        Assert.assertEquals(700, AccountBean.balance(sender));
        Assert.assertEquals(300, AccountBean.balance(reciever));
    }
}
