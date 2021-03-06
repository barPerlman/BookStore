package bgu.spl.mics.application.passiveObjects;



import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;


/**
 * Passive data-object representing the store inventory.
 * It holds a collection of {@link BookInventoryInfo} for all the
 * books in the store.
 * <p>
 * This class must be implemented safely as a thread-safe singleton.
 * You must not alter any of the given public methods of this class.
 * <p>
 * You can add ONLY private fields and methods to this class as you see fit.
 */
public class Inventory {

	private ConcurrentHashMap<String,BookInventoryInfo> booksInventoryInfo;
	/**
	 * The following is a thread safe singleton definition by a static class
	 */
	private static class InventoryHolder {
		private static  Inventory INSTANCE = new Inventory();
	}

	/**
	 * private constructor as part of the thread safe singleton
	 */
	private Inventory(){
		booksInventoryInfo=(new ConcurrentHashMap<>());
	}
	/**
	 * Retrieves the single instance of this class.
	 */
	public static Inventory getInstance() {
		return InventoryHolder.INSTANCE;	}
	
	/**
     * Initializes the store inventory. This method adds all the items given to the store
     * inventory.
     * <p>
     * @param inventory 	Data structure containing all data necessary for initialization
     * 						of the inventory.
     */
	public void load (BookInventoryInfo[ ] inventory ) {
		if(inventory!=null) {
			for (BookInventoryInfo bookInventoryInfo : inventory) {
				if(bookInventoryInfo!=null){
					booksInventoryInfo.put(bookInventoryInfo.getBookTitle(),bookInventoryInfo);
				}
			}
		}
	}
	
	/**
     * Attempts to take one book from the store.
     * <p>
     * @param book 		Name of the book to take from the store
     * @return 	an {@link Enum} with options NOT_IN_STOCK and SUCCESSFULLY_TAKEN.
     * 			The first should not change the state of the inventory while the 
     * 			second should reduce by one the number of books of the desired type.
     */
	public OrderResult take (String book) {
		BookInventoryInfo bookInventoryInfo = booksInventoryInfo.get(book);
		if(bookInventoryInfo!=null && bookInventoryInfo.setAmountInInventory(bookInventoryInfo.getAmountInInventory()-1)==1)
			return OrderResult.SUCCESSFULLY_TAKEN;
		return OrderResult.NOT_IN_STOCK;
	}

	/**
     * Checks if a certain book is available in the inventory.
     * <p>
     * @param book 		Name of the book.
     * @return the price of the book if it is available, -1 otherwise.
     */
	public int checkAvailabiltyAndGetPrice(String book) {
		// if the book is exist in the pool and if the capacity of the book is up to 0
		if(this.booksInventoryInfo.get(book)!=null&&this.booksInventoryInfo.get(book).getAmountInInventory()>0){
			return this.booksInventoryInfo.get(book).getPrice();
		}
		else{// the book is not exist in the pool or it's capacity is 0
			return  -1;
		}
	}
	
	/**
     * 
     * <p>
     * Prints to a file name @filename a serialized object HashMap<String,Integer> which is a Map
	 * of all the books in the inventory. The keys of the Map (type {@link String})
     * should be the titles of the books while the values (type {@link Integer}) should be
     * their respective available amount in the inventory. 
     * This method is called by the main method in order to generate the output.
     */
	public void printInventoryToFile(String filename){

		//convert the hash table of books refs to books ramained amount
		HashMap<String,Integer> outputBooks=new HashMap<>();
		for(String book:booksInventoryInfo.keySet()) {
			BookInventoryInfo bookRef = booksInventoryInfo.get(book);
			int remainedAmount = bookRef.getAmountInInventory();
			outputBooks.putIfAbsent(book, new Integer(remainedAmount));
		}
		//print to file the output hash map
		try{
			FileOutputStream fos =new FileOutputStream(filename);
			ObjectOutputStream oos=new ObjectOutputStream(fos);
			oos.writeObject(outputBooks);
		}catch(IOException e){
			e.printStackTrace();
		}
	}

}
