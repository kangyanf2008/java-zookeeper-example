package com.kyf.client.utils;

import com.kyf.client.error.TimeOutException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

public class HttpClientUtil {

    private static final String DEFAULT_CHARSET = "UTF-8";
    private static final String GET               = "GET";

    private static HttpURLConnection initHttp(String url, String method, Map<String, String> headers)
            throws IOException {
        URL _url = new URL(url);
        HttpURLConnection http = (HttpURLConnection) _url.openConnection();
        http.setConnectTimeout(30000);
        http.setReadTimeout(30000);
        http.setRequestMethod(method);
        http.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        http.setRequestProperty("User-Agent",
                "Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/33.0.1750.146 Safari/537.36");
        if (null != headers && !headers.isEmpty()) {
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                http.setRequestProperty(entry.getKey(), entry.getValue());
            }
        }
        http.setDoOutput(true);
        http.setDoInput(true);
        http.connect();
        return http;
    }

    public static String get(String url, Map<String, String> headers)  throws TimeOutException{
        StringBuffer bufferRes = null;
        try {
            HttpURLConnection http = initHttp(url, GET, headers);
            InputStream in = http.getInputStream();
            BufferedReader read = new BufferedReader(new InputStreamReader(in, DEFAULT_CHARSET));
            String valueString = null;
            bufferRes = new StringBuffer();
            while ((valueString = read.readLine()) != null) {
                bufferRes.append(valueString);
            }
            read.close();
            in.close();
            if (http != null) {
                http.disconnect();// 关闭连接
            }
            throw new TimeOutException("网络超时");
           // return bufferRes.toString();
        } catch (Exception e) {
            throw new TimeOutException("网络超时");
        }
    }

    public static String get2(String url, Map<String, String> headers)  throws TimeOutException{
        StringBuffer bufferRes = null;
        try {
            HttpURLConnection http = initHttp(url, GET, headers);
            InputStream in = http.getInputStream();
            BufferedReader read = new BufferedReader(new InputStreamReader(in, DEFAULT_CHARSET));
            String valueString = null;
            bufferRes = new StringBuffer();
            while ((valueString = read.readLine()) != null) {
                bufferRes.append(valueString);
            }
            read.close();
            in.close();
            if (http != null) {
                http.disconnect();// 关闭连接
            }

            return bufferRes.toString();
        } catch (Exception e) {
            throw new TimeOutException("网络超时");
        }
    }

}
