import com.kyf.client.entiy.ServerEntiy;
import com.kyf.client.error.TimeOutException;
import com.kyf.client.server.ServerList;
import com.kyf.client.utils.HttpClientUtil;
import com.kyf.client.utils.ZooKeeperUtils;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;

import java.util.concurrent.atomic.AtomicInteger;

public class ClientBootStrap {
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

        //启动线程调用服务 ===============beging
/*
        new Thread(new Runnable() {
            @Override
            public void run() {
                int i=0;
                ServerEntiy serverBean = ServerList.robinServerAddress();
                //处理是否成功标记
                boolean resutlSucess = false;
                //服务器列表为空，则结束执行
                while(!resutlSucess && serverBean != null){
                    for( int j=0; j < tryNum ; j++) {
                        try {
                            String result = HttpClientUtil.get("http://"+serverBean.serverAddress  + "?data="+i, null);
                            i++;
                            resutlSucess = true;
                            //System.out.println("thread1 "+serverBean +"_" + result+"\t");
                        } catch (TimeOutException e){
                            serverBean.setFailureTimes(new AtomicInteger(serverBean.getFailureTimes().intValue()+1));
                        }
                    }// end if
                    //踢掉不可用的服务
                    ServerList.removeServer(serverBean);
                    //重新获取服务器地址信息
                    serverBean =  ServerList.robinServerAddress();
                }// end while
                System.out.println(ServerList.serverList);
            }
        }).start();
*/
/*
        new Thread(new Runnable() {
            @Override
            public void run() {
                for(int i=0; i< 100; i++){
                    ServerEntiy serverEntiy = ServerList.robinServerAddress();
                    String result = HttpClientUtil.get2("http://"+ serverEntiy.serverAddress  + "?data="+i, null);
                   // System.out.println("thread2 "+ serverEntiy +"_" + result+"\t");
                }
            }
        }).start();
        new Thread(new Runnable() {
            @Override
            public void run() {
                for(int i=0; i< 100; i++){
                    ServerEntiy serverEntiy = ServerList.robinServerAddress();
                    String result = HttpClientUtil.get2("http://"+ serverEntiy.serverAddress  + "?data="+i, null);
                    // System.out.println("thread2 "+ serverEntiy +"_" + result+"\t");
                }
            }
        }).start();
        new Thread(new Runnable() {
            @Override
            public void run() {
                for(int i=0; i< 100; i++){
                    ServerEntiy serverEntiy = ServerList.robinServerAddress();
                    String result = HttpClientUtil.get2("http://"+ serverEntiy.serverAddress  + "?data="+i, null);
                    // System.out.println("thread2 "+ serverEntiy +"_" + result+"\t");
                }
            }
        }).start();
        new Thread(new Runnable() {
            @Override
            public void run() {
                for(int i=0; i< 100; i++){
                    ServerEntiy serverEntiy = ServerList.robinServerAddress();
                    String result = HttpClientUtil.get2("http://"+ serverEntiy.serverAddress  + "?data="+i, null);
                   // System.out.println("thread3 "+ serverEntiy +"_" + result+"\t");
                }
            }
        }).start();
*/

        //三个线程测试服务异常后，路踢掉异常服务
        new Thread(new Runnable() {
            @Override
            public void run() {
                int i = 0;
                while (true) {
                    try {
                        //处理是否成功标记
                        boolean resutlSucess = false;
                        ServerEntiy serverEntiy = ServerList.robinServerAddress();
                        //讲行服务调用，接口访问超时进行重试操作
                        for( int j=0; j < tryNum ; j++) {
                            String result = "";
                            try {
                                result = HttpClientUtil.get2("http://" + serverEntiy.serverAddress + "?data=" + i, null);
                                resutlSucess = true;
                            } catch (TimeOutException e){
                                //失败后，进行服务失败次数累加
                                serverEntiy.setFailureTimes(new AtomicInteger(serverEntiy.getFailureTimes().intValue()+1));
                            }
                            //成功之后 打印返回结果
                            if(resutlSucess){
                                System.out.println(serverEntiy +"_" + result+"\t");
                                break;
                            }
                        }// end if

                        //重试失败后。路踢掉不可用服务
                        if(!resutlSucess){
                            ServerList.removeServer(serverEntiy);
                        } else {
                            i++;
                        }
                        Thread.sleep(1000L);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            }
        }).start();

        new Thread(new Runnable() {
            @Override
            public void run() {
                int i = 0;
                while (true) {
                    try {
                        //处理是否成功标记
                        boolean resutlSucess = false;
                        ServerEntiy serverEntiy = ServerList.robinServerAddress();
                        //讲行服务调用，接口访问超时进行重试操作
                        for( int j=0; j < tryNum ; j++) {
                            String result = "";
                            try {
                                result = HttpClientUtil.get2("http://" + serverEntiy.serverAddress + "?data=" + i, null);
                                resutlSucess = true;
                            } catch (TimeOutException e){
                                //失败后，进行服务失败次数累加
                                serverEntiy.setFailureTimes(new AtomicInteger(serverEntiy.getFailureTimes().intValue()+1));
                            }
                            //成功之后 打印返回结果
                            if(resutlSucess){
                                System.out.println(serverEntiy +"_" + result+"\t");
                                break;
                            }
                        }// end if

                        //重试失败后。路踢掉不可用服务
                        if(!resutlSucess){
                            ServerList.removeServer(serverEntiy);
                        } else {
                            i++;
                        }
                        Thread.sleep(1000L);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            }
        }).start();

        new Thread(new Runnable() {
            @Override
            public void run() {
                int i = 0;
                while (true) {
                    try {
                        //处理是否成功标记
                        boolean resutlSucess = false;
                        ServerEntiy serverEntiy = ServerList.robinServerAddress();
                        //讲行服务调用，接口访问超时进行重试操作
                        for( int j=0; j < tryNum ; j++) {
                            String result = "";
                            try {
                                result = HttpClientUtil.get2("http://" + serverEntiy.serverAddress + "?data=" + i, null);
                                resutlSucess = true;
                            } catch (TimeOutException e){
                                //失败后，进行服务失败次数累加
                                serverEntiy.setFailureTimes(new AtomicInteger(serverEntiy.getFailureTimes().intValue()+1));
                            }
                            //成功之后 打印返回结果
                            if(resutlSucess){
                                System.out.println(serverEntiy +"_" + result+"\t");
                                break;
                            }
                        }// end if

                        //重试失败后。路踢掉不可用服务
                        if(!resutlSucess){
                            ServerList.removeServer(serverEntiy);
                        } else {
                            i++;
                        }
                        Thread.sleep(1000L);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            }
        }).start();
        //启动线程调用服务 ===============end


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
