package bgu.spl.mics.application.passiveObjects;

/**
 * Passive data-object representing a receipt that should 
 * be sent to a customer after the completion of a BookOrderEvent.
 * You must not alter any of the given public methods of this class.
 * <p>
 * You may add fields and methods to this class as you see fit (including public methods).
 */
public class OrderReceipt {


	//class members
	private int _orderId;			//id if order
	private  String _seller;			//name of the service that handled this order
	private int _customer;			//customer id
	private String _bookTitle;		//book name
	private int _price;
	private int _issuedTick;
	private int _orderTick;
	private int _processTick;

	/**
	 * constructor
	 * @param orderId
	 * @param seller
	 * @param customer
	 * @param bookTitle
	 * @param price
	 * @param issuedTick
	 * @param orderTick
	 * @param processTick
	 */
	public OrderReceipt(int orderId, String seller, int customer, String bookTitle, int price, int issuedTick, int orderTick, int processTick) {
		this._orderId = orderId;
		this._seller = seller;
		this._customer = customer;
		this._bookTitle = bookTitle;
		this._price = price;
		this._issuedTick = issuedTick;
		this._orderTick = orderTick;
		this._processTick = processTick;
	}



	/**
     * Retrieves the orderId of this receipt.
     */
	public int getOrderId() {
		return _orderId;
	}
	
	/**
     * Retrieves the name of the selling service which handled the order.
     */
	public String getSeller() {
		return _seller;
	}
	
	/**
     * Retrieves the ID of the customer to which this receipt is issued to.
     * <p>
     * @return the ID of the customer
     */
	public int getCustomerId() {
		return _customer;
	}
	
	/**
     * Retrieves the name of the book which was bought.
     */
	public String getBookTitle() {
		return _bookTitle;
	}
	
	/**
     * Retrieves the price the customer paid for the book.
     */
	public int getPrice() {
		return _price;
	}
	
	/**
     * Retrieves the tick in which this receipt was issued.
     */
	public int getIssuedTick() {
		return _issuedTick;
	}
	
	/**
     * Retrieves the tick in which the customer sent the purchase request.
     */
	public int getOrderTick() {
		return _orderTick;
	}
	
	/**
     * Retrieves the tick in which the treating selling service started 
     * processing the order.
     */
	public int getProcessTick() {
		return _processTick;
	}
}
