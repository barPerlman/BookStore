package bgu.spl.mics.application.services;

import bgu.spl.mics.Future;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.*;
import bgu.spl.mics.application.passiveObjects.Inventory;
import bgu.spl.mics.application.passiveObjects.MoneyRegister;
import bgu.spl.mics.application.passiveObjects.OrderReceipt;
import bgu.spl.mics.application.passiveObjects.ResourcesHolder;

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
    private MoneyRegister moneyRegister;
    private int currTick;

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
        });

        this.subscribeBroadcast(TickBroadcast.class, tickBroadcast -> {
            this.currTick=tickBroadcast.getCurrTick();
        });

        // when BookOrderEvent is received then the SellingService should react
        this.subscribeEvent(BookOrderEvent.class, bookOrderEvent -> {
            OrderReceipt orderReceipt = new OrderReceipt();
            // Creates new Future with the name of the book and the money that left to the customer
            Future<Integer> checkBook = sendEvent(new CheckBookEvent(bookOrderEvent.getBookName(), bookOrderEvent.getCustomer().getAvailableCreditAmount()));
            if (checkBook != null) {
                Integer price = checkBook.get();
                // if the book is available in the store
                if (price != null) {
                        this.moneyRegister.chargeCreditCard(bookOrderEvent.getCustomer(), price);
                        updateReceipt(bookOrderEvent,orderReceipt,price,this.currTick,this.currTick);
                        moneyRegister.file(orderReceipt);
                        complete(bookOrderEvent, orderReceipt);
                        // Creates new DeliveryEvent of the book
                        sendEvent(new DeliveryEvent(orderReceipt, bookOrderEvent.getCustomer().getDistance(), bookOrderEvent.getCustomer().getAddress()));
                        //System.out.println("Selling service: The customer: " + bookOrderEvent.getCustomer().getName() + " bought the book: " + bookOrderEvent.getBookName());
                        //return;
                }
                else
                {
                    complete(bookOrderEvent,null);
                    //System.out.println("The order " + bookOrderEvent.getCustomer().getName() + " failed in Selling service.");
                }
            }
            // if the book is not available in the store
            else complete(bookOrderEvent,null);
        });
        //System.out.println("Selling service: "+this.getName()+" is initialized");
    }

    /**
     * A private function that setts all the receipt's details.
     */
    private void updateReceipt(BookOrderEvent bookOrderEvent,OrderReceipt orderReceipt,int price,int issuedTick,int processTick) {
        orderReceipt.setSeller(this.getName());
        orderReceipt.setBookTitle(bookOrderEvent.getBookName());
        orderReceipt.setCustomerId(bookOrderEvent.getCustomer().getId());
        orderReceipt.setOrderTick(bookOrderEvent.getOrderTickTime());
        orderReceipt.setPrice(price);
        orderReceipt.setissuedTick(issuedTick);
        orderReceipt.setProccessTick(processTick);
    }
}