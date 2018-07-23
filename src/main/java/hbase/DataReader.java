package hbase;

import java.io.IOException;

/**
 *   Reads data from a source record by record and calls the callback 
 *   on each record.
 */
public interface DataReader extends AutoCloseable {
    void setRecordCallback(DataCallback callback);  
    void ProcessData() throws IOException;
}
