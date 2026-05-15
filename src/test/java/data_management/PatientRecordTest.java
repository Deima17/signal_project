package data_management;

import com.data_management.PatientRecord;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link PatientRecord}.
 * Verifies correct storage and retrieval of all fields.
 */
class PatientRecordTest {

    /**
     * Verifies getPatientId returns the correct patient ID.
     */
    @Test
    void testGetPatientId() {
        PatientRecord record = new PatientRecord(1, 120.0, "SystolicPressure", 1000L);
        assertEquals(1, record.getPatientId());
    }

    /**
     * Verifies getMeasurementValue returns the correct value.
     */
    @Test
    void testGetMeasurementValue() {
        PatientRecord record = new PatientRecord(1, 98.6, "Temperature", 1000L);
        assertEquals(98.6, record.getMeasurementValue());
    }

    /**
     * Verifies getTimestamp returns the correct timestamp.
     */
    @Test
    void testGetTimestamp() {
        PatientRecord record = new PatientRecord(1, 120.0, "SystolicPressure", 5000L);
        assertEquals(5000L, record.getTimestamp());
    }

    /**
     * Verifies getRecordType returns the correct type string.
     */
    @Test
    void testGetRecordType() {
        PatientRecord record = new PatientRecord(1, 120.0, "SystolicPressure", 1000L);
        assertEquals("SystolicPressure", record.getRecordType());
    }
}