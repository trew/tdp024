package se.liu.tdp024.web.test;

import javax.ws.rs.core.Response;
import org.junit.*;
import se.liu.tdp024.util.EMF;
import se.liu.tdp024.web.service.AccountService;

public class AccountServiceTest {
    public AccountServiceTest() {
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
