import com.kyf.client.server.ServerList;
import com.kyf.client.utils.ZooKeeperUtils;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;

import java.util.List;

public class ClientBootStrap {
    public static void main(String[] args) {
        String applicationName  = "/server" ;
        String connectString    = "192.168.30.19:2181";
        int sessionTimeout      = 20000;
        Watcher watcher         = new Watcher() {
            @Override
            public void process(WatchedEvent event) {
                ServerList.flushServerlist();
            }};

        ZooKeeperUtils.initZooKeeperCfg(connectString, sessionTimeout, watcher);
        ZooKeeper zk = ZooKeeperUtils.getZooKeeper();
        try {
           if(zk.exists(applicationName, true) != null){
               List<String> serverList = zk.getChildren(applicationName,true);
               if(serverList != null && serverList.size() > 0){
                   ServerList.init(applicationName, serverList);
               } else {
                   System.err.println("not find server list");
               }
               serverList.forEach(System.out::println);
           } else {
               System.err.println("not find server");
               System.exit(-1);
           }
        } catch (KeeperException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

       synchronized (ClientBootStrap.class) {
            while (true) {
                try {
                    System.out.println("client run finished!");
                    ClientBootStrap.class.wait();
                } catch (Exception e) {
                    e.printStackTrace();
                    System.exit(-1);
                }
            }
        }
    }
}
