package com.alerts.factories;

import com.alerts.Alert;

/**
 * Factory for creating blood pressure related alerts.
 */
public class BloodPressureAlertFactory extends AlertFactory {

    /**
     * Creates a blood pressure alert.
     *
     * @param patientId the ID of the patient
     * @param condition the blood pressure condition detected
     * @param timestamp the time the alert was generated
     * @return a new Alert for blood pressure
     */
    @Override
    public Alert createAlert(String patientId, String condition, long timestamp) {
        return new Alert(patientId, "BloodPressure: " + condition, timestamp);
    }
}