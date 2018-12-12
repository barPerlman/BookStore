package bgu.spl.mics;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * The {@link MessageBusImpl class is the implementation of the MessageBus interface.
 * Write your implementation here!
 * Only private fields and methods can be added to this class.
 */
public class MessageBusImpl implements MessageBus {

	//@INV: the initialization of the singleton successfully completed

	//hash table with message type key to point on queue of subscribed microServices
	private ConcurrentHashMap<Class<? extends Message>, LinkedBlockingQueue<MicroService>> _messageTypeHT;
	//hash table with micro service type key to point on queue of micro Services's aimed messages
	private ConcurrentHashMap<MicroService, LinkedBlockingQueue<Message>> _microServiceTypeHT;
	//hash table which associates between event to its Future object
	private ConcurrentHashMap<Event, Future > _eventToFuture;

	private ConcurrentHashMap<MicroService, LinkedBlockingQueue<Future>> _microServiceToFuture;

	private Object _lockerForFuturePairs;

	/**
	 * the followings are methods for implementation of the thread safe singleton
	 * the method used for the thread safe which is called  "Initialization-on-demand holder (design pattern) idiom"
	 * this thread safety is guaranteed by the jvm initialization classes
	 */

	//private constructor
	private MessageBusImpl() {
		//init the hashTables
		_messageTypeHT = new ConcurrentHashMap<>();
		_microServiceTypeHT = new ConcurrentHashMap<>();
		_eventToFuture = new ConcurrentHashMap<>();
		_microServiceToFuture = new ConcurrentHashMap<>();
		_lockerForFuturePairs=new Object();
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
		_messageTypeHT.putIfAbsent(type, new LinkedBlockingQueue<MicroService>());	//verify there will be object to lock on
		synchronized(_messageTypeHT.get(type.getClass())) {	//lock the relevant queue for the micro service to subscribe only
			//init the queue in case the type has no subscribers
			subscribeMicroService(type, m);	//subscribe the micro service to the event type

		}

	}

	@Override
	public void subscribeBroadcast(Class<? extends Broadcast> type, MicroService m) {
		_messageTypeHT.putIfAbsent(type, new LinkedBlockingQueue<MicroService>());	//verify there will be object to lock on
		synchronized (_messageTypeHT.get(type.getClass())) {	//lock the relevant queue for the micro service to subscribe only
			//init the queue in case the type has no subscribers
			subscribeMicroService(type, m);	//subscribe the micro service to the Broadcast Type

		}
	}

	@Override
	public <T> void complete(Event<T> e, T result) {

		synchronized (_lockerForFuturePairs) {
			//resolve the future associated to the event e
			_eventToFuture.get(e).resolve(result);    //resolve the associated future with the result
			//here we didn't remove the event and its future from the table although this data unnacassary anymore cause delete in a not bi-directional list cost O(n)
		}
	}

	@Override
	public void sendBroadcast(Broadcast b) {
		//get the queue with the micro services which are subscribed to b.getClass()
		LinkedBlockingQueue<MicroService> microsSubscribedTo_b = _messageTypeHT.get(b.getClass());

		//add the b message to the messages list of each micro service which is subscribed to b
		for(MicroService currMicro:microsSubscribedTo_b){
			_microServiceTypeHT.get(currMicro).add(b);   //insert the broadcast message to all subscribers
		}

	}


	@Override
	public <T> Future<T> sendEvent(Event<T> e) {
		if(_messageTypeHT.get(e.getClass())==null){		//there's no micro service which is subscribed to handle this event
			return null;
		}
		else{	//there's at least 1 service which is subscribed to this event

			//get the subscribers of the e event
			LinkedBlockingQueue<MicroService> subscribersTo_e=_messageTypeHT.get(e.getClass());
			MicroService nextMSToGet_e=subscribersTo_e.poll();	//get the head of the queue and remove it from list
			Future<T> future=new Future<>();	//create a future to associate with the event

			//insert the Event e with its future to the HashTable of events with their messages
			_eventToFuture.putIfAbsent(e,future);
			//the same with MS
			_microServiceToFuture.get(nextMSToGet_e).add(future);

			//put the event in the right micro service messages queue
			_microServiceTypeHT.get(nextMSToGet_e).add(e);

			//add the micro service back to the tail of the queue for round robin consistency
			subscribersTo_e.add(nextMSToGet_e);

			return future;
		}
	}

	@Override
	public void register(MicroService m) {

		_microServiceTypeHT.putIfAbsent(m,new LinkedBlockingQueue<>(null));

	}


	@Override
	public synchronized void unregister(MicroService m) {
			//resolve the micro service futures to null (not resolved)
			LinkedBlockingQueue<Future> futuresToNull= _microServiceToFuture.get(m);
			for(Future f: futuresToNull){
				f.resolve(null);
			}
			//clear from hash tables
		for(Class<? extends Message> messageType:_messageTypeHT.keySet()){
			_messageTypeHT.remove(messageType,m);
		}
		_microServiceTypeHT.remove(m);


	}

	@Override
	public Message awaitMessage(MicroService m) throws InterruptedException {

		return _microServiceTypeHT.get(m).take();

		/*
		if(_microServiceTypeHT.get(m)==null){		//in case m was never registered
			throw new IllegalStateException();
		}
		else{
			synchronized (_microServiceTypeHT.get(m)){	//lock on the messages queue of m
				while(_microServiceTypeHT.get(m).isEmpty()){	//the messages queue is empty
					try{
						_microServiceTypeHT.get(m).wait();		//wait till there will be messages
					}
					catch (InterruptedException e){
						if (Thread.currentThread().isInterrupted()) {
							throw new InterruptedException();
						}
						Thread.currentThread().interrupt();
					}
				}
				Message msgFromQueue=_microServiceTypeHT.get(m).poll().cast(Message.class);
				notifyAll();
				return msgFromQueue;
			}
		}
		*/
	}

	/**
	 * subscribe the micro service m to the queue belongs to the received message type
	 * @param type	the type of the message to subscribe to
	 * @param m		the micro service we would like to subscribe
	 */
	private void subscribeMicroService(Class<? extends Message> type, MicroService m) {
		try {
			_messageTypeHT.get(type.getClass()).put(m);    //add m as subscriber to the type of message
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}
	}

}