import javax.management.*;

/**
 * Created by gongp on 2017/10/19.
 */
public class B {

    public static void main(String[] args) throws Exception {
        AMonitor aMonitor = new AMonitor();
        MBeanServer server = MBeanServerFactory.createMBeanServer();
        ObjectName objectName = new ObjectName("objectName:id=serverMonitor");
        server.registerMBean(aMonitor,objectName);
        server.invoke(objectName,"start",null,null);
        Thread.sleep(1000);
        Object upTime = server.getAttribute(objectName, "UpTime");
        System.out.println(upTime);

    }


}

interface AMBean{
    public long getUpTime();
    public void start();
}
class AMonitor implements AMBean{

    public long startTime=0;

    @Override
    public long getUpTime() {
        return System.currentTimeMillis()-startTime;
    }

    @Override
    public void start() {
        startTime=System.currentTimeMillis();
    }
}