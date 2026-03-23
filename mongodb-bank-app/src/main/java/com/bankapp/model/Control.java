package com.bankapp.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

/**
 * MongoDB document mapping for the COBOL CONTROL record.
 *
 * Migrated from:
 *   - copybook: CONTDB2.cpy (DB2 table declaration)
 *   - copybook: ACCTCTRL.cpy (Account control record)
 *   - copybook: CUSTCTRL.cpy (Customer control record)
 *
 * Used as a named counter / sequence generator for Account and Customer numbers.
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
    private String controlValueStr;

    public Control() {
    }

    public Control(String controlName, long controlValueNum, String controlValueStr) {
        this.controlName = controlName;
        this.controlValueNum = controlValueNum;
        this.controlValueStr = controlValueStr;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getControlName() {
        return controlName;
    }

    public void setControlName(String controlName) {
        this.controlName = controlName;
    }

    public long getControlValueNum() {
        return controlValueNum;
    }

    public void setControlValueNum(long controlValueNum) {
        this.controlValueNum = controlValueNum;
    }

    public String getControlValueStr() {
        return controlValueStr;
    }

    public void setControlValueStr(String controlValueStr) {
        this.controlValueStr = controlValueStr;
    }
}
