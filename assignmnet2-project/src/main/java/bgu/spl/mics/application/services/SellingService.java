package bgu.spl.mics.application.services;

import bgu.spl.mics.Future;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.*;
import bgu.spl.mics.application.passiveObjects.MoneyRegister;
import bgu.spl.mics.application.passiveObjects.OrderReceipt;
import bgu.spl.mics.application.passiveObjects.ResourcesHolder;
import bgu.spl.mics.application.passiveObjects.Inventory;

import java.util.concurrent.TimeUnit;

/**
 * Selling service in charge of taking orders from customers.
 * Holds a reference to the {@link MoneyRegister} singleton of the store.
 * Handles {@link BookOrderEvent}.
 * This class may not hold references for objects which it is not responsible for:
 * {@link ResourcesHolder}, {@link Inventory}.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class SellingService extends MicroService{
	private MoneyRegister moneyRegister;
	private int orderId=1; // in order to keep truck on the receipts' ids

	public SellingService(String name) {
		super("SellingService: "+name);
		this.moneyRegister = MoneyRegister.getInstance();
	}

	/**
	 * A protected function that initializes the SellingService.
	 */
	protected void initialize() {
		// when TerminateBroadcast is received then the SellingService should be terminated
		this.subscribeBroadcast(TerminateBroadcast.class, terminateTick->{
			this.terminate();
		});

		// when BookOrderEvent is received then the SellingService should react
		this.subscribeEvent(BookOrderEvent.class, bookOrderEvent -> {
			OrderReceipt receipt = new OrderReceipt();

			// Creates new Future with the time that the selling service started processing his order
			Future<Integer> processTick = sendEvent(new CurrTickEvent());
			Integer processTickTime = processTick.get(1, TimeUnit.MILLISECONDS);

			// Creates new Future with the name of the book and the money that left to the customer
			Future<Integer> takeBook = sendEvent(new CheckBookEvent(bookOrderEvent.getBookName(), bookOrderEvent.getCustomer().getAvailableCreditAmount()));
			Integer price = takeBook.get(1, TimeUnit.MILLISECONDS);

			// if the book is available in the store
			if (price != null) {
				this.moneyRegister.chargeCreditCard(bookOrderEvent.getCustomer(), price);

				// Creates new Future with the time that this receipt issued
				Future<Integer> issuedTick = sendEvent(new CurrTickEvent());
				Integer issuedTickTime = issuedTick.get(1, TimeUnit.MILLISECONDS);

				setReceipt(receipt, bookOrderEvent, processTickTime, issuedTickTime, price);

				moneyRegister.file(receipt);
				complete(bookOrderEvent, receipt);

				// Creates new DeliveryEvent of the book
				sendEvent(new DeliveryEvent(receipt, bookOrderEvent.getCustomer().getDistance(), bookOrderEvent.getCustomer().getAddress()));
				//System.out.println("The customer: " + bookOrderEvent.getCustomer().getName() + " bought the book: " + details.getBookName());
			}
			// if the book is not available in the store
			else {
				complete(bookOrderEvent, receipt);
				//System.out.println("The order " + bookOrderEvent.getCustomer().getName() + " made failed.");
			}
		});

		//System.out.println("Selling service: "+this.getName()+" is initialized");
	}

	/**
	 * A private function that setts all the receipt's details.
	 */
	private void setReceipt(OrderReceipt receipt, BookOrderEvent details, int processTickTime, int issuedTickTime, int price){
		receipt.setBookTitle(details.getBookName());
		receipt.setCustomerId(details.getCustomer().getId());
		receipt.setOrderTick(details.getOrderTickTime());
		receipt.setSeller(this.getName());
		receipt.setProccessTick(processTickTime);
		receipt.setissuedTick(issuedTickTime);
		receipt.setPrice(price);
		receipt.setOrderId(this.orderId);
		this.orderId++;
	}
}