package com.data_management;

import java.io.IOException;

public interface DataReader {
    /**
     * Reads data from a specified source and stores it in the data storage.
     * 
     * @param dataStorage the storage where data will be stored
     * @throws IOException if there is an error reading the data
     */
    void readData(DataStorage dataStorage) throws IOException;

    /**
     * Connects to a real-time data source and continuously reads incoming data.
     * Default implementation throws UnsupportedOperationException so that
     * existing implementations like FileDataReader are not broken.
     *
     * @param dataStorage the storage where data will be stored
     * @param uri the URI of the real-time data source
     * @throws IOException if there is an error connecting or reading
     */
    default void readData(DataStorage dataStorage, String uri) throws IOException {
        throw new UnsupportedOperationException("Real-time reading not supported by this reader");
    }

}
