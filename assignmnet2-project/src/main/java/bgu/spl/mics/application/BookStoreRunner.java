package bgu.spl.mics.application;

import bgu.spl.mics.application.passiveObjects.*;
import bgu.spl.mics.application.services.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.MapType;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
/** This is the Main class of the application. You should parse the input file,
 * create the different instances of the objects, and run the system.
 * In the end, you should output serialized objects.
 */
public class BookStoreRunner {

    private static Inventory _inventory;            //represents the inventory of the store
    private static ResourcesHolder _resources;      //hold the resources of the store as vehicles
    private static TimeService _timeService;        //time service
    private static ArrayList<Runnable> _servicesToRun;   //holds the tunnable services that will become a task to run as thread
    private static ArrayList<Thread> _runThreads;       //services as threads
    private static HashMap<Integer,Customer> _customerRefsHM;   //hash map with key id and value is the customers in store references.
    private static MoneyRegister _moneyRegister;    //get the instance of the money register

    public static void main(String[] args) {

        if(args.length<5||args[0] == null)             //change length!=5
        {
            System.out.println("not all required files are received as arguments!");
            System.exit(0);
        }

        //create the money Register
        _moneyRegister=MoneyRegister.getInstance();
        //read the json file
        readJsonFile(args[0]);

       _runThreads=new ArrayList<>();      //init thread list




        for(int i=0;i<_servicesToRun.size();i++){
            Thread thread=new Thread(_servicesToRun.get(i));
            _runThreads.add(thread);
            thread.start();
        }
        Thread t=new Thread(_timeService);     //run time service after all services are up
        t.start();
        //wait till all of the threads are finished
        for(int i=0;i<_runThreads.size();i++){
            try {
                _runThreads.get(i).join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        try {
            t.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("got here fuck you");
       outputToFiles(args[1],args[2],args[3],args[4]);


    }


private static void outputToFiles(String customerFile,String booksFile,String receiptsFile,String moneyRegObjectFile){

        //create a customer file with hash map of customers
    try{
        FileOutputStream fos =new FileOutputStream(customerFile);
        ObjectOutputStream oos=new ObjectOutputStream(fos);
        oos.writeObject(_customerRefsHM);
        fos.close();
        oos.close();
    }catch(IOException e){
        e.printStackTrace();
    }


    //create the books in inventory status file
    _inventory.printInventoryToFile(booksFile);

    //create the order receipts file
    _moneyRegister.printOrderReceipts(receiptsFile);

    //print the moneyRegister object to a file
    try{
        FileOutputStream fos =new FileOutputStream(moneyRegObjectFile);
        ObjectOutputStream oos=new ObjectOutputStream(fos);
        oos.writeObject(_moneyRegister);
        fos.close();
        oos.close();
    }catch(IOException e){
        e.printStackTrace();
    }



}

private static void readJsonFile(String filePath) {

    ObjectMapper map=new ObjectMapper();
    MapType mapType=map.getTypeFactory().constructMapType(Map.class,String.class,Object.class);
    Map<String,Object> jsonStream=null;         //this object will hold the json object as a map of objects

    try {
        jsonStream=map.readValue(new File(filePath),mapType);   //get the json object into the map object
        //create Inventory object
        createInventory(jsonStream.get("initialInventory"));        //call for a method which init the inventory
        createResourcesHolder(jsonStream.get("initialResources"));
        createServices(jsonStream.get("services"));


    } catch (Exception e) {
        e.printStackTrace();
    }


}

    /**
     * this method responsible for creating all of the store services that has been sent in the json execution file
     * @param services  the received services
     */
    private static void createServices(Object services) {
       LinkedHashMap<String,Object> castServices=(LinkedHashMap<String,Object>)services;
       createTimeService(castServices.get("time"));
       _servicesToRun=new ArrayList<>();            //init the runable services list
       createSellingServices(castServices.get("selling"));  //create the required selling Services
        createInventoryServices(castServices.get("inventoryService"));  //create the required inventory Services
        createLogisticsServices(castServices.get("logistics"));  //create the required logistics Services
        createResourcesServices(castServices.get("resourcesService"));  //create the required resourcesService Services
        createApiServices(castServices.get("customers"));


    }

    /**
     * create the API services
     * @param customers each item of customers is argument for the web api constructor
     */
    private static void createApiServices(Object customers){
        ArrayList<LinkedHashMap<String,Object>> customersCast;
        customersCast=(ArrayList<LinkedHashMap<String,Object>>)customers;   //cast the 'customers' to its original type
        _customerRefsHM=new HashMap<>();    //initialize the customers in store hashmap
        for(int i=0;i<customersCast.size();i++){                            //get the api services's arguments

            //get the customer i details
            LinkedHashMap<String,Object> internMap=customersCast.get(i);    //current service data
            int id=(int)internMap.get("id");
            String name=(String)internMap.get("name");
            String address=(String)internMap.get("address");
            int distance=(int)internMap.get("distance");
            //get the object consist the credit card details
            LinkedHashMap<String,Object> creditCardDetails=(LinkedHashMap<String,Object>)internMap.get("creditCard");
            int cNumber=(int)creditCardDetails.get("number");
            int availAmount=(int)creditCardDetails.get("amount");
            //get the object consist the order schedule details
            ArrayList<LinkedHashMap<String,Object>> orderSchedule=(ArrayList<LinkedHashMap<String,Object>>)internMap.get("orderSchedule");
            ArrayList<OrderPair> orderScheduleList=new ArrayList<>();   //list of orderSchedule for api service constructor
            //manipulate the order schedule data
            for(int j=0;j<orderSchedule.size();j++){    //create order pairs and add them to list. will be sent to the api constructor
                LinkedHashMap<String,Object> internOrder=orderSchedule.get(j);  //current order schedule data
                String bookTitle=(String)internOrder.get("bookTitle");
                Integer startTick=(Integer)internOrder.get("tick");
                OrderPair orderPair=new OrderPair(bookTitle,startTick); //create order pair
                orderScheduleList.add(orderPair);   //add the pair to order list
            }

            //build the customer from the data above
            Customer custom=new Customer(id,name,address,distance,cNumber,availAmount);
            //add the customer to the hash map of customers
            _customerRefsHM.putIfAbsent(new Integer(id),custom);
            //send all processed arguments to the web api constructor and create it
            Runnable service=new APIService(custom,orderScheduleList);
            _servicesToRun.add(service);
        }
    }

    /**
     * create time service
     * @param time the received time data to initialize with
     */
    private static void createTimeService(Object time) {
        LinkedHashMap<String,Object>timeCast;
        timeCast= (LinkedHashMap<String,Object>)time;
        int speed=(int)timeCast.get("speed");
        int duration=(int)timeCast.get("duration");
        _timeService=new TimeService(speed,duration);
    }
//////////////////those are methods to create runnable store services////////////////////////////////////
    private static void createSellingServices(Object selling){
        int numOfSellingServices=(int)selling;
        for(int i=0;i<numOfSellingServices;i++){
            Runnable service=new SellingService("selling"+(i+1));
            _servicesToRun.add(service);
        }
    }

    private static void createInventoryServices(Object inventory){
        int numOfInventoryServices=(int)inventory;
        for(int i=0;i<numOfInventoryServices;i++){
            Runnable service=new InventoryService("inventory"+(i+1));
            _servicesToRun.add(service);
        }
    }

    private static void createLogisticsServices(Object logistic){
        int numOfInventoryServices=(int)logistic;
        for(int i=0;i<numOfInventoryServices;i++){
            Runnable service=new LogisticsService("logistics"+(i+1));
            _servicesToRun.add(service);
        }
    }
    private static void createResourcesServices(Object resource){
        int numOfInventoryServices=(int)resource;
        for(int i=0;i<numOfInventoryServices;i++){
            Runnable service=new ResourceService("resources"+(i+1));
            _servicesToRun.add(service);
        }
    }
/////////////////////////////////////////////////////////////////////////////////////////////
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


