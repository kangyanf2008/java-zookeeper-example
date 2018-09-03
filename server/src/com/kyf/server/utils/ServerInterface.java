package com.kyf.server.utils;

import fi.iki.elonen.NanoHTTPD;

import java.util.Date;
import java.util.Map;

public class ServerInterface extends NanoHTTPD{

	public ServerInterface(int port) {
		super(port);

	}

	//http服务接口
	@Override
	public Response serve(IHTTPSession session) {
		Map<String, String> parms = session.getParms();
		String data = parms.get("data");
		return newFixedLengthResponse("result = " + data + " time="+(new Date().getTime()));
	}


}
