package bgu.spl.mics.application.passiveObjects;

/**
 * A passiveObject that holds the name of the book that should be ordered in the startTick time
 */
public class OrderPair {

    String nameBook;
    Integer startTick;

    public OrderPair(String nameBook, Integer startTick){
        this.nameBook=nameBook;
        this.startTick=startTick;
    }

    public int getStartTick() {
        return startTick;
    }

    public String getNameBook(){
        return nameBook;
    }
}
