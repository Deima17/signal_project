package com.cardio_generator.generators;

import com.cardio_generator.outputs.OutputStrategy;
/**
* Defines the contract for patient data generators in the cardio data simulator.
* <p>Implementing classes are responsible for generating specific types of health data (e.g., ECG, blood pressure, blood saturation) for individual patients. Each generator produces realistic simulated data that mimics real-world medical measurements.
* <p>This interface is part of the strategy pattern, allowing different data generation algorithms to be used interchangeably.
*/
public interface PatientDataGenerator {
/**
* Generates health data for a specific patient and outputs it using the provided output strategy.
* <p>The generated data is typically based on:
* <ul>
*   <li>Patient ID (for consistent per-patient patterns)</li>
*   <li>Randomized variations to simulate real-world fluctuations</li>
*   <li>Medical realistic ranges for each data type</li>
* </ul>
* @param patientId      the unique identifier of the patient (positive integer)
* @param outputStrategy the strategy used to output the generated data (console, file, WebSocket, or TCP)
*/
    void generate(int patientId, OutputStrategy outputStrategy);
}
