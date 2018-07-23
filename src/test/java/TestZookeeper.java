/**
 * Created by gongp on 2017/8/7.
 */
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;

import java.io.IOException;

public class TestZookeeper implements Watcher{
    ZooKeeper zk=null;

    public TestZookeeper(ZooKeeper zk) throws IOException {
        if(zk == null){
            zk = new ZooKeeper("192.168.211.143:2181",3000,this);
        }
    }

    @Override
    public void process(WatchedEvent watchedEvent) {
        watchedEvent.getPath();
    }
}
