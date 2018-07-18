package com.shanduo.newretail.util;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;
import java.net.URLConnection;
import java.util.Iterator;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;

import com.shanduo.newretail.wx.MyX509TrustManager;

import net.sf.json.JSONObject;


public class HttpRequest {
     private String charset = "utf-8";
        private Integer connectTimeout = null;
        private Integer socketTimeout = null;
        private String proxyHost = null;
        private Integer proxyPort = null;

        /**
         * Do GET request
         * @param url
         * @return
         * @throws Exception
         * @throws IOException
         */
        public String doGet(String url) throws Exception {

            URL localURL = new URL(url);

            URLConnection connection = openConnection(localURL);
            HttpURLConnection httpURLConnection = (HttpURLConnection)connection;

            httpURLConnection.setRequestProperty("Accept-Charset", charset);
            httpURLConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

            InputStream inputStream = null;
            InputStreamReader inputStreamReader = null;
            BufferedReader reader = null;
            StringBuffer resultBuffer = new StringBuffer();
            String tempLine = null;

            if (httpURLConnection.getResponseCode() >= 300) {
                throw new Exception("HTTP Request is not success, Response code is " + httpURLConnection.getResponseCode());
            }

            try {
                inputStream = httpURLConnection.getInputStream();
                inputStreamReader = new InputStreamReader(inputStream,"UTF-8");
                reader = new BufferedReader(inputStreamReader);

                while ((tempLine = reader.readLine()) != null) {
                    resultBuffer.append(tempLine);
                }

            } finally {

                if (reader != null) {
                    reader.close();
                }

                if (inputStreamReader != null) {
                    inputStreamReader.close();
                }

                if (inputStream != null) {
                    inputStream.close();
                }
            }
            return resultBuffer.toString();
        }

        /**
         * Do POST request
         * @param url
         * @param parameterMap
         * @return
         * @throws Exception 
         */
        public String doPost(String url, Map parameterMap) throws Exception {

            /* Translate parameter map to parameter date string */
            StringBuffer parameterBuffer = new StringBuffer();
            if (parameterMap != null) {
                Iterator iterator = parameterMap.keySet().iterator();
                String key = null;
                String value = null;
                while (iterator.hasNext()) {
                    key = (String)iterator.next();
                    if (parameterMap.get(key) != null) {
                        value = (String)parameterMap.get(key);
                    } else {
                        value = "";
                    }
                    parameterBuffer.append(key).append("=").append(value);
                    if (iterator.hasNext()) {
                        parameterBuffer.append("&");
                    }
                }
            }
            System.out.println("POST parameter : " + parameterBuffer.toString());
            URL localURL = new URL(url);
            URLConnection connection = openConnection(localURL);
            HttpURLConnection httpURLConnection = (HttpURLConnection)connection;
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setRequestProperty("Accept-Charset", charset);
            httpURLConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            httpURLConnection.setRequestProperty("Content-Length", String.valueOf(parameterBuffer.length()));

            OutputStream outputStream = null;
            OutputStreamWriter outputStreamWriter = null;
            InputStream inputStream = null;
            InputStreamReader inputStreamReader = null;
            BufferedReader reader = null;
            StringBuffer resultBuffer = new StringBuffer();
            String tempLine = null;

            try {
                outputStream = httpURLConnection.getOutputStream();
                outputStreamWriter = new OutputStreamWriter(outputStream);

                outputStreamWriter.write(parameterBuffer.toString());
                outputStreamWriter.flush();

                if (httpURLConnection.getResponseCode() >= 300) {
                    throw new Exception("HTTP Request is not success, Response code is " + httpURLConnection.getResponseCode());
                }

                inputStream = httpURLConnection.getInputStream();
                inputStreamReader = new InputStreamReader(inputStream);
                reader = new BufferedReader(inputStreamReader);

                while ((tempLine = reader.readLine()) != null) {
                    resultBuffer.append(tempLine);
                }

            } finally {

                if (outputStreamWriter != null) {
                    outputStreamWriter.close();
                }

                if (outputStream != null) {
                    outputStream.close();
                }

                if (reader != null) {
                    reader.close();
                }

                if (inputStreamReader != null) {
                    inputStreamReader.close();
                }

                if (inputStream != null) {
                    inputStream.close();
                }

            }

            return resultBuffer.toString();
        }

        private URLConnection openConnection(URL localURL) throws IOException {
            URLConnection connection;
            if (proxyHost != null && proxyPort != null) {
                Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxyHost, proxyPort));
                connection = localURL.openConnection(proxy);
            } else {
                connection = localURL.openConnection();
            }
            return connection;
        }

        /**
         * Render request according setting
         * @param request
         */
        private void renderRequest(URLConnection connection) {

            if (connectTimeout != null) {
                connection.setConnectTimeout(connectTimeout);
            }

            if (socketTimeout != null) {
                connection.setReadTimeout(socketTimeout);
            }

        }

        /*
         * Getter & Setter
         */
        public Integer getConnectTimeout() {
            return connectTimeout;
        }

        public void setConnectTimeout(Integer connectTimeout) {
            this.connectTimeout = connectTimeout;
        }

        public Integer getSocketTimeout() {
            return socketTimeout;
        }

        public void setSocketTimeout(Integer socketTimeout) {
            this.socketTimeout = socketTimeout;
        }

        public String getProxyHost() {
            return proxyHost;
        }

        public void setProxyHost(String proxyHost) {
            this.proxyHost = proxyHost;
        }

        public Integer getProxyPort() {
            return proxyPort;
        }

        public void setProxyPort(Integer proxyPort) {
            this.proxyPort = proxyPort;
        }

        public String getCharset() {
            return charset;
        }

        public void setCharset(String charset) {
            this.charset = charset;
        }
        /**
         * 发送https请求
         * @param requestUrl 请求地址
         * @param requestMethod 请求方式（GET、POST）
         * @param outputStr 提交的数据
         * @return JSONObject(通过JSONObject.get(key)的方式获取json对象的属性值)
         */
        public static JSONObject httpsRequest(String requestUrl, String requestMethod, String outputStr) {
            JSONObject jsonObject = null;
            try {
                // 创建SSLContext对象，并使用我们指定的信任管理器初始化
                TrustManager[] tm = { new MyX509TrustManager() };
                SSLContext sslContext = SSLContext.getInstance("SSL", "SunJSSE");
                sslContext.init(null, tm, new java.security.SecureRandom());
                // 从上述SSLContext对象中得到SSLSocketFactory对象
                SSLSocketFactory ssf = sslContext.getSocketFactory();

                URL url = new URL(requestUrl);
                HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
                conn.setSSLSocketFactory(ssf);

                conn.setDoOutput(true);
                conn.setDoInput(true);
                conn.setUseCaches(false);
                // 设置请求方式（GET/POST）
                conn.setRequestMethod(requestMethod);

                // 当outputStr不为null时向输出流写数据
                if (null != outputStr) {
                    OutputStream outputStream = conn.getOutputStream();
                    // 注意编码格式
                    outputStream.write(outputStr.getBytes("UTF-8"));
                    outputStream.close();
                }

                // 从输入流读取返回内容
                InputStream inputStream = conn.getInputStream();
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "utf-8");
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String str = null;
                StringBuffer buffer = new StringBuffer();
                while ((str = bufferedReader.readLine()) != null) {
                    buffer.append(str);
                }

                // 释放资源
                bufferedReader.close();
                inputStreamReader.close();
                inputStream.close();
                inputStream = null;
                conn.disconnect();
                jsonObject = JSONObject.fromObject(buffer.toString());
            } catch (ConnectException ce) {
             //   log.error("连接超时：{}", ce);
            } catch (Exception e) {
           //     log.error("https请求异常：{}", e);
            }
            return jsonObject;
        }
}