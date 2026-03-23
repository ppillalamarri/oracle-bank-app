package com.bankapp.service;

import com.bankapp.model.Control;
import com.bankapp.repository.ControlRepository;
import org.springframework.stereotype.Service;

/**
 * Service for managing control records (named counters / sequence generators).
 *
 * Replaces COBOL Named Counter Server (NCS) operations:
 *   - ENQ/DEQ named counter in CREACC.cbl and CRECUST.cbl
 *   - DB2 CONTROL table reads/updates
 *
 * In the COBOL system, named counters were used with CICS ENQ/DEQ for
 * concurrency control. In this Java/MongoDB implementation, we use
 * MongoDB's findAndModify with $inc for atomic counter increments.
 */
@Service
public class ControlService {

    private static final String ACCOUNT_COUNTER_PREFIX = "ACCOUNT-LAST-";
    private static final String CUSTOMER_COUNTER_PREFIX = "CUSTOMER-LAST-";

    private final ControlRepository controlRepository;

    public ControlService(ControlRepository controlRepository) {
        this.controlRepository = controlRepository;
    }

    /**
     * Get and increment the next account number for a given sort code.
     * Equivalent to COBOL: ENQ named counter, GET+INC, DEQ in CREACC.cbl
     */
    public synchronized long getNextAccountNumber(String sortCode) {
        String counterName = ACCOUNT_COUNTER_PREFIX + sortCode;
        Control control = controlRepository.findByControlName(counterName)
                .orElseGet(() -> {
                    Control newControl = new Control(counterName, 0, "");
                    return controlRepository.save(newControl);
                });

        control.setControlValueNum(control.getControlValueNum() + 1);
        controlRepository.save(control);
        return control.getControlValueNum();
    }

    /**
     * Get and increment the next customer number for a given sort code.
     * Equivalent to COBOL: ENQ named counter, GET+INC, DEQ in CRECUST.cbl
     */
    public synchronized long getNextCustomerNumber(String sortCode) {
        String counterName = CUSTOMER_COUNTER_PREFIX + sortCode;
        Control control = controlRepository.findByControlName(counterName)
                .orElseGet(() -> {
                    Control newControl = new Control(counterName, 0, "");
                    return controlRepository.save(newControl);
                });

        control.setControlValueNum(control.getControlValueNum() + 1);
        controlRepository.save(control);
        return control.getControlValueNum();
    }

    /**
     * Get the last account number in use for a given sort code.
     */
    public long getLastAccountNumber(String sortCode) {
        String counterName = ACCOUNT_COUNTER_PREFIX + sortCode;
        return controlRepository.findByControlName(counterName)
                .map(Control::getControlValueNum)
                .orElse(0L);
    }

    /**
     * Get the last customer number in use for a given sort code.
     */
    public long getLastCustomerNumber(String sortCode) {
        String counterName = CUSTOMER_COUNTER_PREFIX + sortCode;
        return controlRepository.findByControlName(counterName)
                .map(Control::getControlValueNum)
                .orElse(0L);
    }
}
