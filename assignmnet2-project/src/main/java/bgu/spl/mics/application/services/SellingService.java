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

	/**
	 * SellingService's constructor.
	 *
	 * @param name - the name of the sellingService.
	 */
	public SellingService(String name) {
		super(name);
		this.moneyRegister = MoneyRegister.getInstance();
	}

	/**
	 * This method initializes the sellingService.
	 */
	protected void initialize() {
		terminateService();
		orderBook();
		System.out.println("Selling service: "+this.getName()+" is initialized");
	}

	/**
	 * This method makes sure that the sellingService terminates itself
	 * when StopTickBroadcast is received.
	 */
	private void terminateService(){
		this.subscribeBroadcast(TerminateBroadcast.class, terminateTick->{
			this.terminate();
		});
	}

	/**
	 * This method makes sure that the sellingService terminates itself
	 * when StopTickBroadcast is received.
	 */
	private void orderBook() {
		this.subscribeEvent(BookOrderEvent.class, details -> {
			// Initializing the time in which the selling service started processing the order
			Future<Integer> processTick = sendEvent(new CurrTickEvent());
			Integer processTickTime = processTick.get(1, TimeUnit.MILLISECONDS);

			// Saving the price the customer paid for the book, this price is returned from TakeBookEvent
			Future<Integer> takeBook = sendEvent(new CheckBookEvent(details.getBookName(), details.getCustomer().getAvailableCreditAmount()));
			Integer price = takeBook.get(1, TimeUnit.MILLISECONDS);

			// Initializing a new receipt for this order
			OrderReceipt receipt = new OrderReceipt();

			// If the book was taking successfully,
			// i.e. the customer can afford the book and there's an available copy of the book
			if (price != null) {
				// Charging the customer for the book
				this.moneyRegister.chargeCreditCard(details.getCustomer(), price);

				// Initializing the time in which this receipt was issued
				Future<Integer> issuedTick = sendEvent(new CurrTickEvent());
				Integer issuedTickTime = issuedTick.get(1, TimeUnit.MILLISECONDS);

				// Setting all of the receipt's details/information
				setReceipt(receipt, details, processTickTime, issuedTickTime, price);
				// Adding the receipt to list of receipts in moneyRegister
				moneyRegister.file(receipt);
				complete(details, receipt);
				// Ordering a delivery of the book
				sendEvent(new DeliveryEvent(receipt, details.getCustomer().getAddress(), details.getCustomer().getDistance()));
				System.out.println("The customer: " + details.getCustomer().getName() + " bought the book: " + details.getBookName());
			} else {
				complete(details, receipt);
				System.out.println("The order " + details.getCustomer().getName() + " made failed.");
			}
		});
	}

	/**
	 * This method setts all of the given receipt's details/information with the other parameters.
	 *
	 * @param receipt - the receipt that is being set.
	 * @param details - a BookOrderEvent.
	 * @param processTickTime - the time in which the selling service started processing the order.
	 * @param issuedTickTime - the time in which this receipt was issued.
	 * @param price - the price of the book that was bought.
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