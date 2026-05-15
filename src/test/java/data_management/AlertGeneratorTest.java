package data_management;

import com.alerts.Alert;
import com.alerts.AlertGenerator;
import com.data_management.DataStorage;
import com.data_management.Patient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link AlertGenerator}.
 * Covers blood pressure thresholds, trends, saturation alerts,
 * hypotensive hypoxemia, ECG peaks, manual alerts, and getRecords filtering.
 */
class AlertGeneratorTest {

    private DataStorage storage;
    private AlertGenerator alertGenerator;

    /**
     * Initialises a fresh DataStorage and AlertGenerator before each test.
     */
    @BeforeEach
    void setUp() {
        storage = new DataStorage();
        alertGenerator = new AlertGenerator(storage);
    }

    /**
     * Verifies an alert is triggered when systolic pressure exceeds 180 mmHg.
     */
    @Test
    void testCriticalSystolicHigh() {
        storage.addPatientData(1, 185, "SystolicPressure", 1000L);
        Patient patient = storage.getAllPatients().get(0);
        alertGenerator.evaluateData(patient);
        assertTrue(containsAlert(alertGenerator.getAlertLog(), "Critical Systolic High"));
    }

    /**
     * Verifies an alert is triggered when systolic pressure drops below 90 mmHg.
     */
    @Test
    void testCriticalSystolicLow() {
        storage.addPatientData(1, 85, "SystolicPressure", 1000L);
        Patient patient = storage.getAllPatients().get(0);
        alertGenerator.evaluateData(patient);
        assertTrue(containsAlert(alertGenerator.getAlertLog(), "Critical Systolic Low"));
    }

    /**
     * Verifies an alert is triggered when diastolic pressure exceeds 120 mmHg.
     */
    @Test
    void testCriticalDiastolicHigh() {
        storage.addPatientData(1, 125, "DiastolicPressure", 1000L);
        Patient patient = storage.getAllPatients().get(0);
        alertGenerator.evaluateData(patient);
        assertTrue(containsAlert(alertGenerator.getAlertLog(), "Critical Diastolic High"));
    }

    /**
     * Verifies an alert is triggered when diastolic pressure drops below 60 mmHg.
     */
    @Test
    void testCriticalDiastolicLow() {
        storage.addPatientData(1, 55, "DiastolicPressure", 1000L);
        Patient patient = storage.getAllPatients().get(0);
        alertGenerator.evaluateData(patient);
        assertTrue(containsAlert(alertGenerator.getAlertLog(), "Critical Diastolic Low"));
    }

    /**
     * Verifies an alert is triggered when three consecutive systolic readings
     * each increase by more than 10 mmHg.
     */
    @Test
    void testSystolicIncreasingTrend() {
        storage.addPatientData(1, 100, "SystolicPressure", 1000L);
        storage.addPatientData(1, 115, "SystolicPressure", 2000L);
        storage.addPatientData(1, 130, "SystolicPressure", 3000L);
        Patient patient = storage.getAllPatients().get(0);
        alertGenerator.evaluateData(patient);
        assertTrue(containsAlert(alertGenerator.getAlertLog(), "Systolic Pressure Increasing Trend"));
    }

    /**
     * Verifies an alert is triggered when three consecutive systolic readings
     * each decrease by more than 10 mmHg.
     */
    @Test
    void testSystolicDecreasingTrend() {
        storage.addPatientData(1, 130, "SystolicPressure", 1000L);
        storage.addPatientData(1, 115, "SystolicPressure", 2000L);
        storage.addPatientData(1, 100, "SystolicPressure", 3000L);
        Patient patient = storage.getAllPatients().get(0);
        alertGenerator.evaluateData(patient);
        assertTrue(containsAlert(alertGenerator.getAlertLog(), "Systolic Pressure Decreasing Trend"));
    }

    /**
     * Verifies no trend alert is triggered when consecutive changes are 10 mmHg or less.
     */
    @Test
    void testNoTrendWhenChangeLessThan10() {
        storage.addPatientData(1, 100, "SystolicPressure", 1000L);
        storage.addPatientData(1, 105, "SystolicPressure", 2000L);
        storage.addPatientData(1, 110, "SystolicPressure", 3000L);
        Patient patient = storage.getAllPatients().get(0);
        alertGenerator.evaluateData(patient);
        assertFalse(containsAlert(alertGenerator.getAlertLog(), "Systolic Pressure Increasing Trend"));
    }

    /**
     * Verifies an alert is triggered when blood oxygen saturation falls below 92%.
     */
    @Test
    void testLowSaturationAlert() {
        storage.addPatientData(1, 91, "Saturation", 1000L);
        Patient patient = storage.getAllPatients().get(0);
        alertGenerator.evaluateData(patient);
        assertTrue(containsAlert(alertGenerator.getAlertLog(), "Low Blood Saturation"));
    }

    /**
     * Verifies no alert is triggered when saturation is at or above 92%.
     */
    @Test
    void testNoAlertWhenSaturationNormal() {
        storage.addPatientData(1, 95, "Saturation", 1000L);
        Patient patient = storage.getAllPatients().get(0);
        alertGenerator.evaluateData(patient);
        assertFalse(containsAlert(alertGenerator.getAlertLog(), "Low Blood Saturation"));
    }

    /**
     * Verifies an alert is triggered when saturation drops by 5% or more within 10 minutes.
     */
    @Test
    void testRapidSaturationDrop() {
        long now = System.currentTimeMillis();
        storage.addPatientData(1, 97, "Saturation", now - 500000L);
        storage.addPatientData(1, 91, "Saturation", now - 100000L);
        Patient patient = storage.getAllPatients().get(0);
        alertGenerator.evaluateData(patient);
        assertTrue(containsAlert(alertGenerator.getAlertLog(), "Rapid Saturation Drop"));
    }

    /**
     * Verifies a Hypotensive Hypoxemia alert is triggered when both
     * systolic pressure is below 90 mmHg and saturation is below 92%.
     */
    @Test
    void testHypotensiveHypoxemiaAlert() {
        storage.addPatientData(1, 85, "SystolicPressure", 1000L);
        storage.addPatientData(1, 91, "Saturation", 1000L);
        Patient patient = storage.getAllPatients().get(0);
        alertGenerator.evaluateData(patient);
        assertTrue(containsAlert(alertGenerator.getAlertLog(), "Hypotensive Hypoxemia Alert"));
    }

    /**
     * Verifies no Hypotensive Hypoxemia alert when only blood pressure is low.
     */
    @Test
    void testNoHypoxemiaWhenOnlyLowBP() {
        storage.addPatientData(1, 85, "SystolicPressure", 1000L);
        storage.addPatientData(1, 96, "Saturation", 1000L);
        Patient patient = storage.getAllPatients().get(0);
        alertGenerator.evaluateData(patient);
        assertFalse(containsAlert(alertGenerator.getAlertLog(), "Hypotensive Hypoxemia Alert"));
    }

    /**
     * Verifies an ECG alert is triggered when a peak value exceeds
     * twice the sliding window average.
     */
    @Test
    void testECGAbnormalPeak() {
        for (int i = 0; i < 10; i++) {
            storage.addPatientData(1, 0.5, "ECG", 1000L + i * 100);
        }
        storage.addPatientData(1, 5.0, "ECG", 2000L);
        Patient patient = storage.getAllPatients().get(0);
        alertGenerator.evaluateData(patient);
        assertTrue(containsAlert(alertGenerator.getAlertLog(), "Abnormal ECG Peak"));
    }

    /**
     * Verifies no ECG alert is triggered when all readings are within normal range.
     */
    @Test
    void testNoECGAlertWhenNormal() {
        for (int i = 0; i < 11; i++) {
            storage.addPatientData(1, 0.5, "ECG", 1000L + i * 100);
        }
        Patient patient = storage.getAllPatients().get(0);
        alertGenerator.evaluateData(patient);
        assertFalse(containsAlert(alertGenerator.getAlertLog(), "Abnormal ECG Peak"));
    }

    /**
     * Verifies a manual alert is triggered when the alert value is 1.0.
     */
    @Test
    void testManualAlertTriggered() {
        storage.addPatientData(1, 1.0, "Alert", 1000L);
        Patient patient = storage.getAllPatients().get(0);
        alertGenerator.evaluateData(patient);
        assertTrue(containsAlert(alertGenerator.getAlertLog(), "Manual Alert Triggered"));
    }

    /**
     * Verifies no manual alert is triggered when the alert value is 0.0 (resolved).
     */
    @Test
    void testNoManualAlertWhenResolved() {
        storage.addPatientData(1, 0.0, "Alert", 1000L);
        Patient patient = storage.getAllPatients().get(0);
        alertGenerator.evaluateData(patient);
        assertFalse(containsAlert(alertGenerator.getAlertLog(), "Manual Alert Triggered"));
    }

    /**
     * Verifies that getRecords returns only records within the specified time range.
     */
    @Test
    void testGetRecordsWithinRange() {
        storage.addPatientData(1, 100, "SystolicPressure", 1000L);
        storage.addPatientData(1, 110, "SystolicPressure", 2000L);
        storage.addPatientData(1, 120, "SystolicPressure", 3000L);
        Patient patient = storage.getAllPatients().get(0);
        assertEquals(2, patient.getRecords(1000L, 2000L).size());
    }

    /**
     * Verifies that getRecords returns an empty list when no records fall in range.
     */
    @Test
    void testGetRecordsOutsideRange() {
        storage.addPatientData(1, 100, "SystolicPressure", 1000L);
        Patient patient = storage.getAllPatients().get(0);
        assertEquals(0, patient.getRecords(5000L, 9000L).size());
    }

    /**
     * Verifies that getRecords returns an empty list for a patient with no records.
     */
    @Test
    void testGetRecordsEmptyPatient() {
        Patient patient = new Patient(99);
        assertEquals(0, patient.getRecords(0L, 9999L).size());
    }
    /**
     * Verifies addPatientData creates a new patient if one doesn't exist.
     */
    @Test
    void testAddPatientDataCreatesNewPatient() {
        storage.addPatientData(1, 120, "SystolicPressure", 1000L);
        assertEquals(1, storage.getAllPatients().size());
    }

    /**
     * Verifies addPatientData adds records to an existing patient.
     */
    @Test
    void testAddPatientDataAppendsToExistingPatient() {
        storage.addPatientData(1, 120, "SystolicPressure", 1000L);
        storage.addPatientData(1, 130, "SystolicPressure", 2000L);
        assertEquals(1, storage.getAllPatients().size());
        assertEquals(2, storage.getRecords(1, 0, 9999L).size());
    }

    /**
     * Verifies getRecords returns correct records for a patient within time range.
     */
    @Test
    void testDataStorageGetRecordsInRange() {
        storage.addPatientData(1, 120, "SystolicPressure", 1000L);
        storage.addPatientData(1, 130, "SystolicPressure", 5000L);
        assertEquals(1, storage.getRecords(1, 0, 2000L).size());
    }

    /**
     * Verifies getRecords returns empty list for unknown patient.
     */
    @Test
    void testDataStorageGetRecordsUnknownPatient() {
        assertEquals(0, storage.getRecords(99, 0, 9999L).size());
    }

    /**
     * Verifies getAllPatients returns all added patients.
     */
    @Test
    void testGetAllPatients() {
        storage.addPatientData(1, 120, "SystolicPressure", 1000L);
        storage.addPatientData(2, 80, "DiastolicPressure", 1000L);
        assertEquals(2, storage.getAllPatients().size());
    }

    /**
     * Checks whether any alert in the list contains the given keyword in its condition.
     *
     * @param alerts the list of alerts to search
     * @param keyword the keyword to look for
     * @return true if a matching alert is found, false otherwise
     */
    private boolean containsAlert(List<Alert> alerts, String keyword) {
        for (Alert a : alerts) {
            if (a.getCondition().contains(keyword)) return true;
        }
        return false;
    }
}