package bgu.spl.mics.application.passiveObjects;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Passive data-object representing a information about a certain book in the inventory.
 * You must not alter any of the given public methods of this class. 
 * <p>
 * You may add fields and methods to this class as you see fit (including public methods).
 */
public class BookInventoryInfo {

	/**
     * Retrieves the title of this book.
     * <p>
     * @return The title of this book.   
     */
	private String bookTitle;
	private AtomicInteger amountInInventory;
	private int price;

	public BookInventoryInfo(String bookTitle, int amountInInventory, int price){
		this.amountInInventory=new AtomicInteger(amountInInventory);
		this.price=price;
		this.bookTitle=bookTitle;

	}
	public String getBookTitle() {
		return this.bookTitle;
	}

	/**
     * Retrieves the amount of books of this type in the inventory.
     * <p>
     * @return amount of available books.      
     */
	public int getAmountInInventory() {
		return this.amountInInventory.get();
	}

	/**
	 * Sets the AmountInInventory for book.
	 * return 1 if the set succeeded else returns 0
	 */
	public int setAmountInInventory(Integer sum){
		Integer afterValue,beforeValue;
		do{
			beforeValue=amountInInventory.get();
			if(beforeValue>0){
				afterValue=sum;
			}
			else{
				return 0;
			}
		}
		while (!amountInInventory.compareAndSet(beforeValue,afterValue));
		return 1;
	}

	/**
     * Retrieves the price for  book.
     * <p>
     * @return the price of the book.
     */
	public int getPrice() {
		return this.price;
	}

	public void setPrice(int price) {
		this.price = price;
	}

	public String toString(){
		return "title: "+bookTitle+", amount: "+amountInInventory+", price: "+price;
	}
}
