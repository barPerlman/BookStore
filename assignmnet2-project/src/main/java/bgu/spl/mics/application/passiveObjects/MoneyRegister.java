package bgu.spl.mics.application.passiveObjects;


import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * Passive object representing the store finance management. 
 * It should hold a list of receipts issued by the store.
 * <p>
 * This class must be implemented safely as a thread-safe singleton.
 * You must not alter any of the given public methods of this class.
 * <p>
 * You can add ONLY private fields and methods to this class as you see fit.
 */
public class MoneyRegister implements Serializable {

	//class members
	private List<OrderReceipt> _issuedOrderReceipts;

	private int _orderId=1;	//the id of the order to add to the list

	/**
	 * The following is a thread safe singleton definition by a static class
	 */
	private static class MoneyRegHolder {
		private static final MoneyRegister INSTANCE = new MoneyRegister();
	}

	/**
	 * private constructor as part of the thread safe singleton
	 */
	private MoneyRegister(){
		_issuedOrderReceipts=Collections.synchronizedList(new LinkedList<>());
	}
	/**
     * Retrieves the single instance of this class.
     */
	public static MoneyRegister getInstance() {
		return MoneyRegHolder.INSTANCE;	}
	
	/**
     * Saves an order receipt in the money register.
     * <p>   
     * @param r		The receipt to save in the money register.
     */
	public void file (OrderReceipt r) {
		r.setOrderId(_orderId);
		_issuedOrderReceipts.add(r);
		_orderId++;
	}
	
	/**
     * Retrieves the current total earnings of the store.  
     */
	public int getTotalEarnings() {
		//using the snapshot method of operation on a list
		Iterator<OrderReceipt> it=_issuedOrderReceipts.iterator();	//get a copy of the current list
		int earningsSum=0;		//this variable holds the total earnings from book sells
		while(it.hasNext()){
			OrderReceipt currReceipt=it.next();
			earningsSum += currReceipt.getPrice();
		}
		return earningsSum;
	}
	
	/**
     * Charges the credit card of the customer a certain amount of money.
     * <p>
     * @param amount 	amount to charge
     */
	public void chargeCreditCard(Customer c, int amount) {
		c.chargeCard(amount);
	}
	
	/**
     * Prints to a file named @filename a serialized object List<OrderReceipt> which holds all the order receipts 
     * currently in the MoneyRegister
     * This method is called by the main method in order to generate the output.. 
     */
	public void printOrderReceipts(String filename) {
		try{
			FileOutputStream fos =new FileOutputStream(filename);
			ObjectOutputStream oos=new ObjectOutputStream(fos);
			oos.writeObject(_issuedOrderReceipts);
			fos.close();
			oos.close();
		}catch(IOException e){
			e.printStackTrace();
		}

	}
}
