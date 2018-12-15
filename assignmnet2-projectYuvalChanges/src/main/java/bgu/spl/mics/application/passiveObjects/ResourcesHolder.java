package bgu.spl.mics.application.passiveObjects;

import bgu.spl.mics.Future;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Semaphore;

/**
 * Passive object representing the resource manager.
 * You must not alter any of the given public methods of this class.
 * <p>
 * This class must be implemented safely as a thread-safe singleton.
 * You must not alter any of the given public methods of this class.
 * <p>
 * You can add ONLY private methods and fields to this class.
 */
public class ResourcesHolder {		//in this class we use semaphore in aim to acquire and release vehicle

	private ConcurrentLinkedQueue<DeliveryVehicle> _storedVehicles;		//holds the stored delivery vehicles
	private Semaphore _sem;

	private static class ResourceHolderHolder {
		private static final ResourcesHolder INSTANCE = new ResourcesHolder();
	}

	/**
	 * private constructor as part of the thread safe singleton
	 */
	private ResourcesHolder(){
		_storedVehicles= new ConcurrentLinkedQueue<>();	//init a concurrent queue of vehicles
	}

	/**
     * Retrieves the single instance of this class.
     */
	public static ResourcesHolder getInstance() {
		return ResourceHolderHolder.INSTANCE;
	}
	
	/**
     * Tries to acquire a vehicle and gives a future object which will
     * resolve to a vehicle.
     * <p>
     * @return 	{@link Future<DeliveryVehicle>} object which will resolve to a 
     * 			{@link DeliveryVehicle} when completed.   
     */
	public Future<DeliveryVehicle> acquireVehicle() {

		try {
			_sem.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		Future<DeliveryVehicle> future=new Future<>();	//in case the released amount of vehicles>=permits
		future.resolve(_storedVehicles.poll());
		return future;
	}
	
	/**
     * Releases a specified vehicle, opening it again for the possibility of
     * acquisition.
     * <p>
     * @param vehicle	{@link DeliveryVehicle} to be released.
     */
	public void releaseVehicle(DeliveryVehicle vehicle) {
		_storedVehicles.add(vehicle);
		_sem.release();
	}
	
	/**
     * Receives a collection of vehicles and stores them.
     * <p>
     * @param vehicles	Array of {@link DeliveryVehicle} instances to store.
     */
	public void load(DeliveryVehicle[] vehicles) {
		//init a semaphore with permits according to the amount of received vehicles and fair policy
		_sem=new Semaphore(vehicles.length,true);
		for(int i=0;i<vehicles.length;i++){
			_storedVehicles.add(vehicles[i]);
		}

	}

}
