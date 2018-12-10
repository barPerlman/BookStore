package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;

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
