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
}
