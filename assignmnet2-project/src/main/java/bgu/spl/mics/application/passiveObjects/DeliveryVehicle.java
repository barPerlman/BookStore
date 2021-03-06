package bgu.spl.mics.application.passiveObjects;

/**
 * Passive data-object representing a delivery vehicle of the store.
 * You must not alter any of the given public methods of this class.
 * <p>
 * You may add ONLY private fields and methods to this class.
 */
public class DeliveryVehicle {

	//class members
	private int _license;		//license number of the vehicle
	private int _speed;			//the required amount of milliseconds are required for each delivery
	/**
     * Constructor.   
     */
	 public DeliveryVehicle(int license, int speed) {
		this._license=license;
		this._speed=speed;
	  }
	/**
     * Retrieves the license of this delivery vehicle.   
     */
	public int getLicense() {
		return _license;
	}
	
	/**
     * Retrieves the speed of this vehicle person.   
     * <p>
     * @return Number of ticks needed for 1 Km.
     */
	public int getSpeed() {
		return _speed;
	}
	
	/**
     * Simulates a delivery by sleeping for the amount of time that 
     * it takes this vehicle to cover {@code distance} KMs.  
     * <p>
     * @param address	The address of the customer.
     * @param distance	The distance from the store to the customer.
     */
	public void deliver(String address, int distance) {
		try{
			Thread.sleep(distance*_speed);		//suspend execution of current thread for (distance*speed) period

		}catch(InterruptedException e){
			e.printStackTrace();
		}
	}
	/*	////for tests only! remove before submission!!!
	public String toString(){
		return "license: "+_license+", speed: "+_speed;
	}*/
}
