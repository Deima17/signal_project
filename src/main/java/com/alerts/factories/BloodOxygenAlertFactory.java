package com.alerts.factories;

import com.alerts.Alert;

/**
 * Factory for creating blood oxygen saturation related alerts.
 */
public class BloodOxygenAlertFactory extends AlertFactory {

    /**
     * Creates a blood oxygen alert.
     *
     * @param patientId the ID of the patient
     * @param condition the oxygen condition detected
     * @param timestamp the time the alert was generated
     * @return a new Alert for blood oxygen
     */
    @Override
    public Alert createAlert(String patientId, String condition, long timestamp) {
        return new Alert(patientId, "BloodOxygen: " + condition, timestamp);
    }
}