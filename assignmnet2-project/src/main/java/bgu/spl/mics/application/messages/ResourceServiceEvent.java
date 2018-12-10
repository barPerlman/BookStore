package bgu.spl.mics.application.messages;

public class ResourceServiceEvent {

    private DeliveryEvent deliveryEvent;

    public ResourceServiceEvent(DeliveryEvent deliveryEvent){
        this.deliveryEvent=deliveryEvent;
    }

    public DeliveryEvent getDeliveryEvent() {
        return deliveryEvent;
    }
}
