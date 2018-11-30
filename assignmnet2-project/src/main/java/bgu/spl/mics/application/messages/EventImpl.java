package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;
import bgu.spl.mics.Future;

public class EventImpl<T> implements Event {

    private Future<T> _futureResult;

    EventImpl(Future<T> future){
        _futureResult=future;
    }

    public Future<T> get_futureResult() {
        return _futureResult;
    }
}
