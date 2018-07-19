package com.wzg.service;

import java.util.Map;

import com.wzg.entity.Bank;

public interface IFindService {
	/**
	 * 查找全部 .
	 * 
	 * @return map.
	 */
	Map findAll();

	Map findAllByJDBC();
	
	void addData(Bank bank);
	
	void afterAddData(String msg);
	
}
