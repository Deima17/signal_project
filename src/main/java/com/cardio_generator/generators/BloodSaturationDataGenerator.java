package com.cardio_generator.generators;

import java.util.Random;

import com.cardio_generator.outputs.OutputStrategy;

/**
* Generates simulated blood saturation (SpO2) data for patients.
* <p>Blood saturation refers to the percentage of oxygen-saturated hemoglobin in the blood. Normal ranges are typically 95-100%. This generator creates realistic fluctuations within a healthy range (90-100%) and tracks each patient's current saturation value over time.
* <p>Values below 90% might indicate hypoxemia and could trigger clinical alerts.
*/
public class BloodSaturationDataGenerator implements PatientDataGenerator {
    private static final Random random = new Random();
    private int[] lastSaturationValues;

/**
* Constructs a new blood saturation data generator for the specified number of patients.
* <p>Initializes each patient with a baseline saturation value between 95% and 100% (inclusive) to simulate healthy starting conditions.
* @param patientCount the total number of patients to generate data for
*/
    public BloodSaturationDataGenerator(int patientCount) {
        lastSaturationValues = new int[patientCount + 1];

        // Initialize with baseline saturation values for each patient
        for (int i = 1; i <= patientCount; i++) {
            lastSaturationValues[i] = 95 + random.nextInt(6); // Initializes with a value between 95 and 100
        }
    }

/**
* Generates a blood saturation reading for a specific patient.
* <p>The generation algorithm:
* <ul>
*   <li>Applies a small random variation (-1, 0, or +1) to the last known value</li>
*   <li>Clamps the result to a realistic clinical range (90-100%)</li>
*   <li>Updates the stored saturation value for the patient</li>
*   <li>Outputs the value with a timestamp via the provided output strategy</li>
* </ul>
* @param patientId      the unique identifier of the patient (positive integer)
* @param outputStrategy the strategy used to output the generated data (console, file, WebSocket, or TCP)
*/
    @Override
    public void generate(int patientId, OutputStrategy outputStrategy) {
        try {
            // Simulate blood saturation values
            int variation = random.nextInt(3) - 1; // -1, 0, or 1 to simulate small fluctuations
            int newSaturationValue = lastSaturationValues[patientId] + variation;

            // Ensure the saturation stays within a realistic and healthy range
            newSaturationValue = Math.min(Math.max(newSaturationValue, 90), 100);
            lastSaturationValues[patientId] = newSaturationValue;
            outputStrategy.output(patientId, System.currentTimeMillis(), "Saturation",
                    Double.toString(newSaturationValue) + "%");
        } catch (Exception e) {
            System.err.println("An error occurred while generating blood saturation data for patient " + patientId);
            e.printStackTrace(); // This will print the stack trace to help identify where the error occurred.
        }
    }
}
