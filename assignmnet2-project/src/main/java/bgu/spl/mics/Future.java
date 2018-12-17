package bgu.spl.mics;

import java.util.concurrent.TimeUnit;
/**
 * A Future object represents a promised result - an object that will
 * eventually be resolved to hold a result of some operation. The class allows
 * Retrieving the result once it is available.
 *
 * Only private methods may be added to this class.
 * No public constructor is allowed except for the empty constructor.
 */
public class Future<T> {
	//@INV: this!=null
	private boolean _isResolved=false;	//tells if this Future object is resolved-true,else-false
	private T _result;    //holds the result of the associated operation
	/**
	 * This should be the only public constructor in this class.
	 */
	public Future() {
		_result=null;   //init the result as not resolved
		_isResolved=false;
	}

	/**
	 * retrieves the result the Future object holds if it has been resolved.
	 * This is a blocking method! It waits for the computation in case it has
	 * not been completed.
	 * <p>
	 * @return return the result of type T if it is available, if not wait until it is available.
	 *
	 */
	//@PRE: result!=null
	public synchronized T get() {
			while (!_isResolved) {                //as long as the Future object is not resolved (Precondition blocking) block continuing
				try {
					this.wait();

				} catch (InterruptedException ignore) {
				}
			}
			//notifyAll();				//notify the threads which are waiting for the result to be resolve
			return _result;			//here the result is available
	}

	/**
	 * Resolves the result of this Future object.
	 */
	//@PRE: none
	//@POST: this.get()=@param result
	public synchronized void resolve (T result) {
		//assign the result in a thread safe manner
		_isResolved=true;
		this._result=result;
		notify();
	}

	/**
	 * @return true if this object has been resolved, false otherwise
	 */
	//@PRE: none
	public boolean isDone() {
		return _isResolved;	//the result holds the T object if resolved,otherwise-false
	}

	/**
	 * retrieves the result the Future object holds if it has been resolved,
	 * This method is non-blocking, it has a limited amount of time determined
	 * by {@code timeout}
	 * <p>
	 * @param timeout 	the maximal amount of time units to wait for the result.
	 * @param unit		the {@link TimeUnit} time units to wait.
	 * @return return the result of type T if it is available, if not,
	 * 	       wait for {@code timeout} TimeUnits {@code unit}. If time has
	 *         elapsed, return null.
	 */
	//@PRE: @param timeout>0 @param unit!=null
	public T get(long timeout, TimeUnit unit) {
		int currSleepTime = 0;
		if(!isDone()){
			while (!isDone() && currSleepTime<=timeout) {                          //result is not resolved
				try {
					unit.sleep(1);   //here the thread will wait timeout amount of time in this's waiting list
					currSleepTime++;
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
		return _result;
	}
}