package com.revature.bank.model.cobol;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDate;

/**
 * MongoDB document mapping for the COBOL CUSTOMER record.
 *
 * Migrated from:
 *   - copybook: CUSTOMER.cpy (record layout)
 *   - copybook: CUSTCTRL.cpy (control record)
 *   - COBOL programs: CRECUST.cbl, INQCUST.cbl, UPDCUST.cbl, DELCUS.cbl
 */
@Document(collection = "customers")
@CompoundIndex(name = "sort_customer_idx", def = "{'sortCode': 1, 'customerNumber': 1}", unique = true)
public class Customer {

    @Id
    private String id;

    @Field("eyeCatcher")
    private String eyeCatcher = "CUST";

    @Field("sortCode")
    private String sortCode;

    @Indexed(unique = true)
    @Field("customerNumber")
    private String customerNumber;

    @Field("name")
    private String name;

    @Field("address")
    private String address;

    @Field("dateOfBirth")
    private LocalDate dateOfBirth;

    @Field("creditScore")
    private int creditScore;

    @Field("creditScoreReviewDate")
    private LocalDate creditScoreReviewDate;

    public Customer() {
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getEyeCatcher() { return eyeCatcher; }
    public void setEyeCatcher(String eyeCatcher) { this.eyeCatcher = eyeCatcher; }

    public String getSortCode() { return sortCode; }
    public void setSortCode(String sortCode) { this.sortCode = sortCode; }

    public String getCustomerNumber() { return customerNumber; }
    public void setCustomerNumber(String customerNumber) { this.customerNumber = customerNumber; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public LocalDate getDateOfBirth() { return dateOfBirth; }
    public void setDateOfBirth(LocalDate dateOfBirth) { this.dateOfBirth = dateOfBirth; }

    public int getCreditScore() { return creditScore; }
    public void setCreditScore(int creditScore) { this.creditScore = creditScore; }

    public LocalDate getCreditScoreReviewDate() { return creditScoreReviewDate; }
    public void setCreditScoreReviewDate(LocalDate creditScoreReviewDate) { this.creditScoreReviewDate = creditScoreReviewDate; }
}
