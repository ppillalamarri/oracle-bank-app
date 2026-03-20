# Project-Java-Banking-App

![Admin-approves](https://user-images.githubusercontent.com/20470279/106399033-5124ed80-63e4-11eb-8555-c58e0450fbcc.gif)


# Description:


This application simulates an instance where a customer registers for an account with a bank. The following details below describes how I set up the Oracle database to the java banking application :


 	Created SQL scripts that creates users in Oracle database and table schemas for storing your bank users' account information.
  
 	Implemented Hash Code method in my JavaBeans API to generate account ID for users in my account table in Oracle.

 	This database includes at least 1 or more stored procedures for efficiency in time and space complexity.

 	This bank application connects to the SQL database using JDBC and stores all information that way.

 	I chose to use the Data Access Object(DAO) design pattern in java application for data connectivity.




# How it works for customers :


•		Customers of the bank are able to register with a username and password, and apply to open an account.

•		Customers are able to apply for joint accounts.

•		Once the account is open, customers are able to withdraw, deposit, and transfer funds between accounts (Savings account).

•		All basic validation is checked thoroughly, such as trying to input negative amounts, overdrawing from accounts etc.

# How it works for employees :

•		Employees of the bank should be able to view all of their customers information.

•		This includes, account information, Account balances, and Personal information.

•		Employees should be able to approve/deny open applications for accounts.


# How it works for Bank admins : 

• Should be able to view and edit all accounts.

•	Approving/denying accounts.

•	Withdrawing, depositing, transferring from all accounts.

•	Canceling/deleting accounts.

•	JUnit testing is expected.

•	Log each session (Log4J) so ALL transactions are logged.


# Error handling if user enters incorrect option :


![exception-out-of-bounds-caught](https://user-images.githubusercontent.com/20470279/106412950-dde89f00-6416-11eb-909f-a245903c5e9d.gif)


# Customer registers for a new checking account and is pending active :


![Customer_registers-demo](https://user-images.githubusercontent.com/20470279/103751277-09be6380-4fd6-11eb-95f2-3197fa2349d9.gif)


# Administrator approves the customer's checking account :


![Admin-approves](https://user-images.githubusercontent.com/20470279/106399033-5124ed80-63e4-11eb-8555-c58e0450fbcc.gif)



# Customer now has access to their account and decides to add Savings account :
(Savings account is automatically approved because their checking account is verified)


![savings-account-created](https://user-images.githubusercontent.com/20470279/106399352-17ed7d00-63e6-11eb-9014-bc3c494236fb.gif)



# Customer deposits money into their Checking account : 


![checkings-deposit](https://user-images.githubusercontent.com/20470279/106399651-f9888100-63e7-11eb-9b1e-0c6543c72570.gif)



# Customer decides to transfer funds from their Checking account to their Savings account :


![transfer-funds](https://user-images.githubusercontent.com/20470279/106400718-32c3ef80-63ee-11eb-967a-e4938594befa.gif)


# Admin cancels the user's account :
(Use-case scenario for fraudulent activity, inactivity, long-period of insufficient funds, or customer decides to cancel their account)



![admin-deletes-account](https://user-images.githubusercontent.com/20470279/106413062-24d69480-6417-11eb-92c6-1b66535ee526.gif)








