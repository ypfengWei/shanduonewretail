package com.shanduo.newretail.wx;

import static com.shanduo.newretail.wx.WX_TemplateMsgUtil.packJsonmsg;

import java.util.HashMap;
import java.util.Map;


public class Test {

	public static void main(String[] args) {
        //新增用户成功 - 推送微信消息
        senMsg("11_oSdGjVek6a1mGaWmpykNMmhGSYnjSceovAw_IlaxTBZRq3KkCAs2_Ylhe-biVuFpmWZR-ErElLlDzvjRWqhSuQIsWkMS3XZ72cPngnnhCT5mppqnImURtAxTaxu0Y8l2AxVXOdxUCewJ6fWORQBcAAACBM","o5YU108ZYIvTSoFZuf2oRqXIoRi8");
    }
	
   static void senMsg(String accessToken,String openId){
        //用户是否订阅该公众号标识 (0代表此用户没有关注该公众号 1表示关注了该公众号)
        Integer state= WX_UserUtil.subscribeState(accessToken, openId);
        //绑定了微信并且关注了服务号的用户 , 注册成功-推送注册短信
        if(state==1){
            Map<String,TemplateData> param = new HashMap<>();
            param.put("name",new TemplateData("怡宝","#696969"));
            param.put("remark",new TemplateData("购买成功","#696969"));
            //获取模板Id
//            String regTempId = getWXTemplateMsgId(accessToken, WeiXinEnum.WX_TEMPLATE_MSG_NUMBER.ORDER_SUCCESS.getMsgNumber());
            //调用发送微信消息给用户的接口
            String regTempId = "YmkEkD4zM3hoz6lIZJNL3cv99UPQZuUQjVEuffQctLs";
            WX_TemplateMsgUtil.sendWechatMsgToUser(accessToken, openId,regTempId, "", "#000000", packJsonmsg(param));
        }
   }
}
