# COBOL to Java Migration Mapping

## Complete Program-to-Class Mapping

| COBOL Program | Java Class | Layer | Status |
|---|---|---|---|
| CREACC.cbl | AccountService.createAccount() | Service | Migrated |
| INQACC.cbl | AccountService.getAccount() | Service | Migrated |
| INQACCCU.cbl | AccountService.getAccountsByCustomer() | Service | Migrated |
| UPDACC.cbl | AccountService.updateAccount() | Service | Migrated |
| DELACC.cbl | AccountService.deleteAccount() | Service | Migrated |
| CRECUST.cbl | CustomerService.createCustomer() | Service | Migrated |
| INQCUST.cbl | CustomerService.getCustomer() | Service | Migrated |
| UPDCUST.cbl | CustomerService.updateCustomer() | Service | Migrated |
| DELCUS.cbl | CustomerService.deleteCustomer() | Service | Migrated |
| DBCRFUN.cbl | TransactionService.debitCredit() | Service | Migrated |
| XFRFUN.cbl | TransactionService.transfer() | Service | Migrated |
| CRDTAGY1-5.cbl | CreditAgencyService | Service | Migrated |
| BANKDATA.cbl | DataSeederService | Service | Migrated |
| GETCOMPY.cbl | BankInfoService.getCompanyName() | Service | Migrated |
| GETSCODE.cbl | BankInfoService.getSortCode() | Service | Migrated |
| ABNDPROC.cbl | AbendLoggingService | Service | Migrated |
| BNKMENU.cbl | BankMenuService | Service | Migrated |
| BNK1CAC.cbl | AccountController.createAccount() | Controller | Migrated |
| BNK1CCA.cbl | TransactionController.debitCredit() | Controller | Migrated |
| BNK1CCS.cbl | CustomerController.createCustomer() | Controller | Migrated |
| BNK1CRA.cbl | TransactionController.debitCredit() | Controller | Migrated |
| BNK1DAC.cbl | AccountController.deleteAccount() | Controller | Migrated |
| BNK1DCS.cbl | CustomerController.deleteCustomer() | Controller | Migrated |
| BNK1TFN.cbl | TransactionController.transfer() | Controller | Migrated |
| BNK1UAC.cbl | AccountController.updateAccount() | Controller | Migrated |

## Copybook-to-Model Mapping

| Copybook | Java Model | MongoDB Collection |
|---|---|---|
| CUSTOMER.cpy | Customer.java | customers |
| ACCOUNT.cpy / ACCDB2.cpy | Account.java | accounts |
| PROCTRAN.cpy / PROCDB2.cpy | ProcessedTransaction.java | processed_transactions |
| CONTDB2.cpy / ACCTCTRL.cpy / CUSTCTRL.cpy | Control.java | controls |
| ABNDINFO.cpy | AbendRecord.java | abend_records |
| SORTCODE.cpy | application.yml (bank.default-sort-code) | - |
| GETCOMPY.cpy | BankInfoService (constant) | - |
| GETSCODE.cpy | BankInfoService (config) | - |

## Architecture Mapping

| COBOL/CICS Concept | Java/Spring Equivalent |
|---|---|
| CICS Transaction (TRANSID) | REST API Endpoint |
| BMS Map (SEND/RECEIVE MAP) | REST Request/Response JSON |
| COMMAREA | Method parameters / DTOs |
| VSAM KSDS | MongoDB Collection |
| DB2 Table | MongoDB Collection |
| CICS Named Counter (ENQ/DEQ) | synchronized + MongoDB atomic ops |
| CICS LINK (subroutine) | @Service method call |
| CICS RUN TRANSID (async) | @Async / parallel streams |
| ABNDPROC (abend handler) | @RestControllerAdvice + AbendLoggingService |
| BANKDATA (batch init) | DataSeederService (CommandLineRunner) |
| COPY statement | Java import |
| WORKING-STORAGE | Instance fields |
| LINKAGE SECTION | Method parameters |
| PROCEDURE DIVISION | Method body |
| PERFORM paragraph | Private method call |
| EVALUATE/WHEN | switch/if-else |
| level-88 condition | enum / Set.of() constants |
