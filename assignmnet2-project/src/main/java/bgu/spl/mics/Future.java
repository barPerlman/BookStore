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

	private AtomicReference<T> _result;    //holds the result of the associated operation
	/**
	 * This should be the only public constructor in this class.
	 */
	public Future() {
		_result=new AtomicReference<>(null);
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
		while(!isDone());	//as long as the Future object is not resolved (Precond blocking) block continuing
		return _result.get();			//here the result is available
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

		notifyAll();	//notify the threads which are waiting for the result to be resolve

	}

	/**
     * @return true if this object has been resolved, false otherwise
     */
	//@PRE: none
	public boolean isDone() {
		return _result.get()!=null;	//the result holds the T object if resolved,otherwise-false
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
            while(!isDone()) {                          //result is not resolved
                wait(unit.toMillis(timeout));   //here the thread will wait timeout amount of time in this's waiting list
                break;                                  //after the sleep go out and return null
            }
		}catch(InterruptedException ignore){            //return
		    return _result.get();
        }
    return null;
    }
}