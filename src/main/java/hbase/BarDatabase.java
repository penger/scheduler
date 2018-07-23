package hbase;

import org.apache.hadoop.fs.Path;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HConstants;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Admin;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.filter.PageFilter;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.ConnectionFactory;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.io.compress.Compression.Algorithm;

/**
 *   HBase data access class for Stock bar data.
 *     -Defines column names for daily volume, open, high, low, 
 *      and closing prices
 *     -Defines the row key structure
 *     -Methods to create schema, import data from CSV and query 
 */
public class BarDatabase  {

    // Schema Constants - table, column family and column names
    public final static byte[] TABLE_NAME = "BarData".getBytes();
    public final static byte[] COLUMN_FAMILY = "d".getBytes();  
    public final static byte[] COL_OPEN = "open".getBytes();
    public final static byte[] COL_HIGH = "high".getBytes();
    public final static byte[] COL_LOW = "low".getBytes();
    public final static byte[] COL_CLOSE = "close".getBytes();
    public final static byte[] COL_VOLUME = "volume".getBytes();

    // HBase configuration 
    private final Configuration config;

    /**
     *     Creates a rowkey from date and stock name
     */
    private byte[] makeKey(String date, String stock) {
        // Optimized so that stocks are close to each other lexicographically
        return (stock + date).getBytes();
    }
    
    /**
     *    Creates a new importer object scoped to this DAO which can 
     *    accept text input and flush to the underlying HBase instance.
     *    (LineImporter defined below)
     */
    public DataCallback GetDataImporter(String symbol) {
        return new LineImporter(symbol);
    }

    /**
     *    Gets a single row given the date and stock symbol
     */
    public String[] GetRow(String date, String symbol) throws IOException {
        try (Connection conn = ConnectionFactory.createConnection(config)){
            // Get the table 
            Table table = conn.getTable(TableName.valueOf(TABLE_NAME));
            // Construct a "getter" with the rowkey. 
            Get get = new Get(makeKey(date, symbol));
            // Get the result by passing the getter to the table
            Result r = table.get(get);
            // return the results
            if ( r.isEmpty() ) return null;
            String[] retval = new String[6];
            retval[0] = new String(r.getRow());  // Gets rowkey from the record 
                                                 // for validation
            retval[1] = new String(r.getValue(COLUMN_FAMILY, COL_OPEN));
            retval[2] = new String(r.getValue(COLUMN_FAMILY, COL_HIGH));
            retval[3] = new String(r.getValue(COLUMN_FAMILY, COL_LOW));
            retval[4] = new String(r.getValue(COLUMN_FAMILY, COL_CLOSE));
            retval[5] = new String(r.getValue(COLUMN_FAMILY, COL_VOLUME));
            return retval;
        }
    }

    /**
     *    Gets a single cell given the date and stock symbol and column ID
     */
    public String GetCell(String date, String symbol, byte[] column) 
            throws IOException {
        try (Connection conn = ConnectionFactory.createConnection(config)){
            // Get the table
            Table table = conn.getTable(TableName.valueOf(TABLE_NAME));
            // Construct a "getter" with the rowkey. 
            Get get = new Get(makeKey(date, symbol));
            // Further refine the "get" with a column specification
            get.addColumn(COLUMN_FAMILY, column);
            // Get the result by passing the getter to the table
            Result r = table.get(get);
            // return the results
            if ( r.isEmpty() ) return null;
            // Gets the value of the first (and only) column
            return new String(r.value());
        }
    }

    /** 
     *    Specifies a range of rows to retrieve based on a starting row key
     *    and retrieves up to limit rows.  Each row is passed to the supplied
     *    DataScanner.
     */
    public void ScanRows(String startDate, String symbol, 
             int limit, DataScanner scanner) throws IOException {
        ResultScanner results = null;
        try (Connection conn = ConnectionFactory.createConnection(config)){
            // Get the table
            Table table = conn.getTable(TableName.valueOf(TABLE_NAME));
            // Create the scan
            Scan scan = new Scan();
            // start at a specific rowkey. 
            scan.setStartRow(makeKey(startDate, symbol));
            // Tell the server not to cache more than limit rows 
            // since we won;t need them
            scan.setCaching(limit);
            // Can also set a server side filter
            scan.setFilter(new PageFilter(limit));
            // Get the scan results
            results = table.getScanner(scan);
            // Iterate over the scan results and break at the limit
            int count = 0;
            for ( Result r : results ) {
                scanner.ProcessRow(r);
                if ( count++ >= limit ) break;
            }
        }
        finally {
            // ResultScanner must be closed.
            if ( results != null ) results.close();         
        }
    }

    /**
     *    Creates the HBase table 
     */
    public void CreateTable() throws IOException {
        try (Connection connection = ConnectionFactory.createConnection(config);
             Admin admin = connection.getAdmin()) {

            HTableDescriptor table = 
                new HTableDescriptor(TableName.valueOf(TABLE_NAME));
            table.addFamily(new HColumnDescriptor(COLUMN_FAMILY));

            if (!admin.tableExists(table.getTableName())) {
                System.out.print("Creating table. ");
                admin.createTable(table);
                System.out.println(" Done.");
            }
        }
    }
    
    /**
     *     Drops the HBase table
     */
    public void DropTable() throws IOException {
        try (Connection connection = ConnectionFactory.createConnection(config);
             Admin admin = connection.getAdmin()) {

            HTableDescriptor table =
                new HTableDescriptor(TableName.valueOf(TABLE_NAME));
            table.addFamily(new HColumnDescriptor(COLUMN_FAMILY));
            
            if (admin.tableExists(table.getTableName())) {
                System.out.print("Dropping table. ");
                // a table must be disabled before it can be dropped
                admin.disableTable(table.getTableName());
                admin.deleteTable(table.getTableName());
                System.out.println(" Done.");
            }
        }
    }

    /**
     *       C'tor
     */     
    public BarDatabase(Configuration inConfig) {
        config = inConfig;
    }

    /**
     *    Internal class to do the work of processing a CSV line
     *    into an HBase Put object.
     */
    private class LineImporter implements DataCallback {

        // Data members
        private final String currentStock;      // current stock symbol
        private final List<Put> currentImport;  // cache for bulk load
        private boolean skipFirst;  // set true if there is a header

        /**
         *    Processes a CSV line from the data source 
         */
        @Override
        public void ProcessData(String line) throws IOException {
            if ( line == null ) return;
            // If true, skip the header
            if ( skipFirst ) {
                skipFirst = false;
                return;
            }
            
            // Split the line.  
            String[] data = line.split(","); 
            if ( data.length != 6 ) return;

            // Construct a "put" object for insert
            Put p = new Put(makeKey(data[0], currentStock));
            p.addColumn(COLUMN_FAMILY, COL_OPEN, data[1].getBytes());
            p.addColumn(COLUMN_FAMILY, COL_HIGH, data[2].getBytes());
            p.addColumn(COLUMN_FAMILY, COL_LOW, data[3].getBytes());
            p.addColumn(COLUMN_FAMILY, COL_CLOSE, data[4].getBytes());
            p.addColumn(COLUMN_FAMILY, COL_VOLUME, data[5].getBytes());
            
            // Cache the "put" object for bulk load.
            currentImport.add(p);
        }

        /**
         *    Imports bulk data into HBase table
         */
        @Override
        public void close() throws Exception {
            if ( currentImport.isEmpty() ) return;
            try (Connection conn = ConnectionFactory.createConnection(config)) {
                Table table = conn.getTable(TableName.valueOf(TABLE_NAME));
                table.put(currentImport);
                table.close();
            } 
            finally {
                currentImport.clear();
            }
        }

        /**
         *     C'tor 
         */
        public LineImporter(String inSymbol, boolean inSkipFirst)  {
            skipFirst = inSkipFirst;
            currentStock = inSymbol;
            currentImport = new ArrayList<Put>();
        }

        /**
         *     C'tor 
         */
        public LineImporter(String inSymbol) {
            this(inSymbol, true);  // Default assumes header
        }
    }

    /**
     *      A scan row processor that prints the row out to stdout
     */
    public static class ScanPrinter implements DataScanner {

        @Override
        public void close() throws Exception {}

        @Override
        public void ProcessRow(Result r) {
            System.out.print(new String(r.getRow()) + ",");
            System.out.print(new String(r.getValue(COLUMN_FAMILY, COL_OPEN)) + ",");
            System.out.print(new String(r.getValue(COLUMN_FAMILY, COL_HIGH)) + ",");
            System.out.print(new String(r.getValue(COLUMN_FAMILY, COL_LOW)) + ",");
            System.out.print(new String(r.getValue(COLUMN_FAMILY, COL_CLOSE)) + ",");
            System.out.println(new String(r.getValue(COLUMN_FAMILY, COL_VOLUME)));
        }
        
    }

}
