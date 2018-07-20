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
	/**
	 * 新增数据
	 * @param bank
	 */
	void addData(Bank bank);
	/**
	 * 新增成功之后
	 * @param msg
	 */
	void afterAddData(String msg);
	/**
	 * 新增失败之后
	 * @param msg
	 */
	void afterAddError(String msg);
}
