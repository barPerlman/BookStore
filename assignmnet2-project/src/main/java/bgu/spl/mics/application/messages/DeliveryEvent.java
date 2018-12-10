package bgu.spl.mics.application.messages;
import bgu.spl.mics.Event;
import bgu.spl.mics.application.passiveObjects.OrderReceipt;

public class DeliveryEvent implements Event {
    private OrderReceipt orderReceipt;
    private String address;
    private int distance;

    public DeliveryEvent(OrderReceipt orderReceipt, String address, int distance) {
        this.orderReceipt = orderReceipt;
        this.address = address;
        this.distance = distance;
    }

    public int getDistance() {
        return distance;
    }

    public OrderReceipt getOrderReceipt() {
        return orderReceipt;
    }

    public String getAddress() {
        return address;
    }
}