package com.shanduo.newretail.wx;

/**
 * 模板详细信息 
 * @ClassName: TemplateData
 * @Description: TODO
 * @author fanshixin
 * @date 2018年7月16日 下午2:39:21
 *
 */
public class TemplateData {
	
	private String value;//内容
    private String color;//颜色
    
    public TemplateData(String value,String color){
        this.value = value;
        this.color = color;
    }
    
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public String getColor() {
		return color;
	}
	public void setColor(String color) {
		this.color = color;
	}
    
}
