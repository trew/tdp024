package se.liu.tdp024.logic.test;

import org.junit.*;
import se.liu.tdp024.entity.Todo;
import se.liu.tdp024.logic.bean.TodoBean;
import se.liu.tdp024.logic.util.HTTPHelper;
import se.liu.tdp024.util.EMF;

public class TodoBeanTest {

    public TodoBeanTest() {
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

        Todo todo = TodoBean.create("First Title", "First Content");
        Assert.assertNotNull(todo);
    }

    
    @Test
    public void testOpenClose() {

        Todo todo = TodoBean.create("First Title", "First Content");
        Assert.assertNotNull(todo);
        Assert.assertEquals(false, todo.isOpen());

        TodoBean.open(todo.getId());

        todo = TodoBean.find(todo.getId());
        Assert.assertNotNull(todo);
        Assert.assertEquals(true, todo.isOpen());

        TodoBean.close(todo.getId());

        todo = TodoBean.find(todo.getId());
        Assert.assertNotNull(todo);
        Assert.assertEquals(false, todo.isOpen());


    }
}
