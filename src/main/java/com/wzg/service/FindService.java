package com.wzg.service;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.google.gson.JsonParser;
import com.wzg.dao.IBankDao;
import com.wzg.dao.IJdbcBankDao;
import com.wzg.entity.Bank;
import com.wzg.mq.Consumer;
import com.wzg.mq.Produce;

@Service
@SuppressWarnings("unchecked")
public class FindService implements IFindService {
	@Resource
	private IBankDao bankDao;
	@Autowired
	private IJdbcBankDao jdbcBankDao;
	@Resource
	private Produce produce;
	@Resource
	private Consumer consumer;
	private Gson gson = new Gson();
	public Map findAll() {
		final Map map = new HashMap<>();
		map.put("data", bankDao.selectAll());
		return map;
	}
	

	public Map findAllByJDBC() {
		final Map map = new HashMap<>();
		map.put("data", jdbcBankDao.getAll());
		return map;
	}

	@Override
	public void addData(Bank bank) {
		//状态0位中间状态  1为分布式事务已经成功
		bank.setState("00000000");
		int totle = this.bankDao.addBank(bank);
		System.out.println(bank.getGid());
		String json = gson.toJson(bank);
		produce.sendMsg("bank1", json);
//		produce.sendMsg("bank2", json);
		System.out.println("end---------");
	}

	@Override
	public void afterAddData(String json) {
		Map msg = gson.fromJson(json, Map.class);
		if (msg != null && msg.get("success")!= null && Boolean.valueOf(String.valueOf(msg.get("success")))){
			//分布式事务正常处理 
			//通常这里会改变addData中保存到数据库中数据的状态
			String gid = String.valueOf(msg.get("gid"));
			// 根据GID更新bank状态
			Bank bank = new Bank();
			bank.setGid(gid);
			bank.setState("00000001");
			bankDao.updateByPrimaryKey(bank);
		}
	}

}
