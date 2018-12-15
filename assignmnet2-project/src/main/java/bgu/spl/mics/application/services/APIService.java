package bgu.spl.mics.application.services;

import bgu.spl.mics.Future;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.BookOrderEvent;
import bgu.spl.mics.application.messages.CurrTickEvent;
import bgu.spl.mics.application.messages.TerminateBroadcast;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.passiveObjects.*;

import java.util.Comparator;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * APIService is in charge of the connection between a client and the store.
 * It informs the store about desired purchases using {@link BookOrderEvent}.
 * This class may not hold references for objects which it is not responsible for:
 * {@link ResourcesHolder}, {@link MoneyRegister}, {@link Inventory}.
 * 
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class APIService extends MicroService{

	List<OrderPair> orderSchedule;
	Customer customer;

	public APIService(Customer customer,List<OrderPair> orderSchedule) {
		super(customer.getName());
		this.customer=customer;
		this.orderSchedule=orderSchedule;
		this.orderSchedule.sort(Comparator.comparing(OrderPair::getStartTick));
	}

	/**
	 * A protected function that initializes the APIService.
	 */
	@Override
	protected void initialize() {
		// when TerminateBroadcast is received then the APIService should be terminated
		subscribeBroadcast(TerminateBroadcast.class, terminateBroadcast -> {
			this.terminate();
		});

		// when TickBroadcast is received then the APIService should complete all the matching orders
		subscribeBroadcast(TickBroadcast.class,tickBroadcast -> {
			for(int i=0;i<orderSchedule.size(); i++){
				// if the current book should be ordered in this current tick time
				if(tickBroadcast.getCurrTick()==orderSchedule.get(i).getStartTick()) {
					Future<Integer> orderTick = sendEvent(new CurrTickEvent());
					Integer orderTickTime = orderTick.get(1, TimeUnit.MILLISECONDS);
					Future<OrderReceipt> future = sendEvent(new BookOrderEvent(orderSchedule.get(i).getNameBook(),this.customer,orderTickTime));

					// checking if the order was made
					if(future.get(1, TimeUnit.MILLISECONDS)!=null){
						OrderReceipt orderReceipt = new OrderReceipt(future.get());
						this.customer.addReceipt(orderReceipt);
						//System.out.println("receipt added for customer");
					}
				}
				// if startTime of the current order bigger than the current time then stop the loop
				//else if(orderSchedule.get(i).getStartTick()>tickBroadcast.getCurrTick())
					//break;
			}
		});
		//System.out.println("API Service: "+this.getName()+" is initialized");
	}
}
