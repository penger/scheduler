package hbase;

import org.apache.hadoop.hbase.client.Result;

/**
 *   Processes row data from an HBase scan query.
 *    (AutoCloseable so that it can be used in a try() {} expression)
 */
public interface DataScanner extends AutoCloseable {
    void ProcessRow(Result row);
}
