package se.liu.tdp024.web.test;

import javax.ws.rs.core.Response;
import org.junit.*;
import se.liu.tdp024.util.EMF;
import se.liu.tdp024.web.service.TodoService;

public class TodoServiceTest {

    public TodoServiceTest() {
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

        /*
         * No need to create HTTP requests through tools like curl or HTTPHelper
         * since the service is defined as a Java class.
         *
         * HOWEVER: It might be a good idea to run tests through HTTP aswell to
         * ensure that it works.
         */
        TodoService service = new TodoService();

        Response response = service.create("Title 1", "Content 1");

        Assert.assertEquals(200, response.getStatus());

        String entity = (String) response.getEntity();

        //There might be better ways of doing this...

        String expectedString = "{\"id\":1,\"title\":\"Title 1\",\"content\":\"Content 1\",\"open\":false}";
        Assert.assertEquals(expectedString, entity);

    }
    
    public void testCreateFailure() {
        
        TodoService service = new TodoService();
        
        Response response = service.create(null, "Content 1");
        
        Assert.assertEquals(500, response.getStatus());
        
    }
}
