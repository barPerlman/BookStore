package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;

/**
 * An event that is sent when the sells men of the store wishes to
 * check if a book is in the inventory of the store
 */
public class CheckBookEvent implements Event {

    String bookName;
    int availableCreditAmount;

    public CheckBookEvent(String bookName,int availableCreditAmount){
        this.bookName=bookName;
        this.availableCreditAmount=availableCreditAmount;
    }

    public String getBookName() {
        return bookName;
    }

    public int getAvailableCreditAmount() {
        return availableCreditAmount;
    }
}
