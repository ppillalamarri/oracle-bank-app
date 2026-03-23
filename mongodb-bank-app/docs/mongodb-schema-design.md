# MongoDB Schema Design - COBOL to Java/MongoDB Migration

## Overview
This document describes the MongoDB schema design that replaces the COBOL/DB2/VSAM data structures
in the legacy banking application.

## Collections

### 1. `customers` Collection
**Migrated from:** CUSTOMER.cpy (copybook), CUSTCTRL.cpy, VSAM CUSTOMER file, DB2 CUSTOMER table
**COBOL Programs:** CRECUST.cbl, INQCUST.cbl, UPDCUST.cbl, DELCUS.cbl

```json
{
  "_id": "ObjectId",
  "eyeCatcher": "CUST",
  "sortCode": "987654",
  "customerNumber": "0000000001",
  "name": "Mr John Smith",
  "address": "123 Main Street, London",
  "dateOfBirth": "ISODate('1990-01-15')",
  "creditScore": 750,
  "creditScoreReviewDate": "ISODate('2024-07-15')"
}
```

**Indexes:**
- `{ sortCode: 1, customerNumber: 1 }` — unique compound (replaces VSAM KSDS key)
- `{ customerNumber: 1 }` — unique (fast lookup by customer number)

**COBOL Field Mapping:**
| COBOL Field (CUSTOMER.cpy) | MongoDB Field | Type | Notes |
|---|---|---|---|
| CUSTOMER-EYECATCHER | eyeCatcher | String | Always "CUST" |
| CUSTOMER-SORTCODE | sortCode | String | 6-digit bank sort code |
| CUSTOMER-NUMBER | customerNumber | String | 10-digit zero-padded |
| CUSTOMER-NAME | name | String | Title + name |
| CUSTOMER-ADDRESS | address | String | Full address |
| CUSTOMER-DATE-OF-BIRTH | dateOfBirth | ISODate | Was PIC 9(8) YYYYMMDD |
| CUSTOMER-CREDIT-SCORE | creditScore | int | 0-999 from CRDTAGY1-5 |
| CUSTOMER-CS-REVIEW-DATE | creditScoreReviewDate | ISODate | Next review date |

---

### 2. `accounts` Collection
**Migrated from:** ACCOUNT.cpy (copybook), ACCDB2.cpy (DB2 table), ACCTCTRL.cpy
**COBOL Programs:** CREACC.cbl, INQACC.cbl, INQACCCU.cbl, UPDACC.cbl, DELACC.cbl

```json
{
  "_id": "ObjectId",
  "eyeCatcher": "ACCT",
  "customerNumber": "0000000001",
  "sortCode": "987654",
  "accountNumber": "00000001",
  "accountType": "SAVING",
  "interestRate": "NumberDecimal('1.50')",
  "dateOpened": "ISODate('2024-01-15')",
  "overdraftLimit": 1000,
  "lastStatementDate": "ISODate('2024-01-15')",
  "nextStatementDate": "ISODate('2024-02-15')",
  "availableBalance": "NumberDecimal('5000.00')",
  "actualBalance": "NumberDecimal('5000.00')"
}
```

**Indexes:**
- `{ sortCode: 1, accountNumber: 1 }` — unique compound (replaces DB2 primary key)
- `{ customerNumber: 1 }` — non-unique (for INQACCCU lookups)
- `{ accountNumber: 1 }` — unique

**Valid Account Types:** ISA, SAVING, CURRENT, MORTGAGE, LOAN, PENSION, SHARES, DEPOSIT

**COBOL Field Mapping:**
| COBOL Field (ACCOUNT.cpy / ACCDB2.cpy) | MongoDB Field | Type | Notes |
|---|---|---|---|
| ACCOUNT-EYECATCHER | eyeCatcher | String | Always "ACCT" |
| ACCOUNT-CUSTOMER-NUMBER | customerNumber | String | FK to customers |
| ACCOUNT-SORTCODE | sortCode | String | 6-digit |
| ACCOUNT-NUMBER | accountNumber | String | 8-digit zero-padded |
| ACCOUNT-TYPE | accountType | String | See valid types |
| ACCOUNT-INTEREST-RATE | interestRate | Decimal128 | Was COMP-3 S9(4)V99 |
| ACCOUNT-OPENED | dateOpened | ISODate | Was PIC X(10) DD/MM/YYYY |
| ACCOUNT-OVERDRAFT-LIMIT | overdraftLimit | int | Was S9(9) COMP |
| ACCOUNT-LAST-STMT-DT | lastStatementDate | ISODate | |
| ACCOUNT-NEXT-STMT-DT | nextStatementDate | ISODate | |
| ACCOUNT-AVAIL-BALANCE | availableBalance | Decimal128 | Was COMP-3 S9(10)V99 |
| ACCOUNT-ACTUAL-BALANCE | actualBalance | Decimal128 | Was COMP-3 S9(10)V99 |

---

### 3. `processed_transactions` Collection
**Migrated from:** PROCTRAN.cpy (copybook), PROCDB2.cpy (DB2 table)
**COBOL Programs:** DBCRFUN.cbl, XFRFUN.cbl, CREACC.cbl, CRECUST.cbl

```json
{
  "_id": "ObjectId",
  "eyeCatcher": "PRTR",
  "sortCode": "987654",
  "accountNumber": "00000001",
  "transactionDate": "ISODate('2024-01-15')",
  "transactionTime": "12:30:45",
  "transactionRef": "000123456789",
  "transactionType": "CRE",
  "description": "CRE 500.00",
  "amount": "NumberDecimal('500.00')"
}
```

**Indexes:**
- `{ sortCode: 1, accountNumber: 1, transactionDate: -1 }` — compound (replaces DB2 cursor)

**Transaction Types (from PROCTRAN.cpy level-88 values):**
| Code | Description | Origin |
|---|---|---|
| CRE | Credit | DBCRFUN.cbl |
| DEB | Debit | DBCRFUN.cbl |
| TFR | Transfer | XFRFUN.cbl |
| OCA | Branch Create Account | CREACC.cbl |
| OCC | Branch Create Customer | CRECUST.cbl |
| ODA | Branch Delete Account | DELACC.cbl |
| ODC | Branch Delete Customer | DELCUS.cbl |
| ICA | Web Create Account | BNK1CAC.cbl |
| ICC | Web Create Customer | BNK1CCS.cbl |
| IDA | Web Delete Account | BNK1DAC.cbl |
| IDC | Web Delete Customer | BNK1DCS.cbl |
| PCR | Payment Credit | DBCRFUN.cbl |
| PDR | Payment Debit | DBCRFUN.cbl |
| CHA | Cheque Acknowledged | - |
| CHF | Cheque Failure | - |
| CHI | Cheque Paid In | - |
| CHO | Cheque Paid Out | - |
| OCS | Create Standing Order | - |

---

### 4. `controls` Collection
**Migrated from:** CONTDB2.cpy (DB2 CONTROL table), ACCTCTRL.cpy, CUSTCTRL.cpy
**COBOL Programs:** CREACC.cbl (ENQ/DEQ), CRECUST.cbl (ENQ/DEQ)

```json
{
  "_id": "ObjectId",
  "controlName": "ACCOUNT-LAST-987654",
  "controlValueNum": 42,
  "controlValueStr": ""
}
```

**Indexes:**
- `{ controlName: 1 }` — unique

**Named Counters:**
- `ACCOUNT-LAST-{sortCode}` — last account number issued
- `CUSTOMER-LAST-{sortCode}` — last customer number issued

**COBOL Equivalent:** CICS Named Counter Server (NCS) with ENQ/DEQ for concurrency.
Java uses `synchronized` methods; MongoDB could use `findAndModify` with `$inc` for atomic increments.

---

### 5. `abend_records` Collection (NEW)
**Migrated from:** ABNDPROC.cbl, ABNDINFO.cpy (ABNDFILE KSDS)

```json
{
  "_id": "ObjectId",
  "utimeKey": 123456789012345,
  "taskNumber": "1234",
  "applId": "CICSBSA1",
  "tranId": "OMEN",
  "abendDate": "ISODate('2024-01-15')",
  "abendTime": "12:30:45",
  "abendCode": "HBNK",
  "program": "BNKMENU",
  "respCode": "16",
  "resp2Code": "0",
  "sqlCode": "0",
  "freeformText": "A010 - RETURN TRANSID(MENU) FAIL. EIBRESP=16 RESP2=0"
}
```

**Indexes:**
- `{ utimeKey: 1, taskNumber: 1 }` — unique compound (replaces VSAM KSDS RIDFLD)

---

## Migration from COBOL Data Access Patterns

| COBOL Pattern | MongoDB Equivalent |
|---|---|
| VSAM KSDS READ by key | `findBySortCodeAndCustomerNumber()` |
| VSAM KSDS WRITE | `repository.save()` |
| VSAM KSDS REWRITE | `repository.save()` (upsert) |
| VSAM KSDS DELETE | `repository.delete()` |
| DB2 SELECT with cursor | Spring Data query methods |
| DB2 INSERT | `repository.save()` |
| DB2 UPDATE | `repository.save()` |
| DB2 DELETE | `repository.delete()` |
| CICS Named Counter ENQ/DEQ | `synchronized` + `findAndModify` |
| CICS LINK (subroutine call) | Spring `@Service` method calls |
| CICS RUN TRANSID (async) | `@Async` or parallel streams |
| BMS SEND MAP / RECEIVE MAP | REST API request/response |
