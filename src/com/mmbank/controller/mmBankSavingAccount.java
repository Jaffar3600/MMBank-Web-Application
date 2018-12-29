package com.mmbank.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.moneymoney.account.SavingsAccount;
import com.moneymoney.account.service.SavingsAccountService;
import com.moneymoney.account.service.SavingsAccountServiceImpl;
import com.moneymoney.account.util.DBUtil;
import com.moneymoney.exception.AccountNotFoundException;


@WebServlet("*.mm")
public class mmBankSavingAccount extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final SavingsAccount SavingsAccount = null;
	@Override
	public void init() throws ServletException {
		super.init();
		try {
			Class.forName("com.mysql.jdbc.Driver");
			Connection connection = DriverManager.getConnection
					("jdbc:mysql://localhost:3306/bankapp_db", "root", "root");
			PreparedStatement preparedStatement = 
					connection.prepareStatement("DELETE FROM ACCOUNT");
			preparedStatement.execute();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
		
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String path = request.getServletPath();
		SavingsAccountService savingaccountservice = new SavingsAccountServiceImpl();
		SavingsAccount savingsAccount=null;
		switch(path){
			 
		case "/addNewAccount.mm":
			response.sendRedirect("addNewAccount.html");
			break;
		case "/savingsAccount.mm":
			String name = request.getParameter("accountHolderName");
			double accountBalance = Double.parseDouble(request.getParameter("accountBalance"));
			boolean salaried = request.getParameter("salary").equalsIgnoreCase("yes")?true:false;
			try {
				savingaccountservice.createNewAccount(name, accountBalance, salaried);
			} catch (ClassNotFoundException | SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			break;
		
			
		case "/closeAccount.mm":
			response.sendRedirect("closeAccount.html");
		
			break;
		case "/delete.mm":
			int accountNumber = Integer.parseInt(request.getParameter("closeAccount"));
			
			try {
				savingaccountservice.deleteAccount(accountNumber);
			} catch (ClassNotFoundException | SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			break;
			
		case "/getCurrentBalance.mm":
			response.sendRedirect("getCurrentBalance.html");
			break;
	
		case "/getCurrentBal.mm":
			int accountNumber1 = Integer.parseInt(request.getParameter("currentBalance"));
			try {
				double currentBal=savingaccountservice.checkBalance(accountNumber1);
				PrintWriter out = response.getWriter();
				out.println(currentBal);
				
			} catch (ClassNotFoundException | SQLException
					| AccountNotFoundException e) {
				e.printStackTrace();
			}
			break;
			
		case "/withdraw.mm":
			response.sendRedirect("Withdraw.html");
			break;
		case "/Withdraw.mm":
			int accountNumber2 = Integer.parseInt(request.getParameter("accountNumber"));
			double amount = Double.parseDouble(request.getParameter("amount"));
			try {
				try {
					savingsAccount=savingaccountservice.getAccountById(accountNumber2);
					 savingaccountservice.withdraw(savingsAccount,amount);
					 DBUtil.commit();
				} catch (AccountNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} catch (ClassNotFoundException | SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			break;
	}	
	}
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
	}

}
