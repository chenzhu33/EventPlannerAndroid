package com.carelife.eventplanner.utils;

/**
 * 添加: 地图相关工具
 */
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class MapUtil {
    private static MapUtil mapsApiUtils = new MapUtil();

    /**
     * 单例模式
     *
     */
    public static MapUtil getInstance() {
        return mapsApiUtils;
    }

    /**
     * 网络下载,根据API地址和参数获取响应对象HttpResponse
     *
     */
    private String post(String url) {
        HttpURLConnection httpURLConnection = null;

        PrintWriter printWriter = null;
        BufferedReader bufferedReader = null;
        StringBuilder responseResult = new StringBuilder();

        try {
            URL realUrl = new URL(url);
            // 打开和URL之间的连接
            httpURLConnection = (HttpURLConnection) realUrl.openConnection();
            // 设置通用的请求属性
            httpURLConnection.setRequestProperty("accept", "*/*");
            httpURLConnection.setRequestProperty("connection", "Keep-Alive");
            httpURLConnection.setRequestProperty("Content-Length", String
                    .valueOf(0));
            // 发送POST请求必须设置如下两行
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setDoInput(true);
            // 获取URLConnection对象对应的输出流
            printWriter = new PrintWriter(httpURLConnection.getOutputStream());
            // 发送请求参数
            // flush输出流的缓冲
            printWriter.flush();
            // 根据ResponseCode判断连接是否成功
            int responseCode = httpURLConnection.getResponseCode();
            if (responseCode != 200) {
                return "";
            }
            // 定义BufferedReader输入流来读取URL的ResponseData
            bufferedReader = new BufferedReader(new InputStreamReader(
                    httpURLConnection.getInputStream()));
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                responseResult.append(line);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (httpURLConnection != null) {
                httpURLConnection.disconnect();
            }
            try {
                if (printWriter != null) {
                    printWriter.close();
                }
                if (bufferedReader != null) {
                    bufferedReader.close();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }

        }
        return responseResult.toString();
    }

    /**
     * 根据google API 获取两地的时间
     *
     * @param origin      起點
     * @param destination 終點
     * @param mode        出行方式 driving駕車，  walking步行， bicycling自行車, transit公交車
     * @return
     */
    public String getTimes(String origin, String destination, String mode) {
        String url = "https://maps.googleapis.com/maps/api/distancematrix/json?origins=" + origin + "&" +
                "destinations=" + destination + "&mode=" + mode + "&key=AIzaSyBVD0DilU1xTzRi1D7-wKBVUhiksnEV8hc";
        return post(url);
    }

}
