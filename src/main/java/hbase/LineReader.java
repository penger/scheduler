package hbase;

import hbase.DataReader;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.BufferedReader;

/**
 *   Reads through a text file line by line and invokes a method on 
 *   a callback interface on each line.
 */
public class LineReader implements DataReader {

    //  Data members
    private final String filename;
    private DataCallback callback;
    
    /**
     *     Opens a file, and invokes the callback interface line by line
     */
    @Override
    public void ProcessData() throws FileNotFoundException, IOException {
        if ( filename == null || callback == null ) return;
        File f = new File(filename);
        if ( !f.exists() || f.isDirectory() ) return;
        
        try (BufferedReader reader = new BufferedReader(new FileReader(f))) {
            String line;                    
            while ( (line = reader.readLine()) != null ) {
            	System.out.println(line);
                callback.ProcessData(line);
            }           
        }
    }

    /**
     *     Set the callback interface
     */
    @Override
    public void setRecordCallback(DataCallback cback) {
        callback = cback;
    }

    /**
     *      On close, execute close on the callback interface
     */
    @Override
    public void close() throws Exception {
        if ( callback != null ) callback.close();
    }

    /**
     *       C'tor
     */
    public LineReader(String inFilename, DataCallback inCallback) {
        filename = inFilename;
        callback = inCallback;
    }
}
