package com.cardio_generator.outputs;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.concurrent.ConcurrentHashMap;

/**
* Outputs patient data to files on the local file system.
* <p>This implementation of {@link OutputStrategy} writes data to separate files based on the data label (e.g., ECG, BloodPressure). Each label gets its own file, allowing for organized data persistence and easy post-processing.
* <p>Files are created in a specified base directory, and data is appended with timestamps for chronological tracking.
 */
public class FileOutputStrategy implements OutputStrategy {

    private String baseDirectory;

    public final ConcurrentHashMap<String, String> fileMap = new ConcurrentHashMap<>();
/**
* Constructs a new file output strategy that writes to the specified directory.
* <p>The base directory will be created if it does not already exist.
* Each data label will result in a separate file named "{label}.txt" inside this directory.
* @param baseDirectory the directory path where output files will be stored
*/
    public FileOutputStrategy(String baseDirectory) {

        this.baseDirectory = baseDirectory;
    }
/**
* Writes a single data record to the appropriate file based on the data label.
* <p>Each unique label (e.g., "ECG", "BloodPressure") gets its own file.
* The base directory is created if it doesn't exist. Data is appended to the file in a human-readable format.
* @param patientId  the unique identifier of the patient
* @param timestamp  the time when the data was generated (milliseconds since Unix epoch)
* @param label      the type of measurement (determines which file to write to)
* @param data       the actual measurement value or alert status
*/
    @Override
    public void output(int patientId, long timestamp, String label, String data) {
        try {
            // Create the directory
            Files.createDirectories(Paths.get(baseDirectory));
        } catch (IOException e) {
            System.err.println("Error creating base directory: " + e.getMessage());
            return;
        }
        // Set the FilePath variable
        String FilePath = file_map.computeIfAbsent(label, k -> Paths.get(baseDirectory, label + ".txt").toString());

        // Write the data to the file
        try (PrintWriter out = new PrintWriter(
                Files.newBufferedWriter(Paths.get(FilePath), StandardOpenOption.CREATE, StandardOpenOption.APPEND))) {
            out.printf("Patient ID: %d, Timestamp: %d, Label: %s, Data: %s%n", patientId, timestamp, label, data);
        } catch (Exception e) {
            System.err.println("Error writing to file " + FilePath + ": " + e.getMessage());
        }
    }
}
