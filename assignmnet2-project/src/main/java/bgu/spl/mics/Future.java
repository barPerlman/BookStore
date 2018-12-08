package bgu.spl.mics;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
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
	private Object _lock;	//lock object for get functions
	private boolean _isResolved;	//tells if this Future object is resolved-true,else-false
	private AtomicReference<T> _result;    //holds the result of the associated operation
	/**
	 * This should be the only public constructor in this class.
	 */
	public Future() {
		_lock=new Object();     //init the lock object
		_result=new AtomicReference<>(null);    //init the result as not resolved
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
	public T get() {
		synchronized (_lock) {
			while (!isDone()) {                //as long as the Future object is not resolved (Precondition blocking) block continuing
				try {
					wait();
				} catch (InterruptedException ignore) {
				}
			}
			return _result.get();			//here the result is available
		}

	}

	/**
	 * Resolves the result of this Future object.
	 */
	//@PRE: none
	//@POST: this.get()=@param result
	public void resolve (T result) {
		//assign the result in a thread safe manner
		T localResult;
		T newResult=result;
		do{
			localResult=this._result.get();
			newResult=result;
		}while(!this._result.compareAndSet(localResult,newResult));	//busy wait
		_isResolved=true;			//update the status of the future object to resolved
		//notifyAll();				//notify the threads which are waiting for the result to be resolve

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
		try{
			synchronized (_lock) {
				while (!isDone()) {                          //result is not resolved
					_lock.wait(unit.toMillis(timeout));   //here the thread will wait timeout amount of time in this's waiting list
					break;                                  //after the sleep go out and return null
				}
			}
		}catch(InterruptedException ignore){            //return
		}
		return _result.get();
	}
}