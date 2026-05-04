package com.alerts;

/**
 * Defines the contract for all alert types in the system.
 * Both concrete alerts and decorators implement this interface.
 */
public interface AlertInterface {

    /**
     * @return the patient ID as a String
     */
    String getPatientId();

    /**
     * @return the condition description
     */
    String getCondition();

    /**
     * @return the timestamp in milliseconds since Unix epoch
     */
    long getTimestamp();
}