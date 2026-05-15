package data_management;

import com.data_management.DataStorage;
import com.data_management.PatientRecord;
import com.data_management.WebSocketClientImpl;
import org.java_websocket.handshake.ServerHandshake;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link WebSocketClientImpl}.
 * Tests message parsing, error handling, and data storage integration.
 */
class WebSocketClientImplTest {

    private DataStorage storage;
    private WebSocketClientImpl client;

    /**
     * Initialises a fresh DataStorage and WebSocketClientImpl before each test.
     */
    @BeforeEach
    void setUp() throws Exception {
        storage = new DataStorage();
        client = new WebSocketClientImpl(new URI("ws://localhost:8080"), storage);
    }

    /**
     * Verifies a valid CSV message is correctly parsed and stored in DataStorage.
     */
    @Test
    void testValidMessageParsedAndStored() {
        client.onMessage("1,1000,ECG,0.5");
        List<PatientRecord> records = storage.getRecords(1, 0, 9999L);
        assertEquals(1, records.size());
        assertEquals("ECG", records.get(0).getRecordType());
        assertEquals(0.5, records.get(0).getMeasurementValue());
        assertEquals(1000L, records.get(0).getTimestamp());
    }

    /**
     * Verifies multiple valid messages are all stored correctly.
     */
    @Test
    void testMultipleMessagesStored() {
        client.onMessage("1,1000,ECG,0.5");
        client.onMessage("1,2000,ECG,0.6");
        client.onMessage("1,3000,ECG,0.7");
        List<PatientRecord> records = storage.getRecords(1, 0, 9999L);
        assertEquals(3, records.size());
    }

    /**
     * Verifies a malformed message with wrong number of fields is skipped.
     */
    @Test
    void testMalformedMessageSkipped() {
        client.onMessage("this is not valid");
        assertEquals(0, storage.getAllPatients().size());
    }

    /**
     * Verifies a message with non-numeric patientId is skipped gracefully.
     */
    @Test
    void testInvalidPatientIdSkipped() {
        client.onMessage("abc,1000,ECG,0.5");
        assertEquals(0, storage.getAllPatients().size());
    }

    /**
     * Verifies a message with non-numeric data value is skipped gracefully.
     */
    @Test
    void testInvalidDataValueSkipped() {
        client.onMessage("1,1000,ECG,notanumber");
        assertEquals(0, storage.getAllPatients().size());
    }

    /**
     * Verifies a message with non-numeric timestamp is skipped gracefully.
     */
    @Test
    void testInvalidTimestampSkipped() {
        client.onMessage("1,badtime,ECG,0.5");
        assertEquals(0, storage.getAllPatients().size());
    }

    /**
     * Verifies messages for multiple patients are stored separately.
     */
    @Test
    void testMultiplePatientsStored() {
        client.onMessage("1,1000,ECG,0.5");
        client.onMessage("2,1000,ECG,0.6");
        assertEquals(2, storage.getAllPatients().size());
    }

    /**
     * Verifies onOpen does not throw or cause errors.
     */
    @Test
    void testOnOpenDoesNotThrow() {
        assertDoesNotThrow(() -> client.onOpen(null));
    }

    /**
     * Verifies onClose does not throw or cause errors.
     */
    @Test
    void testOnCloseDoesNotThrow() {
        assertDoesNotThrow(() -> client.onClose(1000, "Normal closure", true));
    }

    /**
     * Verifies onError does not throw or cause errors.
     */
    @Test
    void testOnErrorDoesNotThrow() {
        assertDoesNotThrow(() -> client.onError(new Exception("Test error")));
    }

    /**
     * Verifies readData(DataStorage) throws UnsupportedOperationException.
     */
    @Test
    void testReadDataWithoutUriThrows() {
        assertThrows(UnsupportedOperationException.class, () -> client.readData(storage));
    }

    /**
     * Verifies an empty message is skipped without errors.
     */
    @Test
    void testEmptyMessageSkipped() {
        client.onMessage("");
        assertEquals(0, storage.getAllPatients().size());
    }
}