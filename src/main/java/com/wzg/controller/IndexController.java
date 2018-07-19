package com.wzg.controller;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import com.wzg.entity.Bank;
import com.wzg.service.IFindService;

@RestController
@RequestMapping("/index")
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
	@Transactional
	public Map addData(Bank bank) {
		this.findService.addData(bank);
		return this.find(null);
	}
	@RequestMapping("/onMsg")
	public Map onMsg() {
		
		return this.find(null);
	}
}
