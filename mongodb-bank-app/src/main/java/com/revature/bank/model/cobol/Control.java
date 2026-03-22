package com.revature.bank.model.cobol;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

/**
 * MongoDB document for the COBOL CONTROL record (named counters).
 *
 * Migrated from:
 *   - copybook: CONTDB2.cpy (DB2 CONTROL table)
 *   - copybook: ACCTCTRL.cpy, CUSTCTRL.cpy
 *   - COBOL programs: CREACC.cbl (ENQ/DEQ), CRECUST.cbl (ENQ/DEQ)
 *
 * Named counters:
 *   ACCOUNT-LAST-{sortCode}  - last account number issued
 *   CUSTOMER-LAST-{sortCode} - last customer number issued
 *
 * Uses MongoDB findAndModify with $inc for atomic increments
 * (replaces CICS Named Counter Server with ENQ/DEQ).
 */
@Document(collection = "controls")
public class Control {

    @Id
    private String id;

    @Indexed(unique = true)
    @Field("controlName")
    private String controlName;

    @Field("controlValueNum")
    private long controlValueNum;

    @Field("controlValueStr")
    private String controlValueStr = "";

    public Control() {
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getControlName() { return controlName; }
    public void setControlName(String controlName) { this.controlName = controlName; }

    public long getControlValueNum() { return controlValueNum; }
    public void setControlValueNum(long controlValueNum) { this.controlValueNum = controlValueNum; }

    public String getControlValueStr() { return controlValueStr; }
    public void setControlValueStr(String controlValueStr) { this.controlValueStr = controlValueStr; }
}
