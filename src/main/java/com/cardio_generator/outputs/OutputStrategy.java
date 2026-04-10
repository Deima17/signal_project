package com.cardio_generator.outputs;

/**
 * Defines the contract for output strategies in the cardio data simulator.
 * Implementing classes handle the delivery of generated patient data to
 * different destinations such as console, file, or network sockets.
 * 
 * <p>This interface is part of the Strategy pattern, allowing the data output
 * method to be selected at runtime based on user configuration.
 */
public interface OutputStrategy {
    
    /**
     * Outputs a single data record for a patient.
     * 
     * <p>The output destination and format depend on the specific implementation
     * (e.g., ConsoleOutputStrategy prints to System.out, FileOutputStrategy writes
     * to a file, TcpOutputStrategy sends over a network socket).
     *
     * @param patientId  the unique identifier of the patient (positive integer)
     * @param timestamp  the time when the data was generated (milliseconds since Unix epoch)
     * @param label      the type of measurement (e.g., "ECG", "BloodPressure", "Alert")
     * @param data       the actual measurement value or alert status as a string
     */
void output(int patientId, long timestamp, String label, String data);
}
