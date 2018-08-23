package com.wzg.service;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.aspectj.asm.IProgramElement.Accessibility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.gson.Gson;
import com.wzg.dao.IBankDao;
import com.wzg.dao.IJdbcBankDao;
import com.wzg.entity.Bank;
import com.wzg.mq.Consumer;
import com.wzg.mq.Produce;

@Service
@SuppressWarnings("unchecked")
@Transactional
public class FindService implements IFindService {

	private static final Logger log = LoggerFactory.getLogger(FindService.class);
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
		this.bankDao.addBank(bank);
		
		String json = gson.toJson(bank);
		produce.sendMsg("bank1", json);
//		produce.sendMsg("bank2", json);
		if (log.isInfoEnabled()) {
			log.info("end----");
		}
	
	}

	@Override
	@Transactional
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


	@Override
	public void afterAddError(String msg) {
		if (log.isDebugEnabled()) {
			log.debug(msg);
		}
		Bank bank = gson.fromJson(msg, Bank.class);
		String gid = bank.getGid();
		Integer deleteTotle = this.bankDao.deleteByPrimaryKey(gid);
		
		log.debug("回滚记录数:" + deleteTotle);
		log.debug("回滚记录gid:" + gid);
	}

}
