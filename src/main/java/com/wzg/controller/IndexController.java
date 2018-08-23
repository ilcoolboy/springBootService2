package com.wzg.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.wzg.entity.Bank;
import com.wzg.service.IFindService;

/**
 * rest请求控制器 
 * @author wuzhigang
 *
 */
@RestController
@RequestMapping("/index")
@SuppressWarnings({"rawtypes","unchecked"})
public class IndexController {
	@Autowired
	private IFindService findService;
	
	@RequestMapping("/find")
	@ResponseBody
	@Transactional(readOnly=true)
	public Map find(String name) {
		/*
		final Map map = findService.findAll();
		*/
		final Map map = findService.findAllByJDBC();
		List<Bank> list  = (List<Bank>) map.get("data");
		return map;
	}
	@RequestMapping("/add")
	@ResponseBody
	public Map addData(Bank bank) {
		this.findService.addData(bank);
		return this.find(null);
		
	}
	@RequestMapping("/onMsg")
	public Map onMsg() {
		return this.find(null);
	}

	/**
	 * 消息消费完成的回调接口 .
	 * @param str
	 * @return
	 */
	@RequestMapping("/msgBack")
	public Map afterAddData(@RequestParam(value = "str")String str) {
		this.findService.afterAddData(str);
		Map map = new HashMap();
		map.put("success", true);
		return map;
	}
	
	
	
}
