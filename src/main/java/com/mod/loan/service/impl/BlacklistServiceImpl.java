package com.mod.loan.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mod.loan.mapper.BlacklistMapper;
import com.mod.loan.model.Blacklist;
import com.mod.loan.service.BlacklistService;

@Service
public class BlacklistServiceImpl implements BlacklistService {

	@Autowired
	BlacklistMapper blacklistMapper;

	@Override
	public Blacklist getByUid(Long uid) {
		return blacklistMapper.getByUid(uid);
	}
}
