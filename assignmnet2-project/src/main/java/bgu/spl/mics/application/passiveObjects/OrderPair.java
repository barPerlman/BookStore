package bgu.spl.mics.application.passiveObjects;

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
