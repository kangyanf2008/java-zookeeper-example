package com.kyf.client.server;

import com.kyf.client.utils.ZooKeeperUtils;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooKeeper;

import java.util.ArrayList;
import java.util.List;

public class ServerList {
    //服务应用名
    private static String applicationName;
    //服务地址列表
    private static List<String> serverList = new ArrayList<String>();

    //初始化类参数
    public static void init(String applicationName, List<String>  serverList) {
        ServerList.applicationName = applicationName;
        ServerList.serverList = serverList;
    }

    //刷新服务列表
    public static boolean flushServerlist(){
        try {
            ZooKeeper zk = ZooKeeperUtils.getZooKeeper();
            if(zk.exists(applicationName, true) != null){
                List<String> serverList = zk.getChildren(applicationName,true);
               if(serverList != null && serverList.size() > 0){
                    serverList.forEach(System.out::println);
               } else {
                   System.err.println("not find server list");
                   System.exit(-1);
               }
            } else {
                System.err.println("not find server list");
                System.exit(-1);
            }
        } catch (KeeperException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return true;
    }

}
