package com.shanduo.newretail.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.shanduo.newretail.entity.AccessToken;
import com.shanduo.newretail.entity.JsApiTicket;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.math.BigInteger;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public class WebUtils {
    private final static String LOG_PATH;

    static {
        File logfile = new File("/var/log", "payLog");
        if (!logfile.exists()) {
            logfile.mkdirs();
        }
        LOG_PATH = logfile.getAbsolutePath();
    }

    public static String readInputSteam(InputStream inputStream) {
        StringBuilder resposeBuffer = new StringBuilder();
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(inputStream));
            String lines;
            while ((lines = reader.readLine()) != null) {
                lines = new String(lines.getBytes(), "utf-8");
                resposeBuffer.append(lines);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return resposeBuffer.toString();
    }

    private static byte[] base64DecodeChars = new byte[]{-1, -1, -1, -1, -1,
            -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
            -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
            -1, -1, -1, -1, 62, -1, -1, -1, 63, 52, 53, 54, 55, 56, 57, 58, 59,
            60, 61, -1, -1, -1, -1, -1, -1, -1, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9,
            10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, -1,
            -1, -1, -1, -1, -1, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37,
            38, 39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51, -1, -1, -1,
            -1, -1};

    /**
     * Base64解码
     */
    public static byte[] decode(String str) {
        byte[] data = str.getBytes();
        int len = data.length;
        ByteArrayOutputStream buf = new ByteArrayOutputStream(len);
        int i = 0;
        int b1, b2, b3, b4;

        while (i < len) {
            do {
                b1 = base64DecodeChars[data[i++]];
            } while (i < len && b1 == -1);
            if (b1 == -1) {
                break;
            }

            do {
                b2 = base64DecodeChars[data[i++]];
            } while (i < len && b2 == -1);
            if (b2 == -1) {
                break;
            }
            buf.write((b1 << 2) | ((b2 & 0x30) >>> 4));

            do {
                b3 = data[i++];
                if (b3 == 61) {
                    return buf.toByteArray();
                }
                b3 = base64DecodeChars[b3];
            } while (i < len && b3 == -1);
            if (b3 == -1) {
                break;
            }
            buf.write((((b2 & 0x0f) << 4) | ((b3 & 0x3c) >>> 2)));

            do {
                b4 = data[i++];
                if (b4 == 61) {
                    return buf.toByteArray();
                }
                b4 = base64DecodeChars[b4];
            } while (i < len && b4 == -1);
            if (b4 == -1) {
                break;
            }
            buf.write(((b3 & 0x03) << 6) | b4);
        }
        return buf.toByteArray();
    }

    public static String readBuffer(HttpServletRequest request) {
        StringBuilder inputString = new StringBuilder();
        BufferedReader reader = null;
        try {
            reader = request.getReader();
            String line;
            while ((line = reader.readLine()) != null) {
                inputString.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return inputString.toString();
    }

    /**
     * 解析xml,返回第一级元素键值对。如果第一级元素有子节点，则此节点的值是子节点的xml数据。
     */
    public static SortedMap<String, String> doXMLParse(String strxml) {
        SortedMap<String, String> parameters = new TreeMap<>();
        SAXReader reader = new SAXReader();
        ByteArrayInputStream tInputStringStream = null;
        try {
            tInputStringStream = new ByteArrayInputStream(strxml.getBytes("utf-8"));
            Document document = reader.read(tInputStringStream);
            Element root = document.getRootElement();
            for (Iterator it = root.elementIterator(); it.hasNext(); ) {
                Element element = (Element) it.next();
                String eName = element.getQualifiedName();
                if ("ScanCodeInfo".equals(eName)) {
                    List<Element> list = element.elements();
                    for (Element element1 : list) {
                        parameters.put(element1.getQualifiedName(), element1.getText());
                    }
                } else {
                    parameters.put(eName, element.getText());
                }
            }
        } catch (UnsupportedEncodingException | DocumentException e) {
            e.printStackTrace();
        } finally {
            try {
                if (tInputStringStream != null) {
                    tInputStringStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return parameters;
    }

    /**
     * xml转map集合
     *
     * @param resContent xml字符串
     * @return map
     */
    public static Map<String, Object> parseXML(String resContent) {
        Map<String, Object> params = new HashMap<>();
        try {
            Document document = DocumentHelper.parseText(resContent);
            Element root = document.getRootElement();
            for (Iterator it = root.elementIterator(); it.hasNext(); ) {
                Element element = (Element) it.next();
                params.put(element.getQualifiedName(), element.getText());
            }
        } catch (DocumentException e) {
            e.printStackTrace();
        }
        return params;
    }

    /**
     * 加密字符串
     */
    public static String SHA1(String decript) {
        if (decript != null && !decript.isEmpty()) {
            try {
                MessageDigest digest = MessageDigest.getInstance("SHA-1");
                digest.update(decript.getBytes());
                byte messageDigest[] = digest.digest();
                // Create Hex String
                StringBuilder hexString = new StringBuilder();
                // 字节数组转换为 十六进制 数
                for (byte aMessageDigest : messageDigest) {
                    String shaHex = Integer.toHexString(aMessageDigest & 0xFF);
                    if (shaHex.length() < 2) {
                        hexString.append(0);
                    }
                    hexString.append(shaHex);
                }
                return hexString.toString();
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
        }
        return "";
    }

    /**
     * map集合拼接成xml格式字符串
     */
    public static String toXML(Map<String, Object> params) {
        StringBuilder builder = new StringBuilder();
        builder.append("<xml>");
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            builder.append("<").append(entry.getKey()).append(">").append("<![CDATA[").append(entry.getValue()).append("]]>").append("<").append("/").append(entry.getKey()).append(">");
        }
        builder.append("</xml>");
        return builder.toString();
    }

    /**
     * 远程请求微信服务器获取公众号全局Access_token
     */
    public static AccessToken getWXAccess_token(String appid, String secret) {
        AccessToken accessToken = null;
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpGet method = new HttpGet("https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=APPID&secret=SECRET".replace("APPID", appid).replace("SECRET", secret));
        CloseableHttpResponse response = null;
        try {
            response = httpClient.execute(method);
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                JSONObject jsonObject = JSON.parseObject(readInputSteam(response.getEntity().getContent()));
                if (jsonObject.containsKey("access_token")) {
                    accessToken = new AccessToken();
                    accessToken.setAccessToken(jsonObject.getString("access_token"));
                    accessToken.setExpiresIn(jsonObject.getInteger("expires_in"));
                } else {
                    logResult("获取accessToken失败:" + jsonObject.toJSONString());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            closeConn(httpClient, response);
        }
        return accessToken;
    }

   

    /**
     * 远程请求微信服务器获取网页js调用凭证
     */
    public static JsApiTicket getWXJsApiTicket(String access_token) {
        JsApiTicket jsApiTicket = null;
        CloseableHttpClient httpClient = HttpClients.createDefault();
        CloseableHttpResponse response = null;
        HttpGet method = new HttpGet("https://api.weixin.qq.com/cgi-bin/ticket/getticket?access_token=TOKEN&type=jsapi".replace("TOKEN", access_token));
        try {
            response = httpClient.execute(method);
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                JSONObject jsonObject = JSON.parseObject(readInputSteam(response.getEntity().getContent()));
                if (jsonObject.containsKey("ticket")) {
                    jsApiTicket = new JsApiTicket();
                    jsApiTicket.setTicket(jsonObject.getString("ticket"));
                    jsApiTicket.setExpiresIn(jsonObject.getInteger("expires_in"));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            closeConn(httpClient, response);
        }
        return jsApiTicket;
    }

    /**
     * 创建自定义菜单
     */
    public static String createMenu(String token, String menu) {
        if (menu != null) {
            CloseableHttpResponse response = null;
            CloseableHttpClient httpClient = HttpClients.createDefault();
            HttpPost method = new HttpPost("https://api.weixin.qq.com/cgi-bin/menu/create?access_token=" + token);
            try {
                StringEntity stringEntity = new StringEntity(menu, "UTF-8");
                method.setEntity(stringEntity);
                response = httpClient.execute(method);
                if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                    return readInputSteam(response.getEntity().getContent());
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                closeConn(httpClient, response);
            }
        }
        return null;
    }

    /**
     * 删除自定义菜单
     */
    public static String delMenu(String token) {
        HttpGet method = new HttpGet("https://api.weixin.qq.com/cgi-bin/menu/delete?access_token=" + token);
        CloseableHttpResponse response = null;
        CloseableHttpClient httpClient = HttpClients.createDefault();
        try {
            response = httpClient.execute(method);
            if (response.getStatusLine().getStatusCode() == org.apache.http.HttpStatus.SC_OK) {
                return readInputSteam(response.getEntity().getContent());
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            closeConn(httpClient, response);
        }
        return null;
    }

    /**
     * 查询自定义菜单
     */
    public static String getMenu(String token) {
        CloseableHttpResponse response = null;
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpGet method = new HttpGet("https://api.weixin.qq.com/cgi-bin/menu/get?access_token=" + token);
        try {
            response = httpClient.execute(method);
            if (response.getStatusLine().getStatusCode() == org.apache.http.HttpStatus.SC_OK) {
                return readInputSteam(response.getEntity().getContent());
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            closeConn(httpClient, response);
        }
        return null;
    }

    /**
     * 保存一张微信图片到自己服务器
     */
    public static boolean downloadImage(String access_token, String media_id, File folder, String fileName) {
        CloseableHttpResponse response = null;
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet("http://file.api.weixin.qq.com/cgi-bin/media/get?access_token=TOKEN&media_id=MEDIAID".replace("TOKEN", access_token).replace("MEDIAID", media_id));
        try {
            response = httpClient.execute(httpGet);
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                BufferedInputStream bis = new BufferedInputStream(response.getEntity().getContent());
                FileOutputStream fos = new FileOutputStream(new File(folder, fileName));
                byte[] buf = new byte[8096];
                int size;
                while ((size = bis.read(buf)) != -1) {
                    fos.write(buf, 0, size);
                }
                fos.close();
                bis.close();
                return true;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            closeConn(httpClient, response);
        }
        return false;
    }

    //关闭释放网络资源
    private static void closeConn(CloseableHttpClient httpClient, CloseableHttpResponse response) {
        try {
            if (response != null) {
                response.close();
            }
            if (httpClient != null) {
                httpClient.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 返回系统当前时间(精确到毫秒),作为一个唯一的订单编号
     *
     * @return 以yyyyMMddHHmmss为格式的当前系统时间
     */
    public static String getOrderNum() {
        Date date = new Date();
        DateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
        return df.format(date);
    }

    /**
     * 签名
     *
     * @param params 待签名参数
     * @return 签名字符串
     */
    public static String genAppSign(Map<String, Object> params) {
        StringBuilder sb = new StringBuilder();
        List<String> sortedKeys = new ArrayList<>(params.keySet());
        Collections.sort(sortedKeys);
        for (String key : sortedKeys) {
            sb.append(key).append("=").append(params.get(key)).append("&");
        }
        sb.append("key=").append(Config.PARTNER_KEY);
        return MD5Utils.getInstance().getMD5(sb.toString());
    }

    //微信统一下单
    public static JSONObject unifiedOrder(Map<String, Object> paymentData) {
        TenpayHttpClient httpClient = new TenpayHttpClient();
        if (httpClient.callHttpPost(Config.GATEURL, toXML(paymentData))) {
            Map<String, Object> params = parseXML(httpClient.getResContent());
            if (!params.isEmpty() && "SUCCESS".equals(params.get("return_code")) && "SUCCESS".equals(params.get("result_code"))) {
                String appid = (String) params.get("appid");
                String mch_id = (String) params.get("mch_id");
                String prepay_id = (String) params.get("prepay_id");
                params.clear();
                if (Config.APPID.equals(appid) && Config.PARTNER.equals(mch_id)) {
                    SecureRandom random = new SecureRandom();
                    params.put("appId", appid);
                    params.put("timeStamp", System.currentTimeMillis());
                    params.put("nonceStr", new BigInteger(32, random).toString(8));
                    params.put("package", "prepay_id=" + prepay_id);
                    params.put("signType", "MD5");
                    params.put("paySign", genAppSign(params));
                    return (JSONObject) JSON.toJSON(params);
                }
            }
        }
        return null;
    }

    /*微信原路退款*/
    public static void wxPayRefund(Map<String, Object> paymentData) throws Exception {
        TenpayHttpClient httpClient = new TenpayHttpClient();
        String respString = httpClient.callHttpPostSSL(Config.REFUNDURL, toXML(paymentData));
        paymentData.clear();
        if (respString != null) {
            paymentData = parseXML(respString);
            if (paymentData.isEmpty() || !"SUCCESS".equals(paymentData.get("return_code")) || !("SUCCESS".equals(paymentData.get("result_code")))) {
                throw new Exception();
            }
        }
    }

    /**
     * 写日志，方便测试（看网站需求，也可以改成把记录存入数据库）
     *
     * @param sWord 要写入日志里的文本内容
     */
    public static void logResult(String sWord) {
        new Thread(() -> {
            FileOutputStream out = null;
            try {
                out = new FileOutputStream(LOG_PATH + "/wx_pay_log_" + System.currentTimeMillis() + ".txt");
                out.write(sWord.getBytes("UTF-8"));
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (out != null) {
                    try {
                        out.flush();
                        out.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    //获取template_id 模版id
    public static String getTemplateID(String token, String json) {
        CloseableHttpResponse response = null;
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpPost method = new HttpPost("https://api.weixin.qq.com/cgi-bin/template/api_add_template?access_token=" + token);
        try {
            method.setEntity(new StringEntity(json, "UTF-8"));
            response = httpClient.execute(method);
            if (response.getStatusLine().getStatusCode() == org.apache.http.HttpStatus.SC_OK) {
                return readInputSteam(response.getEntity().getContent());
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            closeConn(httpClient, response);
        }
        return null;
    }

    //发送模版消息
    public static String sendTemplateMessage(String token, String json) {
        CloseableHttpResponse response = null;
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpPost method = new HttpPost("https://api.weixin.qq.com/cgi-bin/message/template/send?access_token=" + token);
        try {
            method.setEntity(new StringEntity(json, "UTF-8"));
            response = httpClient.execute(method);
            if (response.getStatusLine().getStatusCode() == org.apache.http.HttpStatus.SC_OK) {
                return readInputSteam(response.getEntity().getContent());
            }
        } catch (Exception e) {
            logResult("发送模版消息失败:" + e.getMessage());
        } finally {
            closeConn(httpClient, response);
        }
        return null;
    }

    // 装载模板
  /*  public static String loadTemplate(FreeMarkerConfigurer freeMarkerConfigurer, Map<String, Object> map) {
        try {
            Template tpl = freeMarkerConfigurer.getConfiguration().getTemplate("gzh_template_msg.json");
            return FreeMarkerTemplateUtils.processTemplateIntoString(tpl, map);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }*/

    //获取客户端IP
    public static String getRemortIP(HttpServletRequest request) {
        String ip = request.getHeader("x-forwarded-for");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        if (ip.contains(",")) {
            return ip.split(",")[0];
        }
        return ip;
    }

    public static JSONObject create_qrcode(String token, String content) {
        JSONObject jsonObject = null;
        CloseableHttpClient client = HttpClients.createDefault();
        CloseableHttpResponse response = null;
        HttpPost method = new HttpPost("https://api.weixin.qq.com/cgi-bin/qrcode/create?access_token=" + token);
        try {
            StringEntity entity = new StringEntity(content, "UTF-8");
            method.addHeader("Content-type", "application/json; charset=utf-8");
            method.setHeader("Accept", "application/json");
            method.setEntity(entity);
            response = client.execute(method);
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                jsonObject = JSON.parseObject(EntityUtils.toString(response.getEntity()));
                if (jsonObject.containsKey("ticket")) {
                    String ticket = jsonObject.getString("ticket");
                    jsonObject.replace("ticket", URLEncoder.encode(ticket, "utf-8"));
                    return jsonObject;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            closeConn(client, response);
        }
        return jsonObject;
    }

    public static HttpEntity download_qrcode(String ticket) {
        CloseableHttpClient client = HttpClients.createDefault();
        CloseableHttpResponse response = null;
        HttpGet method = new HttpGet("https://mp.weixin.qq.com/cgi-bin/showqrcode?ticket=" + ticket);
        try {
            response = client.execute(method);
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                return response.getEntity();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    //公众号登陆
    public static JSONObject gzhLongin(String unionid) {
        CloseableHttpClient client = HttpClients.createDefault();
        CloseableHttpResponse response = null;
        HttpGet method = new HttpGet("https://yapinkeji.com/shanduoparty/wx/bindingUser?unionId=UNIONID&type=2".replace("UNIONID", unionid));
        try {
            response = client.execute(method);
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                return JSON.parseObject(EntityUtils.toString(response.getEntity()));
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            closeConn(client, response);
        }
        return null;
    }

    //发布闪多动态
    public static JSONObject saveDynamic(Map<String, String> params) {
        CloseableHttpClient client = HttpClients.createDefault();
        CloseableHttpResponse response = null;
        HttpPost method = new HttpPost("https://yapinkeji.com/shanduoparty/jdynamic/savedynamic");
        List<NameValuePair> paramspairs = new ArrayList<>();
        for (Map.Entry<String, String> entry : params.entrySet()) {
            paramspairs.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
        }
        try {
            UrlEncodedFormEntity entity = new UrlEncodedFormEntity(paramspairs, "UTF-8");
            method.setEntity(entity);
            response = client.execute(method);
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                return JSON.parseObject(EntityUtils.toString(response.getEntity()));
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            closeConn(client, response);
        }
        return null;
    }
}
