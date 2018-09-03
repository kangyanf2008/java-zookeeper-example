package com.kyf.server.utils;

import org.apache.log4j.Logger;

import java.io.IOException;



public class ServerInterfaceUtil {

	public static Logger logger = Logger.getLogger(ServerInterfaceUtil.class);
	//开启一个http服务端口
	public static void publicServer(int serverPort){
		try {
			logger.info("开始启动服务");
			new ServerInterface(serverPort).start(2000, true);
			logger.info("启动服务完成");
		} catch (IOException e) {
			e.printStackTrace();
			logger.error("启动服务发生异常，端口为："+serverPort,e);
		}
	}

}
