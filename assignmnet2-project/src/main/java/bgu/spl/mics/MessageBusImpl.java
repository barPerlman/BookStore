package bgu.spl.mics;

import bgu.spl.mics.application.passiveObjects.Inventory;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicReference;

import bgu.spl.mics.MicroService;

/**
 * The {@link MessageBusImpl class is the implementation of the MessageBus interface.
 * Write your implementation here!
 * Only private fields and methods can be added to this class.
 */
public class MessageBusImpl implements MessageBus {

    //@INV: the initialization of the singleton successfully completed

    //hash table with message type key to point on queue of subscribed microServices
    private ConcurrentHashMap<Class<? extends Message>, ConcurrentLinkedQueue<MicroService>> _messageTypeHT;
    //hash table with micro service type key to point on queue of micro Services's aimed messages
    private ConcurrentHashMap<MicroService, ConcurrentLinkedQueue<Class<? extends Message>>> _microServiceTypeHT;
    //hash table which associates between event to its Future object
    private ConcurrentHashMap<Class<? extends Event>, Future> _eventToFuture;
    //lock object
    private Object _lockEvent;
    private Object _lockBrod;


    /**
     * the followings are methods for implementation of the thread safe singleton
     * the method used for the thread safe which is called  "Initialization-on-demand holder (design pattern) idiom"
     * this thread safety is guaranteed by the jvm initialization classes
     */

    //private constructor
    private MessageBusImpl() {
        //init the hashTables
        _messageTypeHT = new ConcurrentHashMap<Class<? extends Message>, ConcurrentLinkedQueue<MicroService>>();
        _microServiceTypeHT = new ConcurrentHashMap<MicroService, ConcurrentLinkedQueue<Class<? extends Message>>>();
        _eventToFuture = new ConcurrentHashMap<Class<? extends Event>, Future>();
        _lockEvent = new Object();
        _lockBrod = new Object();
    }

    private static class Holder {
        private static final MessageBusImpl INSTANCE = new MessageBusImpl();
    }


    //Retrieves the single instance of this class.
    public static MessageBusImpl getInstance() {
        return Holder.INSTANCE;
    }

    //the following are the interface methods
    //@PRE:
    //@POST:
    @Override
    public <T> void subscribeEvent(Class<? extends Event<T>> type, MicroService m) {


        //get the relevant queue to subscribe (insert the micro service as subscriber)
        synchronized (_lockEvent) {
            //init the queue in case the type has no subscribers
            subscribeMicroService(type, m);

        }

    }

    @Override
    public void subscribeBroadcast(Class<? extends Broadcast> type, MicroService m) {
        synchronized (_lockBrod) {
            //init the queue in case the type has no subscribers
            subscribeMicroService(type, m);

        }
    }

    @Override
    public <T> void complete(Event<T> e, T result) {
        // TODO
        //resolve the result that is associated with the received event
    }

    @Override
    public void sendBroadcast(Broadcast b) {
       // synchronized ()
        ConcurrentLinkedQueue<MicroService> q = _messageTypeHT.get(b);
        for(MicroService m:q){
            _microServiceTypeHT.get(m).add(b.getClass());   //insert the brodcast message to all subscribers
        }

    }


    @Override
    public <T> Future<T> sendEvent(Event<T> e) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void register(MicroService m) {

    }


    ///////////////////////////////not finished!! verify keys insertion to table////////////////////////////////////
    @Override
    public void unregister(MicroService m) {

    }

    @Override
    public Message awaitMessage(MicroService m) throws InterruptedException {
        // TODO Auto-generated method stub
        return null;
    }

    private void subscribeMicroService(Class<? extends Message> type, MicroService m) {
        _messageTypeHT.putIfAbsent(type, new ConcurrentLinkedQueue<MicroService>());
        _messageTypeHT.get(type).add(m);    //add m as subscriber to the type of message
    }

}
