package bgu.spl.mics.application.services;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.CurrTickEvent;
import bgu.spl.mics.application.messages.TerminateBroadcast;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.passiveObjects.Inventory;
import bgu.spl.mics.application.passiveObjects.MoneyRegister;
import bgu.spl.mics.application.passiveObjects.ResourcesHolder;

import javax.swing.*;

/**
 * TimeService is the global system timer There is only one instance of this micro-service.
 * It keeps track of the amount of ticks passed since initialization and notifies
 * all other micro-services about the current time tick using {@link //Tick Broadcast}.
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
		super("time");
		currTick=1;
		this.speed=speed;
		this.duration=duration;
	}

	@Override
	protected void initialize() {
		// when TerminateBroadcast is received then the TimeService should be terminated
		subscribeBroadcast(TerminateBroadcast.class,terminateBroadcast -> {
			this.terminate();
			System.out.println("service name: "+getName()+" terminated");

		});

		// when CurrTickEvent is received then the TimeService gets the current tick
		subscribeEvent(CurrTickEvent.class,currTickEvent -> {
			complete(currTickEvent,currTick);
		});

		// Defining the timer activation
		timer = new Timer(speed,(e) ->{
			currTick++;
			///////////////System.out.println("The current tick is : "+currTick);
			if(currTick==duration) {// if the duration time is over
				// timeService tells messageBus to tell all of the services that subscribed to him that
				// terminates itself
				this.sendBroadcast(new TerminateBroadcast(currTick));
				timer.stop();// the duration is over so the timer needs to stop
			}
			else {// if the duration time is not over
				sendBroadcast(new TickBroadcast(currTick));
			}
			////////////////System.out.println("Services: "+this.getName()+" is terminated");

		});
		timer.start();
	}
}
