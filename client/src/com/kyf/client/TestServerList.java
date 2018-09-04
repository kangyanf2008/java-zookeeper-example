package com.kyf.client;

import com.kyf.client.entiy.ServerEntiy;
import com.kyf.client.server.ServerList;
import com.kyf.client.utils.ZooKeeperUtils;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;

public class TestServerList {
    public static void main(String[] args) {
        String applicationName  = "/server" ;
        String connectString    = "192.168.30.19:2181";
        int sessionTimeout      = 20000;
        int tryNum              = 3; //重试次数

        Watcher watcher         = new Watcher() {
            @Override
            public void process(WatchedEvent event) {
                //当服务下线，或者上限。则进行刷新负载列表
                if (event.getType() == Event.EventType.NodeChildrenChanged) {
                    System.out.println(event.getState() + "   "+ event.getPath() + "  "+ event.getType());
                    ServerList.flushServerlist();
                }
                if (event.getType() == Event.EventType.NodeDeleted || event.getType() == Event.EventType.NodeCreated ){
                    System.out.println("else "+event.getState() + "   "+ event.getPath() + "  "+ event.getType());
                }
            }};// end watcher

        //zk初始化
        ZooKeeperUtils.initZooKeeperCfg(connectString, sessionTimeout, watcher);
        ZooKeeper zk = ZooKeeperUtils.getZooKeeper();
        //服务初始化列表
        ServerList.init(applicationName);

        for(int i=0; i<200;i++){
            new Thread(new Runnable() {
                @Override
                public void run() {
                    int i = 1;
                    while(true){
                        if(i%10==0){
                            try {
                                System.out.println(Thread.currentThread().getId()+"   sleep 100");
                                Thread.sleep(100L);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                        i++;
                        ServerEntiy serverEntiy = ServerList.robinServerAddress();
                        System.out.println(Thread.currentThread().getId()+"   "+serverEntiy);
                    }
                }
            }).start();
        }


        synchronized (TestServerList.class) {
            while (true) {
                try {
                    System.out.println("client run finished!");
                    TestServerList.class.wait();
                } catch (Exception e) {
                    e.printStackTrace();
                    System.exit(-1);
                }
            }
        }
    }
}
