package com.alerts.factories;

import com.alerts.Alert;

/**
 * Abstract factory for creating alerts.
 * Subclasses define which type of alert to create.
 */
public abstract class AlertFactory {

    /**
     * Creates an alert for the given patient, condition, and timestamp.
     *
     * @param patientId the ID of the patient
     * @param condition the condition that triggered the alert
     * @param timestamp the time the alert was generated
     * @return a new Alert object
     */
    public abstract Alert createAlert(String patientId, String condition, long timestamp);
}