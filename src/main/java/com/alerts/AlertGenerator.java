package com.alerts;

import com.data_management.DataStorage;
import com.data_management.Patient;
import com.data_management.PatientRecord;
import java.util.ArrayList;
import java.util.List;
/**
 * The {@code AlertGenerator} class is responsible for monitoring patient data
 * and generating alerts when certain predefined conditions are met. This class
 * relies on a {@link DataStorage} instance to access patient data and evaluate
 * it against specific health criteria.
 * <p>Alert conditions may include:
 * <ul>
 *     <li>Abnormal vital signs (e.g., heart rate, blood pressure, saturation)</li>
 *     <li>Critical threshold violations</li>
 *     <li>Rapid changes in patient condition</li>
 * </ul>
 * <p>When an alert condition is detected, a {@link Alert} object is created and passed to {@link #triggerAlert(Alert)} for handling.
 */
public class AlertGenerator {
    private DataStorage dataStorage;
    private List<Alert> alertLog = new ArrayList<>();

    /**
     * Constructs an {@code AlertGenerator} with a specified {@code DataStorage}.
     * The {@code DataStorage} is used to retrieve patient data that this class
     * will monitor and evaluate.
     *
     * @param dataStorage the data storage system that provides access to patient data
     */
    public AlertGenerator(DataStorage dataStorage) {
        this.dataStorage = dataStorage;
    }

    /**
     * Evaluates all alert conditions for the given patient.
     * Checks blood pressure, saturation, combined hypoxemia, ECG, and triggered alerts.
     *
     * @param patient the patient data to evaluate for alert conditions
     */
    public void evaluateData(Patient patient) {
        long now = System.currentTimeMillis();
        long tenMinutesAgo = now - 10 * 60 * 1000;

        List<PatientRecord> allRecords = patient.getRecords(0, now);

        checkBloodPressure(patient, allRecords);
        checkBloodSaturation(patient, allRecords, tenMinutesAgo, now);
        checkHypotensiveHypoxemia(patient, allRecords);
        checkECG(patient, allRecords);
        checkTriggeredAlert(patient, allRecords);
    }

    /**
     * Triggers an alert for the monitoring system.
     * <p>This method can be extended to
     * <ul>
     *     <li>notify medical staff</li>
     *     <li>log the alert</li>
     *     <li>send alerts</li>
     *     <li>update a dashboard</li>
     * </ul>
     *
     * @param alert the alert object containing details about the alert condition (patient ID, timestamp, condition type, and measurement value)
     */
    private void triggerAlert(Alert alert) {
        alertLog.add(alert);
        System.out.println("ALERT: Patient " + alert.getPatientId()+ "|" + alert.getCondition()+ "|" + alert.getTimestamp());
    }
    /**
     * Checks for blood pressure trend and critical threshold alerts.
     * Trend: 3 consecutive readings changing by more than 10 mmHg each.
     * Critical: systolic > 180 or < 90, diastolic > 120 or < 60.
     */
    private void checkBloodPressure(Patient patient, List<PatientRecord> records) {
        List<PatientRecord> systolic = filterByType(records, "SystolicPressure");
        List<PatientRecord> diastolic = filterByType(records, "DiastolicPressure");

        // Critical thresholds
        for (PatientRecord r : systolic) {
            if (r.getMeasurementValue() > 180) {
                triggerAlert(new Alert(String.valueOf(patient.getPatientId()),
                        "Critical Systolic High: " + r.getMeasurementValue(), r.getTimestamp()));
            } else if (r.getMeasurementValue() < 90) {
                triggerAlert(new Alert(String.valueOf(patient.getPatientId()),
                        "Critical Systolic Low: " + r.getMeasurementValue(), r.getTimestamp()));
            }
        }
        for (PatientRecord r : diastolic) {
            if (r.getMeasurementValue() > 120) {
                triggerAlert(new Alert(String.valueOf(patient.getPatientId()),
                        "Critical Diastolic High: " + r.getMeasurementValue(), r.getTimestamp()));
            } else if (r.getMeasurementValue() < 60) {
                triggerAlert(new Alert(String.valueOf(patient.getPatientId()),
                        "Critical Diastolic Low: " + r.getMeasurementValue(), r.getTimestamp()));
            }
        }

        // Trend alerts
        checkPressureTrend(patient, systolic, "Systolic");
        checkPressureTrend(patient, diastolic, "Diastolic");
    }

    /**
     * Checks for 3 consecutive readings that each increase or decrease by more than 10 mmHg.
     */
    private void checkPressureTrend(Patient patient, List<PatientRecord> records, String type) {
        for (int i = 2; i < records.size(); i++) {
            double diff1 = records.get(i - 1).getMeasurementValue() - records.get(i - 2).getMeasurementValue();
            double diff2 = records.get(i).getMeasurementValue() - records.get(i - 1).getMeasurementValue();

            if (diff1 > 10 && diff2 > 10) {
                triggerAlert(new Alert(String.valueOf(patient.getPatientId()),
                        type + " Pressure Increasing Trend", records.get(i).getTimestamp()));
            } else if (diff1 < -10 && diff2 < -10) {
                triggerAlert(new Alert(String.valueOf(patient.getPatientId()),
                        type + " Pressure Decreasing Trend", records.get(i).getTimestamp()));
            }
        }
    }

    /**
     * Checks for low saturation (< 92%) and rapid drop (>= 5% within 10 minutes).
     */
    private void checkBloodSaturation(Patient patient, List<PatientRecord> records,
                                      long tenMinutesAgo, long now) {
        List<PatientRecord> satRecords = filterByType(records, "Saturation");

        for (PatientRecord r : satRecords) {
            // Low saturation alert
            if (r.getMeasurementValue() < 92) {
                triggerAlert(new Alert(String.valueOf(patient.getPatientId()),
                        "Low Blood Saturation: " + r.getMeasurementValue(), r.getTimestamp()));
            }
        }

        // Rapid drop within 10 minutes
        List<PatientRecord> recentSat = filterByTimeRange(satRecords, tenMinutesAgo, now);
        if (recentSat.size() >= 2) {
            double earliest = recentSat.get(0).getMeasurementValue();
            double latest = recentSat.get(recentSat.size() - 1).getMeasurementValue();
            if (earliest - latest >= 5) {
                triggerAlert(new Alert(String.valueOf(patient.getPatientId()),
                        "Rapid Saturation Drop", recentSat.get(recentSat.size() - 1).getTimestamp()));
            }
        }
    }

    /**
     * Triggers a Hypotensive Hypoxemia alert if systolic BP < 90 AND saturation < 92%
     * occur at the same time (within same timestamp window).
     */
    private void checkHypotensiveHypoxemia(Patient patient, List<PatientRecord> records) {
        List<PatientRecord> systolic = filterByType(records, "SystolicPressure");
        List<PatientRecord> saturation = filterByType(records, "Saturation");

        boolean lowBP = systolic.stream().anyMatch(r -> r.getMeasurementValue() < 90);
        boolean lowSat = saturation.stream().anyMatch(r -> r.getMeasurementValue() < 92);

        if (lowBP && lowSat) {
            triggerAlert(new Alert(String.valueOf(patient.getPatientId()),
                    "Hypotensive Hypoxemia Alert", System.currentTimeMillis()));
        }
    }

    /**
     * Checks for abnormal ECG peaks using a sliding window average.
     * Triggers alert if any value exceeds 2x the current window average.
     */
    private void checkECG(Patient patient, List<PatientRecord> records) {
        List<PatientRecord> ecgRecords = filterByType(records, "ECG");
        int windowSize = 10;

        for (int i = windowSize; i < ecgRecords.size(); i++) {
            double windowAvg = 0;
            for (int j = i - windowSize; j < i; j++) {
                windowAvg += Math.abs(ecgRecords.get(j).getMeasurementValue());
            }
            windowAvg /= windowSize;

            double current = Math.abs(ecgRecords.get(i).getMeasurementValue());
            if (windowAvg > 0 && current > 2 * windowAvg) {
                triggerAlert(new Alert(String.valueOf(patient.getPatientId()),
                        "Abnormal ECG Peak: " + current, ecgRecords.get(i).getTimestamp()));
            }
        }
    }

    /**
     * Checks for manually triggered alerts from nurse/patient buttons.
     */
    private void checkTriggeredAlert(Patient patient, List<PatientRecord> records) {
        List<PatientRecord> alertRecords = filterByType(records, "Alert");
        for (PatientRecord r : alertRecords) {
            if (r.getMeasurementValue() == 1.0) {
                triggerAlert(new Alert(String.valueOf(patient.getPatientId()),
                        "Manual Alert Triggered", r.getTimestamp()));
            }
        }
    }

    /**
     * Filters records by record type.
     */
    private List<PatientRecord> filterByType(List<PatientRecord> records, String type) {
        List<PatientRecord> result = new ArrayList<>();
        for (PatientRecord r : records) {
            if (r.getRecordType().equals(type)) {
                result.add(r);
            }
        }
        return result;
    }

    /**
     * Filters records by time range.
     */
    private List<PatientRecord> filterByTimeRange(List<PatientRecord> records, long start, long end) {
        List<PatientRecord> result = new ArrayList<>();
        for (PatientRecord r : records) {
            if (r.getTimestamp() >= start && r.getTimestamp() <= end) {
                result.add(r);
            }
        }
        return result;
    }
    public List<Alert> getAlertLog(){
        return alertLog;
    }
}
