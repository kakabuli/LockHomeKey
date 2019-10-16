package com.kaadas.lock.publiclibrary.ota.ble.ti.BluetoothLEController.Exceptions;

/**
 * Created by ole on 21/03/2017.
 */

public class BluetoothLEDiscoveryTimedOutException extends BluetoothLEException {
    public BluetoothLEDiscoveryTimedOutException() { super(); }
    public BluetoothLEDiscoveryTimedOutException(String message) { super(message); }
    public BluetoothLEDiscoveryTimedOutException(String message, Throwable cause) { super(message,cause); }
    public BluetoothLEDiscoveryTimedOutException(Throwable cause) { super(cause); }
}
