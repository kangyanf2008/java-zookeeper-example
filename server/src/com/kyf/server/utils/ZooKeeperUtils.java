package com.kyf.server.utils;

import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;

import java.io.IOException;

public class ZooKeeperUtils {
    private static String connectString;                  //zk服务器列表。多个用英文“,”逗号隔开 92.168.100.128:2181, 192.168.100.128:2182, 192.168.100.128:2183
    private static int   sessionTimeout;                 //session超时时间
    private static Watcher watcher;

    private ZooKeeperUtils(){};

    //获取zk实例静态内部类
    private static class  ZooKeeperInstance {
        public static ZooKeeper zooKeeper;
        static {
            try {
                zooKeeper = new ZooKeeper(connectString, sessionTimeout , watcher);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 初始化zookeeper连接参数
     * @param connectString
     * @param sessionTimeout
     * @param watcher
     */
    public static void initZooKeeperCfg(String connectString, int sessionTimeout, Watcher watcher) {
        ZooKeeperUtils.connectString = connectString;
        ZooKeeperUtils.sessionTimeout = sessionTimeout;
        ZooKeeperUtils.watcher = watcher;
    }
    public static ZooKeeper getZooKeeper(){
        return ZooKeeperInstance.zooKeeper;
    }

}
