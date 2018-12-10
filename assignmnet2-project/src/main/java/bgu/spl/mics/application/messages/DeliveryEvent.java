package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;
import bgu.spl.mics.application.passiveObjects.OrderReceipt;

public class DeliveryEvent implements Event<Boolean> {

    private OrderReceipt orderReceipt;

    public DeliveryEvent(OrderReceipt orderReceipt){
        this.orderReceipt=orderReceipt;
    }

    public OrderReceipt getOrderReceipt(){
        return this.orderReceipt;
    }
}
