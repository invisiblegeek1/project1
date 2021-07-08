package com.revature.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.Scanner;

import com.revature.connection.ConnectionFactory;
import com.revature.entity.User;

public class JdbcMainRepository implements MainRepository{
	
	
	@Override
	public void deposit(User user) {
		Connection con =null;
		try {
			con = ConnectionFactory.getConnection();
			@SuppressWarnings("resource")
			Scanner sc = new Scanner(System.in);
			System.out.println("Enter the Amount to deposit:");
			double amount = sc.nextDouble();
			double Balance = user.getBalance();
			Balance = Balance + amount; 
			
			String sql = "insert into userinfo (AccHolderName,AccNumber,Balance) values (?,?,?)";
			PreparedStatement ps = con.prepareStatement(sql);
			ps.setString(1, user.getAccHolderName());
			ps.setInt(2, user.getAccNumber());
			ps.setDouble(3, Balance);
			int rowCount = ps.executeUpdate();
			if (rowCount == 1) {
				System.out.println("Money Credited..");
			}			
			
		}catch(SQLException e) {
			e.printStackTrace();
		}finally {
			try {
				con.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void Transaction() {
		Connection con =null;
		try {
			con = ConnectionFactory.getConnection();
			Scanner sc = new Scanner(System.in);
			//Enter the TransferDetails
			System.out.println("Enter the Details......");
			System.out.println("Enter the FromAccount:");
			int fromAcc = sc.nextInt();
			System.out.println("Enter the ToAccount:");
			int toAcc = sc.nextInt();
			System.out.println("Enter the amount to Transfer");
			double Amount = sc.nextDouble();
			
			//Fetch the from Account Balance
			String q = "select * from userinfo where AccNumber = "+fromAcc+" ";
			Statement st = con.createStatement();
			ResultSet rs = st.executeQuery(q);
			rs.next();
			User FromAccount = new User(rs.getString(1),rs.getInt(2),rs.getDouble(3));
//			System.out.println(FromAccount);
//			System.out.println("Balance of fromaccount"+FromAccount.getBalance());
			
			//Fetch the To Account Balance
			String q1 = "select * from userinfo where AccNumber = "+toAcc+" ";
			Statement st1 = con.createStatement();
			ResultSet rs1 = st1.executeQuery(q1);
			rs1.next();
			User ToAccount = new User(rs1.getString(1),rs1.getInt(2),rs1.getDouble(3));
//			System.out.println(ToAccount);
//			System.out.println("Balance of ToAccount"+ToAccount.getBalance());
//			System.out.println(Amount);
			Transact(FromAccount,ToAccount,Amount,con);
			
			
			
		}catch(SQLException e) {
			e.printStackTrace();
		}finally {
			try {
				con.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
	}
	public static void Transact(User FromAccount, User ToAccount, double Amount, Connection con ) {
		try {
			
			if(FromAccount.getBalance() >= Amount) {
				//Transacted the amount
				FromAccount.setBalance(FromAccount.getBalance() - Amount);
				ToAccount.setBalance(ToAccount.getBalance() + Amount);
				
				//Update the remaining balance amount to the database
				String q = "update userinfo set balance = ? where AccNumber = ?";
				PreparedStatement ps = con.prepareStatement(q);
				ps.setDouble(1, FromAccount.getBalance());
				ps.setInt(2,FromAccount.getAccNumber());
				ps.executeUpdate();
				
				String q1 = "update userinfo set balance = ? where AccNumber = ?";
				PreparedStatement ps1 = con.prepareStatement(q1);
				ps1.setDouble(1, ToAccount.getBalance());
				ps1.setInt(2,ToAccount.getAccNumber());
				ps1.executeUpdate();
				
				System.out.println("Transfered Successfully");
				
				//update the transaction to the transaction table
				UpdateTransact(FromAccount,ToAccount,Amount,con);
				
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static void UpdateTransact(User FromAccount,User ToAccount,double Amount,Connection con) {
//		Date dateNow = new Date();
		LocalDate today = LocalDate.now();
		String date = today.toString();
//		SimpleDateFormat ft = new SimpleDateFormat("dd.MM.yyy");
//		String date = ft.format(dateNow);
		
		try {
			String q = "insert into transferinfo(User,Amount,Type,Date) values (?,?,?,?)";
			PreparedStatement ps = con.prepareStatement(q);
			ps.setString(1,FromAccount.getAccHolderName());
			ps.setDouble(2,Amount);
			ps.setString(3,"Dedited");
			ps.setString(4,date);
			
			int rowcount = ps.executeUpdate();
			if(rowcount == 1) {
				System.out.println("Tranasaction Updated.");
			}
			String q1 = "insert into transferinfo(User,Amount,Type,Date) values (?,?,?,?)";
			PreparedStatement ps1 = con.prepareStatement(q);
			ps1.setString(1,ToAccount.getAccHolderName());
			ps1.setDouble(2,Amount);
			ps1.setString(3,"Credited");
			ps1.setString(4,date);
			
			int rowcount1 = ps.executeUpdate();
			if(rowcount1 == 1) {
				System.out.println("Tranasaction Updated.");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

	
	

	

	
	
////	public void withdraw(User user) {
////		Connection con =null;
////		try {
////			con = ConnectionFactory.getConnection();
////			@SuppressWarnings("resource")
////			Scanner sc = new Scanner(System.in);
////			System.out.println("Enter the Amount to Withdraw:");
////			double amount = sc.nextDouble();
////			double Balance = user.getBalance();
////			
////			if(amount<= Balance) {
////				Balance -= amount;
////				System.out.println(user.getAccHolderName()+" "+Balance);
////			}else {
////				System.out.println("Withdrawal of"+user.getAccHolderName()+"Fails due to Insufficient Balance");
////			}
////
////			
////			String sql = "insert into userinfo (AccHolderName,AccNumber,Balance,CreditorDebit) values (?,?,?,?)";
////			PreparedStatement ps = con.prepareStatement(sql);
////			ps.setString(1, user.getAccHolderName());
////			ps.setInt(2, user.getAccNumber());
////			ps.setDouble(3, Balance);
////			ps.setString(4, "Debited");
////			
////			int rowCount = ps.executeUpdate();
////			if (rowCount == 1) {
////				System.out.println("Successfull..");
////			}			
////			
////		}catch(SQLException e) {
////			e.printStackTrace();
////		}finally {
////			try {
////				con.close();
////			} catch (SQLException e) {
////				e.printStackTrace();
////			}
////		}
//	}

}
