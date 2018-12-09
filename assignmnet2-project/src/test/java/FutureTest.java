
import bgu.spl.mics.application.passiveObjects.BookInventoryInfo;
import bgu.spl.mics.application.passiveObjects.DeliveryVehicle;
import bgu.spl.mics.application.passiveObjects.OrderReceipt;
import bgu.spl.mics.Future;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;

public class FutureTest {

    //those are the Future actual types might be created:
    Future<BookInventoryInfo> bookInventoryInfo_future;
    Future<OrderReceipt> orderReceipt_future;
    Future<DeliveryVehicle> deliveryVehicle_future;
    @Before
    public void setUp() throws Exception {

           //creation of the Objects Under Test - types of objects behind T
           bookInventoryInfo_future=new Future<BookInventoryInfo>();
           orderReceipt_future=new Future<OrderReceipt>();
           deliveryVehicle_future=new Future<DeliveryVehicle>();

    }

    @After
    public void tearDown() throws Exception {
    }

    /**
     * get method
     * here we check we get the proper object type behind T
     * and verify this is a block method
     */
    @Test
    public void get() {

/*
** the following test is in comment because if the method works as required it will cause to a liveness problem
* cause the get method can't return with null. in case it's not resolved the method waits for resolve completed
* and it doesn't happen here
* In case there is a problem and it doesn't wait, the test will fail
* so it's not testable:
*
*
        //the following check that this is a check method
        assertNotNull(bookInventoryInfo_future.get());
        assertNotNull(orderReceipt_future.get());
        assertNotNull(deliveryVehicle_future.get());
*/


       //now resolve the Future objects so we could check the proper instances are received
        bookInventoryInfo_future.resolve(new BookInventoryInfo("Harry Poter 2",2,90));
        assertEquals("bgu.spl.mics.application.passiveObjects.BookInventoryInfo",bookInventoryInfo_future.get().getClass().getName());
        orderReceipt_future.resolve(new OrderReceipt(1,"s",1,"s",1,1,1,1));
        assertEquals("bgu.spl.mics.application.passiveObjects.OrderReceipt",orderReceipt_future.get().getClass().getName());
        deliveryVehicle_future.resolve(new DeliveryVehicle(1,1));   //resolve custom delivery car for test
        assertEquals("bgu.spl.mics.application.passiveObjects.DeliveryVehicle",deliveryVehicle_future.get().getClass().getName());


    }

    /**
     * check the assign of result into the Future object
     *
     */
    @Test
    public void resolve() {
        //init clean objects under test
        bookInventoryInfo_future=new Future<BookInventoryInfo>();
        orderReceipt_future=new Future<OrderReceipt>();
        deliveryVehicle_future=new Future<DeliveryVehicle>();

        //checks resolve of type book inventory info result
        bookInventoryInfo_future.resolve(new BookInventoryInfo("Harry Poter 2",2,90));
        assertEquals("bgu.spl.mics.application.passiveObjects.BookInventoryInfo",bookInventoryInfo_future.get().getClass().getName());

        //checks resolve of type order receipt result
        orderReceipt_future.resolve(new OrderReceipt(1,"s",1,"s",1,1,1,1));
        assertEquals("bgu.spl.mics.application.passiveObjects.OrderReceipt",orderReceipt_future.get().getClass().getName());

        //checks resolve of type delivery vehicle result
        deliveryVehicle_future.resolve(new DeliveryVehicle(1,1));
        assertEquals("bgu.spl.mics.application.passiveObjects.DeliveryVehicle",deliveryVehicle_future.get().getClass().getName());
    }

    /**
     * check that the received result status reflects the actual status
     *here we check only for one type as a sample
     */
    @Test
    public void isDone() {
        //init clean OUT
        bookInventoryInfo_future=new Future<BookInventoryInfo>();
        orderReceipt_future=new Future<OrderReceipt>();
        deliveryVehicle_future=new Future<DeliveryVehicle>();

        //here we check the status when its not resolved
        assertFalse("isDone expected:false",bookInventoryInfo_future.isDone());

        //here we check the status when its resolved
        bookInventoryInfo_future.resolve(new BookInventoryInfo("Harry Potter 2",2,90));
        assertTrue("isDone expected: true",bookInventoryInfo_future.isDone());
    }

    /**
     * checks the following:
     * 1.the method get an instance of the Future class in case it is resolved before end session of get1
     * 2.the method returns null in case of end time session before the event was resolved.
     *
     */
    @Test
    public void get1() {

        //the following test is checked with delivery vehicle instance only
        //sample test to check the method can return null as get

        //beginning of the method so the OUT will be clean and ready for the new tests
        deliveryVehicle_future=new Future<DeliveryVehicle>();

        assertNull("should be null since the object didn't resolve on time",deliveryVehicle_future.get(1, TimeUnit.SECONDS));
        //resolve the object in aim to test positively get of the proper class
        deliveryVehicle_future.resolve(new DeliveryVehicle(1,1));
        assertEquals("bgu.spl.mics.application.passiveObjects.DeliveryVehicle",deliveryVehicle_future.get(1,TimeUnit.MICROSECONDS).getClass().getName());

    }
}