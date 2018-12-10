package bgu.spl.mics.application.messages;
import bgu.spl.mics.Event;
import bgu.spl.mics.application.passiveObjects.Customer;

public class BookOrderEvent implements Event {

    private String bookName;
    private Customer customer;
    private int orderTickTime;

    public BookOrderEvent(String bookName, Customer customer, int orderTickTime){
        this.bookName = bookName;
        this.customer = customer;
        this.orderTickTime = orderTickTime;
    }

    public String getBookName() {
        return this.bookName;
    }

    public Customer getCustomer(){
        return this.customer;
    }

    public int getOrderTickTime() {
        return this.orderTickTime;
    }
}