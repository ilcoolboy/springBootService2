package com.wzg.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

public class Bank implements Serializable {
	private static final long serialVersionUID = 1L;
	private String gid;
	private String name;
	private BigDecimal account;
	private Date lasttime;
	private String state;
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	public String getGid() {
		return gid;
	}
	public void setGid(String gid) {
		this.gid = gid;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public BigDecimal getAccount() {
		return account;
	}
	public void setAccount(BigDecimal account) {
		this.account = account;
	}
	public Date getLasttime() {
		return lasttime;
	}
	public void setLasttime(Date lasttime) {
		this.lasttime = lasttime;
	}
	
}
