package bgu.spl.mics.application;

import bgu.spl.mics.application.passiveObjects.BookInventoryInfo;
import bgu.spl.mics.application.passiveObjects.DeliveryVehicle;
import bgu.spl.mics.application.passiveObjects.Inventory;
import bgu.spl.mics.application.passiveObjects.ResourcesHolder;
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

    private static Inventory _inventory;            //represents the inventory of the store
    private static ResourcesHolder _resources;      //hold the resources of the store as vehicles

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
        createInventory(jsonStream.get("initialInventory"));        //call for a method which init the inventory
        createResourcesHolder(jsonStream.get("initialResources"));



    } catch (Exception e) {
        e.printStackTrace();
    }


}

    /**
     * initialize the inventory
     * @param initialInventory the received initialInventory object
     */
    private static void createInventory(Object initialInventory) {


        ArrayList<BookInventoryInfo> bookInventoryInfos=new ArrayList<>();  //get the books in its correct type bookInv info
        _inventory=Inventory.getInstance();     //get/create instance of Inventory
        ArrayList<LinkedHashMap<String,Object>> inventoryArr;  //holds the books from read received object

        inventoryArr=(ArrayList<LinkedHashMap<String,Object>>)initialInventory; //cast back the initial inventory to its fit type

        for(int i=0;i<inventoryArr.size();i++){
           //for convenience
            LinkedHashMap<String,Object> internMap=inventoryArr.get(i);
                //get the values of the book from the hash table object
                String bookTitle=(String) internMap.get("bookTitle");
                int amount=(int)internMap.get("amount");
                int price=(int)internMap.get("price");
                //add as a new book object into arrayList
                bookInventoryInfos.add(new BookInventoryInfo(bookTitle,amount,price));

            }
        BookInventoryInfo[] booksArr=new BookInventoryInfo[bookInventoryInfos.size()];   //get the BookInventoryInfo type arr for loading in inventory
        bookInventoryInfos.toArray(booksArr);   //convert the arrayList into array of BookInventoryInfo

        _inventory.load(booksArr);  //load all the books into the Inventory

        }


    /**
     * initialize the resourceHolder
     * @param initialResources the received initialResources object
     */
    private static void createResourcesHolder(Object initialResources){

        ArrayList<DeliveryVehicle> resources=new ArrayList<>();  //get the vehicles in its correct type DeliveryVehicle
        _resources=ResourcesHolder.getInstance();     //get/create instance of ResourceHolder

        LinkedHashMap<String,Object> vehicles;  //jolds the map of vehicles
        //cast the initialResources to its actual type
        ArrayList<LinkedHashMap<String, Object>> cast=(ArrayList<LinkedHashMap<String, Object>>)initialResources;

        for(int j=0;j<cast.size();j++) {    //get the vehicles and add them to arrayList as delivery vehicles
            vehicles = cast.get(j);         //get bunch of resources (the for is only for case of adding mor resources of type vehicles
            for (int k = 0; k < vehicles.size(); k++) {     //run on array of DeliveryVehicle objects
                ArrayList<LinkedHashMap<String, Object>> vList; //for convenient
                vList = (ArrayList<LinkedHashMap<String, Object>>) vehicles.get("vehicles");    //get vehicle objects array
                if(vList!=null) {   //if there are vehicles
                    for (int i = 0; i < vList.size(); i++) {        //insert each vehicle as delivery vehicle in array list
                        //for convenience
                        LinkedHashMap<String, Object> internMap = vList.get(i);
                        //get the values of the vehicle from the hash table object
                        int license = (int) internMap.get("license");
                        int speed = (int) internMap.get("speed");
                        //add as a new vehicle object into arrayList
                        resources.add(new DeliveryVehicle(license, speed));

                    }
                }
            }
        }
        DeliveryVehicle[] DVArr=new DeliveryVehicle[resources.size()];   //get the DeliveryVehicle type arr for loading in resource holder
        resources.toArray(DVArr);   //convert the arrayList into array of DeliveryVehicles

        _resources.load(DVArr);  //load all the vehicles into the Inventory

        /* ////////////for tests only! remove this before submit
        for(int i=0;i<DVArr.length;i++){
            System.out.println(DVArr[i]);
        }*/

    }

    }


