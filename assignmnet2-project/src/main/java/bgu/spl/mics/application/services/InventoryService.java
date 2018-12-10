package bgu.spl.mics.application.services;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.CheckBookEvent;
import bgu.spl.mics.application.messages.TerminateBroadcast;
import bgu.spl.mics.application.passiveObjects.Inventory;
import bgu.spl.mics.application.passiveObjects.OrderResult;

/**
 * InventoryService is in charge of the book inventory and stock.
 * Holds a reference to the {@link Inventory} singleton of the store.
 * This class may not hold references for objects which it is not responsible for:
 * {@link ResourcesHolder}, {@link MoneyRegister}.
 * 
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */

public class InventoryService extends MicroService{

	private Inventory inventory;

	public InventoryService(String inventoryServiceName) {
		super("InventoryService :" +inventoryServiceName);
		inventory=Inventory.getInstance();
	}

	@Override
	protected void initialize() {
		subscribeBroadcast(TerminateBroadcast.class, c -> {
			this.terminate();
		});

		this.subscribeEvent(CheckBookEvent.class,info ->{
			int bookPrice = this.inventory.checkAvailabiltyAndGetPrice(info.getBookName());
			if(bookPrice!=-1 && bookPrice<=info.getAvailableCreditAmount()){
				OrderResult orderResult=this.inventory.take(info.getBookName());
				if(orderResult==OrderResult.NOT_IN_STOCK){
					System.out.println("The book can't be taken");
				}
				else{
					complete(info,bookPrice);
				}
			}
			else
				complete(info,null);

		});
	}

}
