package com.shanduo.newretail.util;

import java.io.IOException;
import java.util.Properties;

public class Config {
    public static final String HOST;
    public static final String APPID;
    public static final String SECRET;
    public static final String TOKEN;
    public static final String PARTNER;
    public static final String PARTNER_KEY;
    public static final String GATEURL;
    public static final String REFUNDURL;
    public static final String NOTIFYURL;
    public static final String PAYSUCCESSTEMPLATEID;
    public static final String REFUNDTEMPLATEID;
    public static final String SHIPPEDSUCCESSTEMPLATEID;
    public static final String BUYSUCCESSTEMPLATEID;
    static Properties properties = new Properties();

    static {
        try {
            properties.load(Config.class.getResourceAsStream("/sys.properties"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        HOST = properties.getProperty("gzh.host");
        APPID = properties.getProperty("gzh.app_id");
        SECRET = properties.getProperty("gzh.app_secret");
        TOKEN = properties.getProperty("gzh.token");
        PARTNER = properties.getProperty("gzh.partner");
        PARTNER_KEY = properties.getProperty("gzh.partner_key");
        GATEURL = properties.getProperty("gzh.pay.gateurl");
        REFUNDURL = properties.getProperty("gzh.pay.refundurl");
        NOTIFYURL = properties.getProperty("gzh.pay.notify_url");
        PAYSUCCESSTEMPLATEID = properties.getProperty("gzh.pay.success.template");
        REFUNDTEMPLATEID = properties.getProperty("gzh.refund.template");
        SHIPPEDSUCCESSTEMPLATEID = properties.getProperty("gzh.shipped.success.template");
        BUYSUCCESSTEMPLATEID = properties.getProperty("gzh.buy.success.template");
    }
}
