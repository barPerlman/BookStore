package bgu.spl.mics.application.messages;

import bgu.spl.mics.Broadcast;
import bgu.spl.mics.Event;

public class CurrTickEvent implements Event<Integer> {

    private int currTick;

    public CurrTickEvent(int currTick){
        this.currTick=currTick;
    }

    public CurrTickEvent(){
    }

    public int getCurrTick() {
        return currTick;
    }
}
