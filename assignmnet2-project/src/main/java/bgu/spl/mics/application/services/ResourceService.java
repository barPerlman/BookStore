package bgu.spl.mics.application.services;

import bgu.spl.mics.Future;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.DeliveryEvent;
import bgu.spl.mics.application.messages.ResourceServiceEvent;
import bgu.spl.mics.application.passiveObjects.DeliveryVehicle;
import bgu.spl.mics.application.passiveObjects.ResourcesHolder;
import bgu.spl.mics.application.passiveObjects.Inventory;
import bgu.spl.mics.application.passiveObjects.MoneyRegister;

import java.util.concurrent.TimeUnit;

/**
 * ResourceService is in charge of the store resources - the delivery vehicles.
 * Holds a reference to the {@link ResourcesHolder} singleton of the store.
 * This class may not hold references for objects which it is not responsible for:
 * {@link MoneyRegister}, {@link Inventory}.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class ResourceService extends MicroService{
	private ResourcesHolder resourcesHolder;

	public ResourceService(String name) {
		super(name);
		this.resourcesHolder = ResourcesHolder.getInstance();
	}

	protected void initialize() {
		this.subscribeEvent(ResourceServiceEvent.class, deliveryMessage-> {
			Future<DeliveryVehicle> futureDeliveryVehicle =	this.resourcesHolder.acquireVehicle();
			DeliveryVehicle deliveryVehicle = futureDeliveryVehicle.get(); /// with time or without????
			deliveryVehicle.deliver(deliveryMessage.getDeliveryMessage().getAddress(), deliveryMessage.getDeliveryMessage().getDistance());
			this.resourcesHolder.releaseVehicle(deliveryVehicle);
		});
	}

}