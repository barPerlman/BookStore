package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;


/**
 * An event that is sent when the logisticService wishes to deliver the book
 */
public class ResourceServiceEvent implements Event {


public class ResourceServiceEvent implements Event {
    private DeliveryEvent deliveryMessage;

    public ResourceServiceEvent(DeliveryEvent deliveryEvent){

        this.deliveryEvent = deliveryEvent;
    }

    public DeliveryEvent getDeliveryMessage() {
        return deliveryEvent;

    }
}