package com.wzg.dao;

import java.util.List;

import com.wzg.entity.Bank;

public interface IJdbcBankDao {
	/**
	 * 查询所有数据
	 * @return
	 */
	List<Bank> getAll();
	/**
	 * 更新银行状态 .
	 * @param state .
	 * @param gid .
	 * @return .
	 */
	int updateBankState(String state, String gid);
}
