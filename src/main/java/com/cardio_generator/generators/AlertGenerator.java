package com.cardio_generator.generators;

import java.util.Random;

import com.cardio_generator.outputs.OutputStrategy;
/**
*Generates random alert events for simulated patients.
*<p> Alerts are triggered or resolved based or probabilistic timing to simulate real-world medical alert scenarios. 
*/
public class AlertGenerator implements PatientDataGenerator {

    public static final Random randomGenerator = new Random();
    private boolean[] alertStates; // false = resolved, true = pressed

    /**
    * constructs an {@code AlertGenerator} for the given number of patients
    * @param patientCount the number of patients to monitor and determines the size of the internal alert state array
    */
    public AlertGenerator(int patientCount) {
        alertStates = new boolean[patientCount + 1];
    }

    /**
     * Generates an alert event for the specified patient.
     * <p>If an alert is currently active, there is a 90% chance it will be resolved, otherwise, a new alert may be triggered based on a Poisson probability model.
     *
     * @param patientId 
     * @param outputStrategy used to send the alert data
     * @throws Exception if an error occurs during alert data generation
     */
        @Override
    public void generate(int patientId, OutputStrategy outputStrategy) {
        try {
            if (alertStates[patientId]) {
                if (randomGenerator.nextDouble() < 0.9) { // 90% chance to resolve
                    alertStates[patientId] = false;
                    // Output the alert
                    outputStrategy.output(patientId, System.currentTimeMillis(), "Alert", "resolved");
                }
            } else {
                double lambda = 0.1; // Average rate (alerts per period), adjust based on desired frequency
                double p = -Math.expm1(-lambda); // Probability of at least one alert in the period
                boolean alertTriggered = randomGenerator.nextDouble() < p;

                if (alertTriggered) {
                    alertStates[patientId] = true;
                    // Output the alert
                    outputStrategy.output(patientId, System.currentTimeMillis(), "Alert", "triggered");
                }
            }
        } catch (Exception e) {
            System.err.println("An error occurred while generating alert data for patient " + patientId);
            e.printStackTrace();
        }
    }
}
