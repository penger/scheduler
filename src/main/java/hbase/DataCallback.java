package hbase;

import java.io.IOException;

/**
 *   Imports data into HBase, assumes string data contains one entire row
 *    (AutoCloseable so that it can be used in a try() {} expression)
 */
public interface DataCallback extends AutoCloseable {
    void ProcessData(String record) throws IOException;
}
