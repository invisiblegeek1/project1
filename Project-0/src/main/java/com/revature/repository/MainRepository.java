package com.revature.repository;

import com.revature.entity.User;

public interface MainRepository {

	void deposit(User user);
	//void withdraw(User user);
	void Transaction();
	void TopTen();
	void MonthlyTransaction();
	
	
	
}
