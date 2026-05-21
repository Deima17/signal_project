package com.alerts.strategies;

import com.alerts.Alert;
import com.data_management.Patient;
import java.util.List;

/**
 * Defines the contract for alert evaluation strategies.
 * Each strategy checks a specific type of patient data
 * and returns any alerts that should be triggered.
 */
public interface AlertStrategy {

    /**
     * Checks the patient's records for a specific alert condition.
     *
     * @param patient the patient to evaluate
     * @return a list of alerts triggered, or an empty list if none
     */
    List<Alert> checkAlert(Patient patient);
}
