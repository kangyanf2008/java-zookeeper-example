package com.kyf.client.bean;

import java.util.concurrent.atomic.AtomicInteger;

public class ServerBean {
    public String serverAddress;                //服务器地址
    public AtomicInteger failureTimes;          //失败次数
    public String getServerAddress() {
        return serverAddress;
    }

    public void setServerAddress(String serverAddress) {
        this.serverAddress = serverAddress;
    }

    public AtomicInteger getFailureTimes() {
        return failureTimes;
    }

    public void setFailureTimes(AtomicInteger failureTimes) {
        this.failureTimes = failureTimes;
    }
    public ServerBean() {
    }
    public ServerBean(String serverAddress, AtomicInteger failureTimes) {
        this.serverAddress = serverAddress;
        this.failureTimes = failureTimes;
    }
    @Override
    public String toString() {
        return "ServerBean{" +
                "serverAddress='" + serverAddress + '\'' +
                ", failureTimes=" + failureTimes +
                '}';
    }
}
