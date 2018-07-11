package com.shanduo.newretail.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.shanduo.newretail.entity.common.ResultBean;

@Controller
@RequestMapping(value = "code")
public class CodeController {

	@RequestMapping(value = "envoyer",method={RequestMethod.POST,RequestMethod.GET})
	@ResponseBody
	public ResultBean envoyer() {
		return null;
	}
}
