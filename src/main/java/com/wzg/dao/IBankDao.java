package com.wzg.dao;

import java.util.List;

import com.wzg.entity.Bank;

public interface IBankDao {
	List<Bank> selectAll();
	Integer addBank(Bank bank);
	Integer updateByPrimaryKey(Bank bank);
}
