package com.bankapp.service;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Service for performing credit checks against multiple credit agencies.
 *
 * Replaces COBOL programs:
 *   - CRDTAGY1.cbl through CRDTAGY5.cbl (5 credit agency checks)
 *   - Async child task invocation in CRECUST.cbl
 *
 * In the COBOL system, CRECUST used CICS RUN TRANSID to asynchronously
 * invoke 5 credit agency programs (CRDTAGY1-5), waited 3 seconds with
 * CICS DELAY, then aggregated/averaged the returned credit scores.
 *
 * In this Java implementation, we simulate the credit agency calls
 * and return an averaged score.
 */
@Service
public class CreditAgencyService {

    private static final int NUM_AGENCIES = 5;
    private static final int MIN_CREDIT_SCORE = 1;
    private static final int MAX_CREDIT_SCORE = 999;

    private final Random random = new Random();

    /**
     * Perform credit checks against all agencies and return averaged score.
     * Equivalent to COBOL CRECUST.cbl credit-check section which invoked
     * CRDTAGY1-5 asynchronously and averaged results.
     *
     * @param customerName the customer name
     * @param customerAddress the customer address
     * @param dateOfBirth the date of birth as string
     * @return averaged credit score from all agencies (0 if all fail)
     */
    public int performCreditCheck(String customerName, String customerAddress, String dateOfBirth) {
        List<Integer> scores = new ArrayList<>();

        for (int i = 1; i <= NUM_AGENCIES; i++) {
            int score = callCreditAgency(i, customerName, customerAddress, dateOfBirth);
            if (score > 0) {
                scores.add(score);
            }
        }

        if (scores.isEmpty()) {
            return 0;
        }

        int total = scores.stream().mapToInt(Integer::intValue).sum();
        return total / scores.size();
    }

    /**
     * Simulate calling a single credit agency.
     * In the COBOL system, each CRDTAGY program (1-5) would return a
     * credit score between 1-999 in the COMMAREA.
     */
    private int callCreditAgency(int agencyNumber, String customerName,
                                  String customerAddress, String dateOfBirth) {
        return random.nextInt(MAX_CREDIT_SCORE - MIN_CREDIT_SCORE + 1) + MIN_CREDIT_SCORE;
    }
}
