package bgu.spl.mics.application.services;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.BookOrderEvent;
import bgu.spl.mics.application.messages.CheckBookEvent;
import bgu.spl.mics.application.messages.TerminateBroadcast;
import bgu.spl.mics.application.passiveObjects.Inventory;
import bgu.spl.mics.application.passiveObjects.OrderResult;
import bgu.spl.mics.application.passiveObjects.ResourcesHolder;
import bgu.spl.mics.application.passiveObjects.MoneyRegister;
import com.sun.org.apache.xpath.internal.operations.Or;

/**
 * InventoryService is in charge of the book inventory and stock.
 * Holds a reference to the {@link Inventory} singleton of the store.
 * This class may not hold references for objects which it is not responsible for:
 * {@link ResourcesHolder, {@link MoneyRegister}.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */

public class InventoryService extends MicroService{
	private Inventory inventory;

	public InventoryService(String name) {
		super(name);
		this.inventory=Inventory.getInstance();
	}
	/**
	 * This method initializes the sellingService.
	 */
	protected void initialize() {
		terminateService();
		takeBook();
		System.out.println("Inventory service: "+this.getName()+" is initialized");
	}

	/**
	 * This method makes sure that the InventoryService terminates itself
	 * when StopTickBroadcast is received.
	 */
	private void terminateService(){
		this.subscribeBroadcast(TerminateBroadcast.class, terminateTick->{
			this.terminate();
		});
	}

	private void takeBook(){
		this.subscribeEvent(CheckBookEvent.class, details -> {
			int price = this.inventory.checkAvailabiltyAndGetPrice(details.getBookName());
			if (price!=-1 && price <= details.getAvailableCreditAmount()){
				OrderResult orderResult = this.inventory.take(details.getBookName());
				if (orderResult == OrderResult.NOT_IN_STOCK){
					System.out.println("The book: "+details.getBookName()+" is not in stock");
				}
				else
					complete(details, price);
			}
			else
				complete(details, null);
		});
	}

}