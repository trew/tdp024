/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package se.liu.tdp024.logic.test;

import org.junit.*;
import com.google.gson.*;
import se.liu.tdp024.entity.Account;
import se.liu.tdp024.logic.util.HTTPHelper;
import se.liu.tdp024.logic.bean.AccountBean;


public class AccountBeanTest {

    public AccountBeanTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {

    }

    @Test
    public void testCreate() {


        String responseP = HTTPHelper.get("http://enterprise-systems.appspot.com/person/find.name", "name", "Zorro");
        Assert.assertFalse("Testperson not available, cannot do test", responseP==null);

        JsonParser jp = new JsonParser();
        JsonElement jeP = jp.parse(responseP);
        JsonObject joP = jeP.getAsJsonObject();
        String personKey = joP.get("key").getAsString();

        String responseB = HTTPHelper.get("http://enterprise-systems.appspot.com/bank/find.name", "name", "SWEDBANK");
        Assert.assertFalse("Testbank not available, cannot do test", responseB==null);

        JsonElement jeB = jp.parse(responseB);
        JsonObject joB = jeB.getAsJsonObject();
        String bankKey = joB.get("key").getAsString();

        Assert.assertTrue(AccountBean.create(Account.SALARY, personKey, bankKey) != 0);


    }

    @Test
    public void testDeposit() {

    }

    @Test
    public void testWithdraw() {

    }

    @Test
    public void testGetAccount() {

    }

    @Test
    public void testFindByBankKey() {

    }

    @Test
    public void testFindByPersonKey() {

    }



}
