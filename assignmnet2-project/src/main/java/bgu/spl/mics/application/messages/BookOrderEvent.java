package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;
import bgu.spl.mics.application.passiveObjects.Customer;

public class BookOrderEvent implements Event {

    String bookName;
    Customer customer;
    int orderTime;

    public BookOrderEvent(String bookName,Customer customer,int orderTime)
    {
        this.customer=customer;
        this.bookName=bookName;
        this.orderTime=orderTime;
    }

    public Customer getCustomer() {
        return customer;
    }

    public int getOrderTime() {
        return orderTime;
    }

    public String getBookName() {
        return bookName;
    }
}
