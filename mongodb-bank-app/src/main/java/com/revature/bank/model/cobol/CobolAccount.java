package com.revature.bank.model.cobol;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * MongoDB document mapping for the COBOL ACCOUNT record.
 *
 * Migrated from:
 *   - copybook: ACCOUNT.cpy (record layout)
 *   - copybook: ACCDB2.cpy (DB2 table declaration)
 *   - COBOL programs: CREACC.cbl, INQACC.cbl, UPDACC.cbl, DELACC.cbl
 *
 * Valid account types: ISA, SAVING, CURRENT, MORTGAGE, LOAN, PENSION, SHARES, DEPOSIT
 */
@Document(collection = "cobol_accounts")
@CompoundIndex(name = "sort_account_idx", def = "{'sortCode': 1, 'accountNumber': 1}", unique = true)
public class CobolAccount {

    @Id
    private String id;

    @Field("eyeCatcher")
    private String eyeCatcher = "ACCT";

    @Indexed
    @Field("customerNumber")
    private String customerNumber;

    @Field("sortCode")
    private String sortCode;

    @Indexed(unique = true)
    @Field("accountNumber")
    private String accountNumber;

    @Field("accountType")
    private String accountType;

    @Field("interestRate")
    private BigDecimal interestRate;

    @Field("dateOpened")
    private LocalDate dateOpened;

    @Field("overdraftLimit")
    private int overdraftLimit;

    @Field("lastStatementDate")
    private LocalDate lastStatementDate;

    @Field("nextStatementDate")
    private LocalDate nextStatementDate;

    @Field("availableBalance")
    private BigDecimal availableBalance;

    @Field("actualBalance")
    private BigDecimal actualBalance;

    public CobolAccount() {
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getEyeCatcher() { return eyeCatcher; }
    public void setEyeCatcher(String eyeCatcher) { this.eyeCatcher = eyeCatcher; }

    public String getCustomerNumber() { return customerNumber; }
    public void setCustomerNumber(String customerNumber) { this.customerNumber = customerNumber; }

    public String getSortCode() { return sortCode; }
    public void setSortCode(String sortCode) { this.sortCode = sortCode; }

    public String getAccountNumber() { return accountNumber; }
    public void setAccountNumber(String accountNumber) { this.accountNumber = accountNumber; }

    public String getAccountType() { return accountType; }
    public void setAccountType(String accountType) { this.accountType = accountType; }

    public BigDecimal getInterestRate() { return interestRate; }
    public void setInterestRate(BigDecimal interestRate) { this.interestRate = interestRate; }

    public LocalDate getDateOpened() { return dateOpened; }
    public void setDateOpened(LocalDate dateOpened) { this.dateOpened = dateOpened; }

    public int getOverdraftLimit() { return overdraftLimit; }
    public void setOverdraftLimit(int overdraftLimit) { this.overdraftLimit = overdraftLimit; }

    public LocalDate getLastStatementDate() { return lastStatementDate; }
    public void setLastStatementDate(LocalDate lastStatementDate) { this.lastStatementDate = lastStatementDate; }

    public LocalDate getNextStatementDate() { return nextStatementDate; }
    public void setNextStatementDate(LocalDate nextStatementDate) { this.nextStatementDate = nextStatementDate; }

    public BigDecimal getAvailableBalance() { return availableBalance; }
    public void setAvailableBalance(BigDecimal availableBalance) { this.availableBalance = availableBalance; }

    public BigDecimal getActualBalance() { return actualBalance; }
    public void setActualBalance(BigDecimal actualBalance) { this.actualBalance = actualBalance; }
}
