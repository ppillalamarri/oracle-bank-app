package com.revature.bank.service.cobol;

import com.revature.bank.model.cobol.Control;
import com.revature.bank.repository.cobol.ControlRepository;
import org.springframework.stereotype.Service;

/**
 * Service for managing named counters (account/customer number generation).
 *
 * Replaces CICS Named Counter Server (NCS) with ENQ/DEQ.
 * Uses synchronized methods for thread safety; MongoDB findAndModify
 * with $inc could be used for distributed concurrency.
 */
@Service
public class ControlService {

    private final ControlRepository controlRepository;

    public ControlService(ControlRepository controlRepository) {
        this.controlRepository = controlRepository;
    }

    public synchronized long getNextAccountNumber(String sortCode) {
        String controlName = "ACCOUNT-LAST-" + sortCode;
        return incrementCounter(controlName);
    }

    public synchronized long getNextCustomerNumber(String sortCode) {
        String controlName = "CUSTOMER-LAST-" + sortCode;
        return incrementCounter(controlName);
    }

    private long incrementCounter(String controlName) {
        Control control = controlRepository.findByControlName(controlName)
                .orElseGet(() -> {
                    Control newControl = new Control();
                    newControl.setControlName(controlName);
                    newControl.setControlValueNum(0);
                    return newControl;
                });

        control.setControlValueNum(control.getControlValueNum() + 1);
        controlRepository.save(control);
        return control.getControlValueNum();
    }
}
