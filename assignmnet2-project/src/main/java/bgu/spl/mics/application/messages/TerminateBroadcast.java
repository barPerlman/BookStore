package bgu.spl.mics.application.messages;

import bgu.spl.mics.Broadcast;

/**
 * An broadcast that is sent when the timeService reaches its last tick
 */
public class TerminateBroadcast implements Broadcast {
    private int currTick;

    public TerminateBroadcast(int currTick){
        this.currTick=currTick;
    }

    public int getCurrTick(){
        return currTick;
    }
}
