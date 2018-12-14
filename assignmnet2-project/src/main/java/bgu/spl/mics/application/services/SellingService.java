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
 * <p>
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class SellingService extends MicroService {
    private int orderId = 1; // represents the id of the receipts
    private MoneyRegister moneyRegister;

    public SellingService(String name) {
        super(name);
        this.moneyRegister = MoneyRegister.getInstance();
    }

    /**
     * A protected function that initializes the SellingService.
     */
    protected void initialize() {
        // when TerminateBroadcast is received then the SellingService should be terminated
        this.subscribeBroadcast(TerminateBroadcast.class, terminateTick -> {
            this.terminate();
            System.out.println("service name: "+getName()+" terminated");

        });

        // when BookOrderEvent is received then the SellingService should react
        this.subscribeEvent(BookOrderEvent.class, bookOrderEvent -> {
            OrderReceipt orderReceipt = new OrderReceipt();

            // Creates new Future with the time that the selling service started processing his order
            Future<Integer> processTick = sendEvent(new CurrTickEvent());
            if (processTick != null) {
                Integer tempProcessTick = processTick.get(1, TimeUnit.MILLISECONDS);
                if (tempProcessTick != null) {
                    // Creates new Future with the name of the book and the money that left to the customer
                    Future<Integer> checkBook = sendEvent(new CheckBookEvent(bookOrderEvent.getBookName(), bookOrderEvent.getCustomer().getAvailableCreditAmount()));
                    if (checkBook != null) {
                        Integer price = checkBook.get(1, TimeUnit.MILLISECONDS);

                        // if the book is available in the store
                        if (price != null) {
                            // Creates new Future with the time that this receipt issued
                            Future<Integer> issuedTick = sendEvent(new CurrTickEvent());
                            if (issuedTick != null) {
                                Integer tempIssuedTick = issuedTick.get(1, TimeUnit.MILLISECONDS);
                                if (tempIssuedTick != null) {
                                    this.moneyRegister.chargeCreditCard(bookOrderEvent.getCustomer(), price);
                                    updateReceipt(bookOrderEvent,orderReceipt,price,tempIssuedTick,tempProcessTick);
                                    moneyRegister.file(orderReceipt);
                                    complete(bookOrderEvent, orderReceipt);
                                    // Creates new DeliveryEvent of the book
                                    sendEvent(new DeliveryEvent(orderReceipt, bookOrderEvent.getCustomer().getDistance(), bookOrderEvent.getCustomer().getAddress()));
                                    //System.out.println("The customer: " + bookOrderEvent.getCustomer().getName() + " bought the book: " + details.getBookName());
                                    return;
                                }
                                // System.out.println("The order " + bookOrderEvent.getCustomer().getName() + " made failed.");
                            }
                        }
                    }
                }
            }
            // if the book is not available in the store
            complete(bookOrderEvent,null);
        });
        //System.out.println("Selling service: "+this.getName()+" is initialized");
    }

    /**
     * A private function that setts all the receipt's details.
     */
    private void updateReceipt(BookOrderEvent bookOrderEvent,OrderReceipt orderReceipt,int price,int issuedTick,int processTick) {
        orderReceipt.setSeller(this.getName());
        orderReceipt.setOrderId(this.orderId);
        this.orderId++;
        orderReceipt.setBookTitle(bookOrderEvent.getBookName());
        orderReceipt.setCustomerId(bookOrderEvent.getCustomer().getId());
        orderReceipt.setOrderTick(bookOrderEvent.getOrderTickTime());
        orderReceipt.setPrice(price);
        orderReceipt.setissuedTick(issuedTick);
        orderReceipt.setProccessTick(processTick);
    }
}