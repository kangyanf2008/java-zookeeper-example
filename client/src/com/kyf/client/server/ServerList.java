package com.kyf.client.server;

import com.kyf.client.bean.ServerBean;
import com.kyf.client.utils.ZooKeeperUtils;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class ServerList {
    //服务应用名
    private static String applicationName;
    //服务地址列表
    public static volatile List<ServerBean> serverList = new ArrayList<ServerBean>();
    private static AtomicInteger serverNum = new AtomicInteger(0);
    private static AtomicInteger robinIndex = new AtomicInteger(0);
    private static Stat stat = new Stat();
    //初始化类参数
    public static void init(String applicationName) {
        ServerList.applicationName = applicationName;
        flushServerlist();
    }

    //刷新服务列表
    public static void flushServerlist(){
        try {
            ZooKeeper zk = ZooKeeperUtils.getZooKeeper();
            if(zk.exists(applicationName, true) != null){
                List<String> serverList = zk.getChildren(applicationName,true);
               if(serverList != null && serverList.size() > 0){
                   List<ServerBean> serverListT =  new ArrayList<ServerBean>();

                   serverList.forEach(s->{
                       //服务进行监听上下线
                    /*
                       try {
                           //System.out.println(zk.getData(applicationName+"/"+s,ZooKeeperUtils.watcher,stat));
                           zk.getData(applicationName+"/"+s,ZooKeeperUtils.watcher,stat);
                       } catch (KeeperException e) {
                           e.printStackTrace();
                       } catch (InterruptedException e) {
                           e.printStackTrace();
                       }
                       */
                       //存放服务列表
                       serverListT.add(new ServerBean(s,new AtomicInteger(0)));
                   });
                   synchronized (ServerList.serverList){
                       //更新服务列表
                       ServerList.serverList = serverListT;
                       //更新服务器数量
                       ServerList.serverNum = new AtomicInteger(ServerList.serverList.size());
                       if(ServerList.robinIndex.intValue() > ServerList.serverNum.intValue() - 1){
                           ServerList.robinIndex = new AtomicInteger(0);
                       }
                   } // end synchronized
                   System.out.println("=====================刷新服务列表=============================");
               } else {
                   System.err.println("not find server list");
                   //System.exit(-1);
               }
            } else {
                System.err.println("not find server list");
               // System.exit(-1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //踢掉不可用的服务
    public static void removeServer(ServerBean serverBean){
        synchronized ( ServerList.serverList) {
            ServerList.serverList.remove(serverBean);
            ServerList.serverNum = new AtomicInteger(ServerList.serverList.size());
            if(ServerList.robinIndex.intValue() > ServerList.serverNum.intValue() - 1){
                ServerList.robinIndex = new AtomicInteger(0);
            }
        }
    }

    //获取调用服务器信息
    public static  ServerBean robinServerAddress() {
        ServerBean serverBean = null;
        //先使用后累加
        int index = ServerList.robinIndex.getAndIncrement();
        //判断是否大小总服务器数。假如大于。则取会下标越界
        boolean status = true;
        while (status) {
            if (index > ServerList.serverNum.intValue() - 1 && ServerList.serverNum.intValue() > 0) {
                //更新轮询下标值
                synchronized (ServerList.robinIndex) {
                    if (ServerList.robinIndex.intValue() > ServerList.serverNum.intValue() - 1) {
                        ServerList.robinIndex = new AtomicInteger(0);
                    }
                    index = ServerList.robinIndex.getAndIncrement();
                } // end synchronized

            } else {
                status = false;
            }// end if
        }// end while

        if(index < ServerList.serverNum.intValue()){
            serverBean = serverList.get(index);
            if (serverBean != null) {
    System.out.println(serverBean);
                return serverBean;
            } else {
                System.err.println("robinIndex " + index + " 未找到服务器信息");
                flushServerlist();//刷新服务列表
            }
        }

        //判断服务器列表是否为空
        index = ServerList.robinIndex.getAndIncrement();
        if(index < ServerList.serverNum.intValue()) {
            serverBean = serverList.get(index);
        }
        //更新轮询下标值
        synchronized (ServerList.robinIndex) {
            if (ServerList.robinIndex.intValue() > ServerList.serverNum.intValue() - 1) {
                ServerList.robinIndex = new AtomicInteger(0);
            }
        }
        return serverBean;

    }



    public static void main(String[] args) {
        System.out.println(ServerList.robinIndex.getAndIncrement());
        System.out.println(ServerList.robinIndex.getAndIncrement());

    }

}