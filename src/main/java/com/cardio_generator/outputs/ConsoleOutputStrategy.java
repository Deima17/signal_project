package com.cardio_generator.outputs;
/**
* Outputs patient data to the system console (standard output).
* <p>This implementation of {@link OutputStrategy} prints formatted dat directly to the console, which is useful for debugging, development, or when real-time monitoring is needed without persistence.
* <p>Output format: "Patient ID: X, Timestamp: Y, Label: Z, Data: W"
*/
public class ConsoleOutputStrategy implements OutputStrategy {
/**
* Prints a single data record to the console.
* <p>The output is formatted as a human-readable string containing the patient ID, timestamp, measurement label, and data value.
* @param patientId  the unique identifier of the patient
* @param timestamp  the time when the data was generated (milliseconds since Unix epoch)
* @param label      the type of measurement (e.g., "ECG", "BloodPressure")
* @param data       the actual measurement value or alert status
*/
	@Override
    public void output(int patientId, long timestamp, String label, String data) {
        System.out.printf("Patient ID: %d, Timestamp: %d, Label: %s, Data: %s%n", patientId, timestamp, label, data);
    }
}
