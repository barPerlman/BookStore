package bgu.spl.mics.application.services;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.CurrTickEvent;
import bgu.spl.mics.application.messages.TerminateBroadcast;
import bgu.spl.mics.application.messages.TickBroadcast;

import javax.swing.*;

/**
 * TimeService is the global system timer There is only one instance of this micro-service.
 * It keeps track of the amount of ticks passed since initialization and notifies
 * all other micro-services about the current time tick using {@link Tick Broadcast}.
 * This class may not hold references for objects which it is not responsible for:
 * {@link ResourcesHolder}, {@link MoneyRegister}, {@link Inventory}.
 * 
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class TimeService extends MicroService{

	Timer timer;
	int speed;
	int duration;
	int currTick;
	public TimeService(int speed, int duration) {
		super("timer");
		currTick=1;
		this.speed=speed;
		this.duration=duration;
	}

	@Override
	protected void initialize() {
		// Defining the timer activation
		timer = new Timer(speed,e ->{
			currTick++;
			///////////////System.out.println("The current tick is : "+currTick);
			if(currTick==duration) {// if the duration time is over
				// timeService tells messageBus to tell all of the services that subscribed to receive
				// messages from time service that the current time is currTick
				this.sendBroadcast(new TerminateBroadcast(currTick)); // sends the current tick to the TickBroadCast
				timer.stop();// the duration is over so the timer needs to stop
			}
			else // if the duration time is not over
				sendBroadcast(new TickBroadcast(currTick)); // sends the current tick to the TickBroadCast
			subBroadcast();
			subEvent();

			////////////////System.out.println("Services: "+this.getName()+" is terminated");
			timer.start();
		});
	}

	private void subBroadcast(){
		subscribeBroadcast(TerminateBroadcast.class,c -> {
			this.terminate();
		});
	}

	private void subEvent(){
		subscribeEvent(CurrTickEvent.class,c -> {
			complete(c,currTick);
		});
	}
}
