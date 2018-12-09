package bgu.spl.mics.application.passiveObjects;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Passive data-object representing a customer of the store.
 * You must not alter any of the given public methods of this class.
 * <p>
 * You may add fields and methods to this class as you see fit (including public methods).
 */
public class Customer {

	private int _id;							//customer's id
	private String _name;						//customer's name
	private String _address;					//customer's address
	private int _distance;						//distance of customer from store
	private List<OrderReceipt> _receipts;		//order receipts for this customer
	private int _creditCard;					//credit card number
	private AtomicInteger _availableAmountInCreditCard;	//money remains in card

	public Customer(int id,String name,String address,int distance,int creditCard,int availableAmountInCreditCard) {
		_id=id;
		_name=name;
		_address=address;
		_distance=distance;
		_receipts= Collections.synchronizedList(new LinkedList<>());
		_creditCard=creditCard;
		_availableAmountInCreditCard=new AtomicInteger(availableAmountInCreditCard);
	}


	/**
     * Retrieves the name of the customer.
     */
	public String getName() {
		return _name;
	}

	/**
     * Retrieves the ID of the customer  . 
     */
	public int getId() {
		return _id;
	}
	
	/**
     * Retrieves the address of the customer.  
     */
	public String getAddress() {
		return _address;
	}
	
	/**
     * Retrieves the distance of the customer from the store.  
     */
	public int getDistance() {
		return _distance;
	}

	
	/**
     * Retrieves a list of receipts for the purchases this customer has made.
     * <p>
     * @return A list of receipts.
     */
	public List<OrderReceipt> getCustomerReceiptList() {
		return _receipts;
	}
	
	/**
     * Retrieves the amount of money left on this customers credit card.
     * <p>
     * @return Amount of money left.   
     */
	public int getAvailableCreditAmount() {
		return _availableAmountInCreditCard.get();
	}
	
	/**
     * Retrieves this customers credit card serial number.    
     */
	public int getCreditNumber() {
		return _creditCard;
	}

	/**
	 * Update the custumer's money amount left in card after purchase
	 * @param amountToCharge
	 */
	public void chargeCard(int amountToCharge){		//using atomic integer method
		int localLeftAmount;
		do{
			localLeftAmount=_availableAmountInCreditCard.get();
		}while(!_availableAmountInCreditCard.compareAndSet(localLeftAmount,localLeftAmount-amountToCharge));
	}

	/**
	 * add the receipt r to the order receipts list of the customer
	 * @param r
	 */
	public void addReceipt(OrderReceipt r){
		_receipts.add(r);
	}
	
}
