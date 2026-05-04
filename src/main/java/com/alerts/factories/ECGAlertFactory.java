package com.alerts.factories;

import com.alerts.Alert;

/**
 * Factory for creating ECG related alerts.
 */
public class ECGAlertFactory extends AlertFactory {

    /**
     * Creates an ECG alert.
     *
     * @param patientId the ID of the patient
     * @param condition the ECG condition detected
     * @param timestamp the time the alert was generated
     * @return a new Alert for ECG
     */
    @Override
    public Alert createAlert(String patientId, String condition, long timestamp) {
        return new Alert(patientId, "ECG: " + condition, timestamp);
    }
}