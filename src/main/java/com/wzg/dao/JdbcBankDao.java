package com.wzg.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import com.wzg.entity.Bank;
@Repository
public class JdbcBankDao implements IJdbcBankDao {
	@Autowired
	JdbcTemplate jdbcTemplate;
	
	public List<Bank> getAll() {
//		return jdbcTemplate.queryForList("SELECT GID,NAME,ACCOUNT,LASTTIME FROM BANK", Bank.class);
		List<Bank> list = jdbcTemplate.query("SELECT GID,NAME,ACCOUNT,LASTTIME FROM BANK", new RowMapper<Bank>() {
			@Override
			public Bank mapRow(ResultSet rs, int rowNum) throws SQLException {
				// TODO Auto-generated method stub
				Bank bank = new Bank();
				bank.setGid(rs.getString("gid"));
				bank.setAccount(rs.getBigDecimal("account"));
				bank.setLasttime(rs.getDate("lasttime"));
				bank.setName(rs.getString("name"));
				return bank;
			}});
		return list;
	}
	
	
	public int updateBankState(String state, String gid) {
		return jdbcTemplate.update("UPDATE BANK SET STATE=? WHERE GID=? ", new Object[] {state, gid});
	}

}
