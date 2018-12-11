package bgu.spl.mics.application.services;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.DeliveryEvent;
import bgu.spl.mics.application.messages.ResourceServiceEvent;
import bgu.spl.mics.application.passiveObjects.ResourcesHolder;
import bgu.spl.mics.application.passiveObjects.MoneyRegister;
import bgu.spl.mics.application.passiveObjects.Inventory;
/**
 * Logistic service in charge of delivering books that have been purchased to customers.
 * Handles {@link DeliveryEvent}.
 * This class may not hold references for objects which it is not responsible for:
 * {@link ResourcesHolder}, {@link MoneyRegister}, {@link Inventory}.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class LogisticsService extends MicroService {

    public LogisticsService(String name) {
        super(name);
    }

    @Override
    protected void initialize() {
        System.out.println("Service " + getName() + " started");
        this.subscribeEvent(DeliveryEvent.class, deliveryMessage -> {// sends an event to resources, no references,
            DeliveryEvent d = new DeliveryEvent(deliveryMessage.getOrderReceipt(), deliveryMessage.getAddress(), deliveryMessage.getDistance());
            sendEvent(new ResourceServiceEvent(d));
        });
    }

}