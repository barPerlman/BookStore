package bgu.spl.mics.application.services;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.DeliveryEvent;
import bgu.spl.mics.application.messages.ResourceServiceEvent;
import bgu.spl.mics.application.messages.TerminateBroadcast;
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

    /**
     * A protected function that initializes the LogisticsService.
     */
    @Override
    protected void initialize() {
        // when DeliveryEvent is received then the LogisticsService should react
        this.subscribeEvent(DeliveryEvent.class, deliveryEvent -> {
            DeliveryEvent d = new DeliveryEvent(deliveryEvent.getOrderReceipt(),deliveryEvent.getDistance(),deliveryEvent.getAddress());
            sendEvent(new ResourceServiceEvent(d));
        });
        // when TerminateBroadcast is received then the LogisticsService should be terminated
        this.subscribeBroadcast(TerminateBroadcast.class, terminateBroadcast->{
            this.terminate();

        });
       // System.out.println("Service " + getName() + " started");
    }

}