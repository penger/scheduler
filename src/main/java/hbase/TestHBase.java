package hbase;

import java.io.IOException;

import hbase.BarDatabase;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.HBaseConfiguration;

/**
 *   Command line application for testing the BarDatabase class.
 */
public class TestHBase {

    /**
     *   Imports CSV data given a BarDatabase instance, a filename 
     *   of a valid file containing stock price data, and a stock 
     *   symbol.   
     */
    private static void ImportCSVData(BarDatabase db, String filename, String symbol) {
    	System.out.println("Loading data for " + symbol + " from " + filename);
        try(LineReader reader = new LineReader(filename, db.GetDataImporter(symbol)))
        {
            reader.ProcessData();
        }
        catch(Exception e) {
            System.out.println("Caught exception while importing data:" + e.getMessage());
            e.printStackTrace();
        }   
    }

    /** 
     *   Main program
     */
    public static void main(String... args) throws IOException {

        // HBase context from configuration file
        Configuration config = HBaseConfiguration.create();
//        config.addResource(new Path(System.getenv("HBASE_CONF_DIR"),
//                    "hbase-site.xml"));


        config.addResource("hbase-site.xml");

        // Create a new instance of BarDatabase to be used as a DAO
        // for the stock price data
        BarDatabase db = new BarDatabase(config);

        // Drop and add the table for a clean test.
        // (Comment these out if you want to play with an existing table.)
        db.DropTable();
        db.CreateTable();

        // Import the example data from quandl
        ImportCSVData(db, "E:\\javawork\\z_bigdata\\src\\main\\java\\hbase\\FinData/GOOG-NYSE_ABT.csv", "ABT");
        ImportCSVData(db, "E:\\javawork\\z_bigdata\\src\\main\\java\\hbase\\FinData/GOOG-NYSE_BMY.csv", "BMY");
        ImportCSVData(db, "E:\\javawork\\z_bigdata\\src\\main\\java\\hbase\\FinData/GOOG-NYSE_MRK.csv", "MRK");
        ImportCSVData(db, "E:\\javawork\\z_bigdata\\src\\main\\java\\hbase\\FinData/GOOG-NYSE_PFE.csv", "PFE");

        // Get a single row
        String[] sResult = db.GetRow("2015-08-28", "BMY");
        System.out.print(sResult[0] + ",");
        System.out.print(sResult[1] + ",");
        System.out.print(sResult[2] + ",");
        System.out.print(sResult[3] + ",");
        System.out.print(sResult[4] + ",");
        System.out.println(sResult[5]);

        // Get a single cell
        System.out.println("BMY 8/28 close is " +
        db.GetCell("2015-08-28", "BMY", BarDatabase.COL_CLOSE));

        // Iterate over many rows
        db.ScanRows("2015-08-17", "BMY", 10, new BarDatabase.ScanPrinter());

    }
}
