package se.liu.tdp024.data.test;

import org.junit.*;
import se.liu.tdp024.entity.Todo;
import se.liu.tdp024.facade.TodoFacade;
import se.liu.tdp024.util.EMF;

public class TodoFacadeTest {

    public TodoFacadeTest() {
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

        long id = TodoFacade.create("First Title", "First Content");
        Assert.assertTrue("Id shouldn't be 0", id != 0);

    }

    @Test
    public void testFind() {

        long id = TodoFacade.create("First Title", "First Content");
        Assert.assertTrue("Id shouldn't be 0", id != 0);

        Todo todo = TodoFacade.find(id);

        Assert.assertNotNull("Todo shouldn't be null", todo);
        Assert.assertEquals(todo.getTitle(), "First Title");
        Assert.assertEquals(todo.getContent(), "First Content");

    }
    
    @Test
    public void testUpdateOpen() {

        long id = TodoFacade.create("First Title", "First Content");
        Assert.assertTrue("Id shouldn't be 0", id != 0);

        Todo todo = TodoFacade.find(id);

        Assert.assertNotNull("Todo shouldn't be null", todo);
        Assert.assertEquals(false, todo.isOpen());
        
        TodoFacade.updateOpen(id, true);
        todo = TodoFacade.find(id);
        Assert.assertEquals(true, todo.isOpen());
    }
    
}
