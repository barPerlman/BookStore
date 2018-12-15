
import bgu.spl.mics.application.passiveObjects.BookInventoryInfo;
import bgu.spl.mics.application.passiveObjects.Inventory;
import bgu.spl.mics.application.passiveObjects.OrderResult;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.HashMap;

import static org.junit.Assert.*;

public class InventoryTest {

    Inventory inventory;

    @Before
    public void setUp() throws Exception {
        this.inventory = inventory.getInstance();
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void getInstance() {
        // a test that checks the creation of the inventory.
        // if the inventory created successfully then it should not be null.
        assertNotNull("should not be null", inventory.getInstance());
    }

    @Test
    public void load() {
        // a test that checks that if the books array is empty then it should returns
        // on take that the book is not in stock, if it does that means that the load
        // worked correctly.
        BookInventoryInfo[] books = new BookInventoryInfo[3];
        inventory.load(books);
        assertEquals(OrderResult.NOT_IN_STOCK,inventory.take("Harry Poter 2"));

        // a test that checks that if the books array is full then it should returns
        // on take with a specific book that the book is successfully taken, if it does that .
        // means that the load worked correctly.
        books[0]= new BookInventoryInfo("Harry Poter 2",2,90);
        books[1]= new BookInventoryInfo("Alice in wonderland",3,50);
        books[2]= new BookInventoryInfo("How to cook",1,60);
        inventory.load(books);
        assertEquals(OrderResult.SUCCESSFULLY_TAKEN,inventory.take("Harry Poter 2"));
    }

    @Test
    public void take() {
        // a test that checks that if the books array is empty then it should returns
        // on take that the book is not in stock.
        BookInventoryInfo[] books = new BookInventoryInfo[3];
        inventory.load(books);
        assertEquals(OrderResult.NOT_IN_STOCK,inventory.take("Harry Poter 2"));

        // a test that checks that if the books array is full then it should returns
        // on take with a specific book that the book is successfully taken.
        books[0]= new BookInventoryInfo("Harry Poter 2",2,90);
        books[1]= new BookInventoryInfo("Alice in wonderland",3,50);
        books[2]= new BookInventoryInfo("How to cook?",1,60);
        inventory.load(books);
        assertEquals(OrderResult.SUCCESSFULLY_TAKEN,inventory.take("How to cook"));

        // a test that checks that if the book was taken and it's quantity was over, then it should
        // not be taken anymore.
        assertEquals(OrderResult.NOT_IN_STOCK,inventory.take("How to cook?"));
    }

    @Test
    public void checkAvailabiltyAndGetPrice() {
        // a test that checks that if the books array is empty then it should returns
        // on checkAvailabiltyAndGetPrice that a specific book is not in stock (returns -1).
        BookInventoryInfo[] books = new BookInventoryInfo[3];
        inventory.load(books);
        assertEquals(-1,inventory.checkAvailabiltyAndGetPrice("Harry Poter 2"));
        // a test that checks that if the books array is full then it should returns
        // on checkAvailabiltyAndGetPrice with a specific book the price of the book.
        books[0]= new BookInventoryInfo("Harry Poter 2",2,90);
        books[1]= new BookInventoryInfo("Alice in wonderland",3,50);
        books[2]= new BookInventoryInfo("How to cook?",1,60);
        inventory.load(books);
        assertEquals(90,inventory.checkAvailabiltyAndGetPrice("Harry Poter 2"));
    }

    @Test
    public void printInventoryToFile() {
        // a test that checks that if the books array is empty then it should print
        // on printInventoryToFile an empty file.
        BookInventoryInfo[] books = new BookInventoryInfo[3];
        inventory.load(books);
        String stringName="path";
        inventory.printInventoryToFile(stringName);
        HashMap<String,Integer> hashMapFromFile1=readFromFile(stringName);
        HashMap<String,Integer> hashMapOrginal1=createHashMap(books);
        assertEquals(hashMapFromFile1,hashMapOrginal1);

        // a test that checks that if the books array is full then it should print
        // on printInventoryToFile the details of the hashmap that describes the inventory.
        books[0]= new BookInventoryInfo("Harry Poter 2",2,90);
        books[1]= new BookInventoryInfo("Alice in wonderland",3,50);
        books[2]= new BookInventoryInfo("How to cook?",1,60);
        inventory.load(books);
        inventory.printInventoryToFile(stringName);
        HashMap<String,Integer> hashMapFromFile2=readFromFile(stringName);
        HashMap<String,Integer> hashMapOriginal2=createHashMap(books);
        assertEquals(hashMapFromFile2,hashMapOriginal2);
    }

    /**
     * A private function that creates a hashmap.
     * @param booksArray - the array that needs to be convert to a hashmap
     * @return the hashmap that describes the inventory.
     */
    private HashMap<String,Integer> createHashMap(BookInventoryInfo[] booksArray){
        HashMap<String,Integer> hashMap = new HashMap<>();
        if(booksArray!=null){
            for(int i=0; i<booksArray.length; i++){
                if(booksArray[i]!=null)
                    hashMap.put(booksArray[i].getBookTitle(),booksArray[i].getAmountInInventory());
            }
        }
        return hashMap;
    }

    /**
     * A private function that reads a hashmap from a file
     * @param fileName - the path of the file
     * @return the hashmap that was describes in the file.
     */
    private HashMap<String,Integer> readFromFile(String fileName){
        HashMap<String,Integer> hashMap = new HashMap<>();
        ObjectInputStream objectInputStream = null;
        try{
            objectInputStream = new ObjectInputStream(new FileInputStream(fileName));
            hashMap = (HashMap<String,Integer>) objectInputStream.readObject();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            try{
                if (objectInputStream!=null) objectInputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return hashMap;
    }
}