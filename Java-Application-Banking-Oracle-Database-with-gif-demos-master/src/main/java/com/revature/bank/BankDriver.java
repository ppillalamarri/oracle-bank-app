package com.revature.bank;


import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.apache.log4j.Logger;
import org.apache.log4j.chainsaw.Main;

import com.revature.beans.Account;
import com.revature.beans.User;
import com.revature.data.AccountDao;
import com.revature.data.AccountFake;
import com.revature.data.UserDao;
import com.revature.data.UserFake;
//import com.revature.driver.Main;

public class BankDriver {
	private static Logger log = Logger.getLogger(Main.class);

	public static void main(String[] args) {
		Scanner scan = new Scanner(System.in);
		boolean keepLooping = true;
		boolean customerLoop = false;
		boolean adminLoop = false;
		boolean tellerLoop = false;
		UserDao ud = new UserFake();
		AccountDao ad = new AccountFake();

		System.out.println("Welcome to Roger's Bank of America.");

		while (keepLooping) {
			System.out.println("How may I help you today?");
			System.out.println("Type 1 to log in, type 2 to create an account or type 3 to quit.");
			System.out.print("Input: ");
			String choice = scan.nextLine();

			if (choice.length() > 1) {
				System.out.println("You must input 1, 2 or 3 Thank You.");
				continue;
			}
			char cChoice = choice.charAt(0);
			if (!Character.isDigit(cChoice)) {
				System.out.println("You must input 1, 2 or 3 Thank You.");
				continue;
			}

			int intChoice = Integer.parseInt(choice);
			if (intChoice == 1) {

				System.out.println("Username: ");
				String username = scan.nextLine();

				System.out.println("Password: ");
				String password = scan.nextLine();

				User u;
				//if (username.contentEquals("z")) { // dont forget to delete this shortcut
					//u = ud.login("rrichar1", "bhearts1");
				//} else {
				//	u = ud.login(username, password);
				//}
				u = ud.login(username, password);
				if (u != null) {
					System.out.println("Logged in as " + u);
				} else {
					System.out.println("Incorrect username or password.");
					continue;
				}

				String whoAmI = u.getType();

				if (whoAmI.equals("admin")) {
					adminLoop = true;
				} else if (whoAmI.equals("employee")) {
					tellerLoop = true;
				} else if (whoAmI.equals("customer")) {
					customerLoop = true;
				}

				while (adminLoop) {
					System.out.println("");
					System.out.println("WELCOME ADMIN");
					System.out.println("");
					System.out.println(
							"Type 1 to view accounts, type 2 to withdraw/deposit/transfer, type 3 to cancel account, type 4 approve/deny or type 5 to quit.");
					System.out.print("Input: ");
					String adminChoice = scan.nextLine();
					int intAdminChoice = Integer.parseInt(adminChoice);
					if (intAdminChoice == 1) {
						printInfo(ud, ad, u);
					} else if (intAdminChoice == 2) {
						printInfo(ud, ad, u);
						System.out.println("Type the account id of the account you want to withdraw/deposit/transfer from");
						String accountChoice = scan.nextLine();
						int intAccountChoice = Integer.parseInt(accountChoice);
						Account getAccount = ad.getAccount(intAccountChoice);
						double balance = getAccount.getBalance();
						System.out.println("The account balance is: " + balance);
						System.out.println("Now click 1 to withdraw, 2 to deposit or 3 to transfer");
						String option = scan.nextLine();
						int intOption = Integer.parseInt(option);
						if (intOption == 1) {
							Withdraw(scan, ad, getAccount, balance);
						} else if (intOption == 2) {
							Deposit(scan, ad, getAccount, balance);
						} else if (intOption == 3) {
							Transfer(scan, ad, getAccount, balance);
						}

						// private static void Withdraw(Scanner scan, AccountDao ad, Account
						// userAccount, double balance)
					} else if (intAdminChoice == 3) {
						System.out.println("ADD ADMIN cancel account");
						printInfo(ud, ad, u);
						System.out.println("Type the user ID of the account you would like to delete");
						String accDelete = scan.nextLine();
						int intAccDelete = Integer.parseInt(accDelete);
						System.out.println("accdelete " + intAccDelete);
						// check here to make sure the user select an id in the table
						User removingUser = ud.getUserU(intAccDelete);

						System.out.println(removingUser.toString());

						int account_id = removingUser.getAccount_id();
						int account_id2 = removingUser.getAccount_id2();
						//System.out.println("made it " + account_id);
						//System.out.println("madeit here " + account_id2);
						ud.deleteUser(account_id, removingUser.getId());
						/*
						if (account_id > 0) {
							ad.deleteAccount(account_id);
						}
						if (account_id2 > 0) {
							ad.deleteAccount(account_id2);
						}

						ud.deleteUser(removingUser);
						*/
						// PROBLEMS HERE CONSTRAINT ISSUE TRY and solve with stored procedure

					} else if (intAdminChoice == 4) {
						approveDeny(ud, ad, u, scan);
					} else if (intAdminChoice == 5) {
						adminLoop = false;
						customerLoop = false;
					}
				}
				while (tellerLoop) {
					System.out.println("WELCOME EMPLOYEE");
					System.out.println("Type 1 to view info, type 2 to approve/deny or type 3 to quit.");
					System.out.print("Input: ");
					String tellerChoice = scan.nextLine();
					int intTellerChoice = Integer.parseInt(tellerChoice);
					if (intTellerChoice == 1) {
						printInfo(ud, ad, u);
					} else if (intTellerChoice == 2) {
						approveDeny(ud, ad, u, scan);
					} else if (intTellerChoice == 3) {
						tellerLoop = false;
						customerLoop = false;
					}
				}
				while (customerLoop) {
					/*
					 * ADD approval stuff
					 * here-------------------------------------------------------------------------
					 * --------
					 */
					int id = u.getAccount_id();
					Account getAccount = ad.getAccount(id);
					System.out.println(getAccount.getApproved());
					if(getAccount.getApproved().equals("pending")){
						System.out.println("Sorry your account is still pending");
						break;
					}

					int curAccount_id = u.getAccount_id();
					int curAccount_id2 = u.getAccount_id2();
					
					int account_id = 0;

					if (curAccount_id > 0 && curAccount_id2 > 0) {
						System.out.println("You now have a Savings account added with your Checking Account. "
								+ "Type 1 to access your checkings, type 2 to access your savings.");
						String option = scan.nextLine();
						int intOption = Integer.parseInt(option);
						Account curAccount = ad.getAccount(curAccount_id);

						if (intOption == 1) {
							if (curAccount.getType().equals("checkings")) {
								account_id = curAccount_id;
							} else {
								account_id = curAccount_id2;
							}

						} else if (intOption == 2) {
							if (curAccount.getType().equals("checkings")) {
								account_id = curAccount_id2;
							} else {
								account_id = curAccount_id;
							}
						}
					} else if (curAccount_id > 0) {
						account_id = u.getAccount_id();
					} else if (curAccount_id2 > 0) {
						account_id = u.getAccount_id2();
					}

					// int account_id = u.getAccount_id();
					Account userAccount = ad.getAccount(account_id);
					double balance = userAccount.getBalance();
					System.out.println("Your balance is: " + balance);
					System.out.println("");
					System.out.println("WELCOME CUSTOMER");
					System.out.println("");
					System.out.println(
							"Type 1 to withdraw, type 2 to deposit, type 3 to transfer, type 4 to apply for another account or type 5 to quit.");
					System.out.print("Input: ");
					String customerChoice = scan.nextLine();
					int intCustomerChoice = Integer.parseInt(customerChoice);

					if (intCustomerChoice == 1) {
						Withdraw(scan, ad, userAccount, balance);
					} else if (intCustomerChoice == 2) {
						Deposit(scan, ad, userAccount, balance);
					} else if (intCustomerChoice == 3) {
						Transfer(scan, ad, userAccount, balance);
					} else if (intCustomerChoice == 4) {

						if (curAccount_id > 0 && curAccount_id2 > 0) {
							System.out.println("You already have both a checkings & savings account");
							continue;
						}
						Account myAccount = ad.getAccount(curAccount_id);
						String curType = myAccount.getType();
						String accountType;
						if (curType.equals("checkings")) {
							accountType = "savings";
						} else {
							accountType = "checkings";
						}
						String curpending = myAccount.getApproved();
						String pending;
						if (curpending.equals("pending")) {
							pending = "pending";
						} else {
							pending = "accepted";
						}
						
						int maxID = (ad.getMaxID() + 1);

						Account a = new Account();
						a.setId(maxID);
						a.setBalance(0.01);
						a.setType(accountType);
						a.setApproved(pending);
						a.setJoint(0);
						
						u.setAccount_id2(maxID);
						
						ad.saveAccount(a);
						ud.updateAccount2(maxID, u.getId());
						
						System.out.println("--Account " + accountType + " Created--");

					} else if (intCustomerChoice == 5) {
						customerLoop = false;
					}
				}

			} else if (intChoice == 2) {
				User u = new User();
				Account a = new Account();
				String accountType;
				int ID = ud.getMAXID() + 1;
				int account_id = ad.getMaxID() + 1;

				System.out.println("Enter your First Name: ");
				String newFirst = scan.nextLine();

				System.out.println("Enter your Last Name: ");
				String newLast = scan.nextLine();

				System.out.println("Enter a Username: ");
				String newUsername = scan.nextLine();

				System.out.println("Enter a Password: ");
				String newPassword = scan.nextLine();
				
				System.out.println("Enter your Email: ");
				String newEmail = scan.nextLine();
				
				System.out.println("Enter your Phone number: ");
				String newPhone = scan.nextLine();
				
				System.out.println("Enter your Street Address: ");
				String newAddress = scan.nextLine();
				
				System.out.println("Enter the City: ");
				String newCity = scan.nextLine();
				
				System.out.println("Enter the State: ");
				String newState = scan.nextLine();
				
				System.out.println("Enter the Zip: ");
				String newZip = scan.nextLine();
								
				System.out.println("What type account would you like to make?");
				System.out.println("Click 1 for checkings, click 2 for savings.");
				String newAccount = scan.nextLine();
				int newAccountInt = Integer.parseInt(newAccount);
				if (newAccountInt == 1) {
					accountType = "checkings";
				} else if (newAccountInt == 2) {
					accountType = "savings";
				} else {
					System.out.println("Input must be 1 or 2");
					break;
				}
				System.out.println("accID " + account_id);
				System.out.println("UserID " + ID);

				a.setId(account_id);
				a.setBalance(0.01);
				a.setType(accountType);
				a.setApproved("pending");
				a.setJoint(0);

				u.setId(ID);
				u.setFirstname(newFirst);
				u.setLastname(newLast);
				u.setUsername(newUsername);
				u.setPassword(newPassword);
				u.setType("customer");
				u.setAccount_id(account_id);

				u.setEmail(newEmail);
				u.setPhone(newPhone);
				u.setAddress(newAddress);
				u.setCity(newCity);
				u.setState(newState);
				u.setZip(newZip);

				ad.saveAccount(a);
				ud.saveUser(u);

				System.out.println("-- Account Created --");

			} else if (intChoice == 3) {
				System.out.println("Thank you for your business. Please come back soon!");
				System.exit(0);
			} else {
				System.out.println("You must input 1, 2 or 3 Thank You.");
			}
		}
	}

	private static void printInfo(UserDao ud, AccountDao ad, User u) {
		List<User> Users = new ArrayList<User>();
		Users = ud.getUsers();
		for (int i = 0; i < Users.size(); i++) {
			User curUser = Users.get(i);
			Account curAccount = ad.getAccount(curUser.getAccount_id());
			if(curUser.getAccount_id2() > 0) {
				Account curAccount2 = ad.getAccount(curUser.getAccount_id2());
				System.out.println(curUser.toString() + " " + curAccount.toString() + "\n Account2:" + curAccount2.toString());
			}else {
				System.out.println(curUser.toString() + " " + curAccount.toString());
			}

		}
	}

	private static void approveDeny(UserDao ud, AccountDao ad, User u, Scanner scan) {
		System.out.println("approve/denying");
		List<Account> Accounts = new ArrayList<Account>();

		Accounts = ad.getPending();
		System.out.println(Accounts.size());
		for (int i = 0; i < Accounts.size(); i++) {
			Account curAccount = Accounts.get(i);
			User curUser = ud.getUser(curAccount.getId());
			System.out.println(curUser.toString() + curAccount.toString());
		}
		System.out.println("Type 1 to examine, Type 2 to quit");
		System.out.print("Input: ");
		String examine = scan.nextLine();
		int intExamine = Integer.parseInt(examine);
		if (intExamine == 1) {
			System.out.println("Type the account index you want to examine");
			String indexExamine = scan.nextLine();
			int intIndexExamine = Integer.parseInt(indexExamine);
			int getAccountID = 0;
			for (int i = 0; i < Accounts.size(); i++) {
				Account getAccount = Accounts.get(i);
				User getUser = ud.getUser(getAccount.getId());
				if (getAccount.getId() == intIndexExamine) {
					getAccountID = getAccount.getId();
					System.out.println(getUser.toString() + getAccount.toString());
				}
			}

			System.out.println("Would you like to accept/deny this account? 1 to accept 2 to deny.");
			String approved = scan.nextLine();
			int intApproved = Integer.parseInt(approved);
			if (intApproved == 1) {
				ad.updateApproved(getAccountID);
				System.out.println("CONGRATS ACCEPTED!");
			} else {
				// delete user & account
				ad.deleteAccount(getAccountID);
				ud.deleteUserAccount(getAccountID); // check if user has other account first
				System.out.println("DENIED");
			}

		}
	}

	private static void Withdraw(Scanner scan, AccountDao ad, Account userAccount, double balance) {
		System.out.println("How much would you like to withdraw?");
		String amt = scan.nextLine();
		double doubleAmt = Double.parseDouble(amt);

		if (doubleAmt > balance) {
			System.out.println("Insufficient funds");
		} else {
			ad.updateBalance(userAccount, doubleAmt, false);
		}
		System.out.println("Money withdrawn your new balance is " + (balance - doubleAmt));
	}

	private static void Deposit(Scanner scan, AccountDao ad, Account userAccount, double balance) {
		System.out.println("How much would you like to deposit?");
		String amt = scan.nextLine();
		double doubleAmt = Double.parseDouble(amt);
		ad.updateBalance(userAccount, doubleAmt, true);
		System.out.println("Money Deposited your new balance is " + (balance + doubleAmt));
	}

	private static void Transfer(Scanner scan, AccountDao ad, Account userAccount, double balance) {
		System.out.println("How much would you like to transfer?");
		String amt = scan.nextLine();
		double doubleAmt = Double.parseDouble(amt);
		if (doubleAmt > balance) {
			System.out.println("Insufficient funds");
		} else {
			System.out.println("Now Enter the Account ID of the account you'd like to transfer money to.");
			String reciever = scan.nextLine();
			int intReciever = Integer.parseInt(reciever);
			// check here to see if the account is in accounts then update
			Account otherAccount = ad.getAccount(intReciever);
			ad.updateBalance(otherAccount, doubleAmt, true);
			ad.updateBalance(userAccount, doubleAmt, false);
		}
		System.out.println("Money transfered your new balance is " + (balance - doubleAmt));
	}
}
		