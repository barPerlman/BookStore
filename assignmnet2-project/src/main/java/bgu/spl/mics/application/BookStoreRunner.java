package bgu.spl.mics.application;

import javax.xml.transform.sax.SAXSource;

/** This is the Main class of the application. You should parse the input file,
 * create the different instances of the objects, and run the system.
 * In the end, you should output serialized objects.
 */
public class BookStoreRunner {
    public static void main(String[] args) {

        if(args.length!=1||args[0] == null)             //change length!=5
        {
            System.out.println("not all required files are received as arguments!");
            System.exit(0);
        }
        System.out.println("got the file as argument");


    }
    //read the json file
    
}
