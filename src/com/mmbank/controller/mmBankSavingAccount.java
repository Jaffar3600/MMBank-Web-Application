package com.mmbank.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import javax.servlet.RequestDispatcher;
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
	private RequestDispatcher dispatcher;
	private int count=1;
	
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
			response.sendRedirect("addNewAccount.jsp");
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
			response.sendRedirect("closeAccount.jsp");
		
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
			response.sendRedirect("getCurrentBalance.jsp");
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
			response.sendRedirect("Withdraw.jsp");
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
			
		case "/deposit.mm":
			response.sendRedirect("deposit.jsp");
			break;
			
		case "/Deposit.mm":
			int accountNumber3 = Integer.parseInt(request.getParameter("accountNumber"));
			double amount1 = Double.parseDouble(request.getParameter("amount"));
			try {
				savingsAccount=savingaccountservice.getAccountById(accountNumber3);
				savingaccountservice.deposit(savingsAccount, amount1);
				 DBUtil.commit();
			} 
			catch (AccountNotFoundException e) {
				
				e.printStackTrace();
			}
		 catch (ClassNotFoundException | SQLException e) {
			
			e.printStackTrace();
		}			
			break;
		case "/fundTransfer.mm":
			response.sendRedirect("fundTransfer.jsp");
			break;
			
		case "/FundTransfer.mm":
			int accountNumber4=Integer.parseInt(request.getParameter("accountNumber1"));
			int accountNumber5=Integer.parseInt(request.getParameter("accountNumber2"));
			double amount3 = Double.parseDouble(request.getParameter("amount"));
			try {
				SavingsAccount ss =savingaccountservice.getAccountById(accountNumber4);
				SavingsAccount sr=savingaccountservice.getAccountById(accountNumber5);
				savingaccountservice.fundTransfer(ss,sr,amount3);
				 DBUtil.commit();
			} catch (ClassNotFoundException | SQLException
					| AccountNotFoundException e) {
				
				e.printStackTrace();
			}
			break;
			
		case "/searchForm.mm":
			response.sendRedirect("SearchForm.jsp");
			break;
		case "/search.mm":
			int accountNumber11 = Integer.parseInt(request.getParameter("AccountNumber"));
			try {
				SavingsAccount account = savingaccountservice.getAccountById(accountNumber11);
				request.setAttribute("account", account);
				dispatcher = request.getRequestDispatcher("AccountDetails.jsp");
				dispatcher.forward(request, response);
			} catch (ClassNotFoundException | SQLException | AccountNotFoundException e) {
				e.printStackTrace();
			}
			break;
		case "/getAll.mm":
			try {
				java.util.List<com.moneymoney.account.SavingsAccount> accounts = savingaccountservice.getAllSavingsAccount();
				request.setAttribute("accounts", accounts);
				dispatcher = request.getRequestDispatcher("AccountDetails.jsp");
				dispatcher.forward(request, response);
			} catch (ClassNotFoundException | SQLException e) {
				e.printStackTrace();
			}
			break;
			
		case "/sortByName.mm":
			count+=1;
			try {
				Collection<SavingsAccount> accounts = savingaccountservice.getAllSavingsAccount();
				List<SavingsAccount> accountSet = new ArrayList<>(accounts);
					Collections.sort(accountSet,new Comparator<SavingsAccount>(){
					@Override
					public int compare(SavingsAccount arg0, SavingsAccount arg1) {
						int result= arg0.getBankAccount().getAccountHolderName().compareTo
								(arg1.getBankAccount().getAccountHolderName());
						if(count%2==0)
							return 1;
						return -1;
	
					}
				});
				
				request.setAttribute("accounts", accountSet);
				dispatcher = request.getRequestDispatcher("AccountDetails.jsp");
				dispatcher.forward(request, response);
			} catch (ClassNotFoundException | SQLException e) {
				e.printStackTrace();
			}
			break;
			
		case "/sortByBalance.mm":
			count+=1;
			try {
				Collection<SavingsAccount> accounts = savingaccountservice.getAllSavingsAccount();
				List<SavingsAccount> accountSet = new ArrayList<>(accounts);
				Collections.sort(accountSet,new Comparator<SavingsAccount>(){
					@Override
					public int compare(SavingsAccount arg0, SavingsAccount arg1) {
						int result= (int) (arg0.getBankAccount().getAccountBalance()-
								(arg1.getBankAccount().getAccountBalance()));
						if(count%2==0)
							return 1;
						return -1;
					}
				});
				
				request.setAttribute("accounts", accountSet);
				dispatcher = request.getRequestDispatcher("AccountDetails.jsp");
				dispatcher.forward(request, response);
			} catch (ClassNotFoundException | SQLException e) {
				e.printStackTrace();
			}
			break;
	
		case "/sortByAccountNumber.mm":
			count+=1;
			try {
				Collection<SavingsAccount> accounts = savingaccountservice.getAllSavingsAccount();
				List<SavingsAccount> accountSet = new ArrayList<>(accounts);
				Collections.sort(accountSet,new Comparator<SavingsAccount>(){
					@Override
					public int compare(SavingsAccount arg0, SavingsAccount arg1) {
						int result= (int) (arg0.getBankAccount().getAccountNumber()-
								(arg1.getBankAccount().getAccountNumber()));
						if(count%2==0)
							return 1;
						return -1;
					}
				});
				
				request.setAttribute("accounts", accountSet);
				dispatcher = request.getRequestDispatcher("AccountDetails.jsp");
				dispatcher.forward(request, response);
			} catch (ClassNotFoundException | SQLException e) {
				e.printStackTrace();
			}
			break;
	

	}	
	}
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
	}
}
