package com.bankapp.model;

/**
 * Represents a bank menu option.
 *
 * Migrated from BNKMENU.cbl which displayed menu options via BMS maps
 * and routed to different CICS transactions based on user selection.
 *
 * Menu options from BNKMENU.cbl EDIT-MENU-DATA / INVOKE-OTHER-TXNS:
 *   1 = Display Customer details (ODCS)
 *   2 = Display Account details (ODAC)
 *   3 = Create Customer (OCCS)
 *   4 = Create Account (OCAC)
 *   5 = Update Customer/Account
 *   6 = Delete Customer/Account
 *   7 = Transfer/Payment operations
 *   A = Admin functions
 */
public class MenuItem {

    private final String code;
    private final String label;
    private final String description;
    private final String apiEndpoint;

    public MenuItem(String code, String label, String description, String apiEndpoint) {
        this.code = code;
        this.label = label;
        this.description = description;
        this.apiEndpoint = apiEndpoint;
    }

    public String getCode() {
        return code;
    }

    public String getLabel() {
        return label;
    }

    public String getDescription() {
        return description;
    }

    public String getApiEndpoint() {
        return apiEndpoint;
    }
}
