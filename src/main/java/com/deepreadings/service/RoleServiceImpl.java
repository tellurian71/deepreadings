package com.deepreadings.service;

import jakarta.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.deepreadings.dao.RoleDao;
import com.deepreadings.model.Role;

@Service
@Transactional
public class RoleServiceImpl implements RoleService {
	

	@Autowired
	RoleDao roleDao;

	@Override
	public Role readByName(String name) {
		return roleDao.readByName(name);
	}	
	
	
}

