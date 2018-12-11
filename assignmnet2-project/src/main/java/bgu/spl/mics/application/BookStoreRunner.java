package bgu.spl.mics.application;

import bgu.spl.mics.application.passiveObjects.Inventory;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.MapType;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
/** This is the Main class of the application. You should parse the input file,
 * create the different instances of the objects, and run the system.
 * In the end, you should output serialized objects.
 */
public class BookStoreRunner {

    private static Inventory _inventory;

    public static void main(String[] args) {





        if(args.length!=1||args[0] == null)             //change length!=5
        {
            System.out.println("not all required files are received as arguments!");
            System.exit(0);
        }
        System.out.println("got the file as argument");





        //read the json file
        readJsonFile(args[0]);
    }




private static void readJsonFile(String filePath) {

    ObjectMapper map=new ObjectMapper();
    MapType mapType=map.getTypeFactory().constructMapType(Map.class,String.class,Object.class);
    Map<String,Object> jsonStream=null;         //this object will hold the json object as a map of objects

    try {
        jsonStream=map.readValue(new File(filePath),mapType);   //get the json object into the map object
        System.out.println(jsonStream);
        //create Inventory object
        createInventory(jsonStream.get("initialInventory"));
    } catch (Exception e) {
        e.printStackTrace();
    }


}

    private static void createInventory(Object initialInventory) {

        _inventory=Inventory.getInstance();     //get instance of Inventory
        ArrayList<LinkedHashMap<String,Object>> inventoryArr;  //holds the books from read received object

        inventoryArr=(ArrayList<LinkedHashMap<String,Object>>)initialInventory;
        for(int i=0;i<inventoryArr.size();i++){

            LinkedHashMap<String,Object> internMap=inventoryArr.get(i);

                String bookTitle=(String) internMap.get("bookTitle");
                int amount=(int)internMap.get("amount");
                int price=(int)internMap.get("price");
                System.out.println(bookTitle+","+amount+","+price);

            }
        }


    }


