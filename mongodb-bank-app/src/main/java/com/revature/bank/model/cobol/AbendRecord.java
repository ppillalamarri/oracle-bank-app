package com.revature.bank.model.cobol;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * MongoDB document for the COBOL ABEND record.
 *
 * Migrated from:
 *   - copybook: ABNDINFO.cpy (ABNDFILE KSDS layout)
 *   - COBOL program: ABNDPROC.cbl
 */
@Document(collection = "abend_records")
@CompoundIndex(name = "abend_key_idx", def = "{'utimeKey': 1, 'taskNumber': 1}", unique = true)
public class AbendRecord {

    @Id
    private String id;

    @Field("utimeKey")
    private long utimeKey;

    @Field("taskNumber")
    private String taskNumber;

    @Field("applId")
    private String applId;

    @Field("tranId")
    private String tranId;

    @Field("abendDate")
    private LocalDate abendDate;

    @Field("abendTime")
    private LocalTime abendTime;

    @Field("abendCode")
    private String abendCode;

    @Field("program")
    private String program;

    @Field("respCode")
    private String respCode;

    @Field("resp2Code")
    private String resp2Code;

    @Field("sqlCode")
    private String sqlCode;

    @Field("freeformText")
    private String freeformText;

    public AbendRecord() {
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public long getUtimeKey() { return utimeKey; }
    public void setUtimeKey(long utimeKey) { this.utimeKey = utimeKey; }

    public String getTaskNumber() { return taskNumber; }
    public void setTaskNumber(String taskNumber) { this.taskNumber = taskNumber; }

    public String getApplId() { return applId; }
    public void setApplId(String applId) { this.applId = applId; }

    public String getTranId() { return tranId; }
    public void setTranId(String tranId) { this.tranId = tranId; }

    public LocalDate getAbendDate() { return abendDate; }
    public void setAbendDate(LocalDate abendDate) { this.abendDate = abendDate; }

    public LocalTime getAbendTime() { return abendTime; }
    public void setAbendTime(LocalTime abendTime) { this.abendTime = abendTime; }

    public String getAbendCode() { return abendCode; }
    public void setAbendCode(String abendCode) { this.abendCode = abendCode; }

    public String getProgram() { return program; }
    public void setProgram(String program) { this.program = program; }

    public String getRespCode() { return respCode; }
    public void setRespCode(String respCode) { this.respCode = respCode; }

    public String getResp2Code() { return resp2Code; }
    public void setResp2Code(String resp2Code) { this.resp2Code = resp2Code; }

    public String getSqlCode() { return sqlCode; }
    public void setSqlCode(String sqlCode) { this.sqlCode = sqlCode; }

    public String getFreeformText() { return freeformText; }
    public void setFreeformText(String freeformText) { this.freeformText = freeformText; }
}
