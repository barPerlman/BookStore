package bgu.spl.mics.application.messages;

import bgu.spl.mics.Broadcast;

public class TerminateBroadcast implements Broadcast {
    private int currTick;

    public TerminateBroadcast(int currTick){
        this.currTick=currTick;
    }

    public int getCurrTick(){
        return currTick;
    }
}
