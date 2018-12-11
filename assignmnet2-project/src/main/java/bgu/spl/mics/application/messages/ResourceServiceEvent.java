package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;

public class ResourceServiceEvent implements Event {
    private DeliveryEvent deliveryMessage;

    public ResourceServiceEvent(DeliveryEvent deliveryEvent){
        this.deliveryMessage = deliveryEvent;
    }

    public DeliveryEvent getDeliveryMessage() {
        return deliveryMessage;
    }
}