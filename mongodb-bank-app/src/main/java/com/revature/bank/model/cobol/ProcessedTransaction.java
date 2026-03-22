package com.revature.bank.model.cobol;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

/**
 * MongoDB document mapping for the COBOL PROCTRAN record.
 *
 * Migrated from:
 *   - copybook: PROCTRAN.cpy (record layout)
 *   - copybook: PROCDB2.cpy (DB2 table)
 *   - COBOL programs: DBCRFUN.cbl, XFRFUN.cbl, CREACC.cbl, CRECUST.cbl
 *
 * Transaction types:
 *   CRE=Credit, DEB=Debit, TFR=Transfer,
 *   OCA=Branch Create Account, OCC=Branch Create Customer,
 *   ODA=Branch Delete Account, ODC=Branch Delete Customer
 */
@Document(collection = "processed_transactions")
@CompoundIndex(name = "txn_lookup_idx",
        def = "{'sortCode': 1, 'accountNumber': 1, 'transactionDate': -1}")
public class ProcessedTransaction {

    @Id
    private String id;

    @Field("eyeCatcher")
    private String eyeCatcher = "PRTR";

    @Field("sortCode")
    private String sortCode;

    @Field("accountNumber")
    private String accountNumber;

    @Field("transactionDate")
    private LocalDate transactionDate;

    @Field("transactionTime")
    private LocalTime transactionTime;

    @Field("transactionRef")
    private String transactionRef;

    @Field("transactionType")
    private String transactionType;

    @Field("description")
    private String description;

    @Field("amount")
    private BigDecimal amount;

    public ProcessedTransaction() {
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getEyeCatcher() { return eyeCatcher; }
    public void setEyeCatcher(String eyeCatcher) { this.eyeCatcher = eyeCatcher; }

    public String getSortCode() { return sortCode; }
    public void setSortCode(String sortCode) { this.sortCode = sortCode; }

    public String getAccountNumber() { return accountNumber; }
    public void setAccountNumber(String accountNumber) { this.accountNumber = accountNumber; }

    public LocalDate getTransactionDate() { return transactionDate; }
    public void setTransactionDate(LocalDate transactionDate) { this.transactionDate = transactionDate; }

    public LocalTime getTransactionTime() { return transactionTime; }
    public void setTransactionTime(LocalTime transactionTime) { this.transactionTime = transactionTime; }

    public String getTransactionRef() { return transactionRef; }
    public void setTransactionRef(String transactionRef) { this.transactionRef = transactionRef; }

    public String getTransactionType() { return transactionType; }
    public void setTransactionType(String transactionType) { this.transactionType = transactionType; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
}
