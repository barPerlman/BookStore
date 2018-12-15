package bgu.spl.mics.application.messages;
import bgu.spl.mics.Event;
import bgu.spl.mics.application.passiveObjects.Customer;

/**
 * An event that is sent when a client of the store wishes to buy a book.
 */
public class BookOrderEvent implements Event {

    private String bookName;
    private Customer customer;

    private int tickTime;

    public BookOrderEvent(String bookName, Customer customer, int tickTime){
        this.bookName = bookName;
        this.customer = customer;
        this.tickTime = tickTime;

    }

    public String getBookName() {
        return this.bookName;
    }

    public Customer getCustomer(){
        return this.customer;
    }

    public int getOrderTickTime() {

        return this.tickTime;

    }
}