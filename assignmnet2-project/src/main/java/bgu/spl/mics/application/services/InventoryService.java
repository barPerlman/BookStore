package bgu.spl.mics.application.services;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.CheckBookEvent;
import bgu.spl.mics.application.messages.TerminateBroadcast;
import bgu.spl.mics.application.passiveObjects.Inventory;
import bgu.spl.mics.application.passiveObjects.MoneyRegister;
import bgu.spl.mics.application.passiveObjects.OrderResult;
import bgu.spl.mics.application.passiveObjects.ResourcesHolder;

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
	 * A protected function that initializes the InventoryService.
	 */
	protected void initialize() {
		// when TerminateBroadcast is received then the InventoryService should be terminated
		this.subscribeBroadcast(TerminateBroadcast.class, terminateBroadcast->{
			this.terminate();

		});

		// when CheckBookEvent is received then the InventoryService should react
		this.subscribeEvent(CheckBookEvent.class, checkBookEvent -> {
			int price = this.inventory.checkAvailabiltyAndGetPrice(checkBookEvent.getBookName());

			// if the book is exist and the price of the book is lower then the money that left
			if (price!=-1 && price <= checkBookEvent.getAvailableCreditAmount()){
				OrderResult orderResult = this.inventory.take(checkBookEvent.getBookName());

				//if the book is not in stuck
				if (orderResult == OrderResult.NOT_IN_STOCK){
					System.out.println("The book: "+checkBookEvent.getBookName()+" is not in stock");
				}
				else// if the book is in stuck
					complete(checkBookEvent, price);
				//System.out.println("the book can be bought");
			}
			// if the book doesn't exist or the price of the book is bigger then the money that left
			else {
				//System.out.println("the book can't be bought");
				complete(checkBookEvent, null);
			}
		});
		//System.out.println("Inventory service: "+this.getName()+" is initialized");
	}

}