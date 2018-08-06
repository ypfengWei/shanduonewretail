package com.shanduo.newretail.controller;

import java.io.File;
import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.shanduo.newretail.consts.ConfigConsts;
import com.shanduo.newretail.entity.common.ResultBean;
import com.shanduo.newretail.entity.common.SuccessBean;
import com.shanduo.newretail.util.UUIDGenerator;

/**
 * 上传文件控制层
 * @ClassName: FileController
 * @Description: TODO
 * @author fanshixin
 * @date 2018年4月23日 上午9:33:28
 * 
 */
@Controller
@RequestMapping("file")
public class FileController {
	
	
	/**
	 * 文件上传
	 * @param request
	 * @param file
	 * @return
	 */
    @RequestMapping(value="upload",method=RequestMethod.POST)
    @ResponseBody
    public ResultBean upload(HttpServletRequest request,@RequestParam("file") MultipartFile file){
	        String fileName = file.getOriginalFilename();
	        fileName = UUIDGenerator.getUUID()+fileName.substring(fileName.lastIndexOf("."));
	        File dir = new File(ConfigConsts.IMAGE_PATH,fileName);
	        if(!dir.exists()){
	            dir.mkdirs();
	        }
	        //MultipartFile自带的解析方法
	        try {
				file.transferTo(dir);
			} catch (IllegalStateException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		    return new SuccessBean(fileName);
    }   

}
