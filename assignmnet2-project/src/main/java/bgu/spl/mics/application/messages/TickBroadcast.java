package bgu.spl.mics.application.messages;

import bgu.spl.mics.Broadcast;

/**
 * A broadcast that is sent at every timeService tick
 */
public class TickBroadcast implements Broadcast {

    private int currTick;

    public TickBroadcast(int currTick){
        this.currTick=currTick;
    }

    public int getCurrTick(){
        return currTick;
    }
}
