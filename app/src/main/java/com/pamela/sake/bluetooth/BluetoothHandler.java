package com.pamela.sake.bluetooth;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Build;
import android.util.Log;

import com.pamela.sake.MainActivity;
import com.pamela.sake.commons.Utilities;
import com.pamela.sake.exceptions.BluetoothException;
import com.pamela.sake.exceptions.BluetoothExceptionAdapterDisabled;
import com.pamela.sake.exceptions.BluetoothExceptionExitApp;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.UUID;


public class BluetoothHandler {
    private static BluetoothAdapter bluetoothAdapter;
    private MainActivity mainActivity;

    public BluetoothHandler(BluetoothAdapter bluetoothAdapter, Activity activity) {
        this.bluetoothAdapter = bluetoothAdapter;
        this.mainActivity = (MainActivity) activity;
    }

    public Boolean connectToBluetoothDevice() {
        try {
            validateIfBluetoothAdapterIsSupported();
            checkBluetoothAdapterState();
            return true;
        } catch (BluetoothException bex) {
            bex.manageException(mainActivity);
            return false;
        }
    }

    public void validateIfBluetoothAdapterIsSupported() throws BluetoothExceptionExitApp {
        if(bluetoothAdapter == null)
            throw new BluetoothExceptionExitApp(Utilities.FATAL_ERROR, Utilities.BLUETOOTH_NOT_SUPPORTED);
    }

    public void checkBluetoothAdapterState() throws BluetoothExceptionAdapterDisabled {
        if(!validateBluetoothAdapterState())
            throw new BluetoothExceptionAdapterDisabled();
        showMessageInLog(Utilities.BLUETOOTH_ON);
    }

    public Boolean validateBluetoothAdapterState() {
        return bluetoothAdapter.isEnabled();
    }

    private void showMessageInLog(String message) {
        Log.d(Utilities.TAG, message);
    }

    public void continueConnecting() {
        try {
            BluetoothDevice bluetoothDevice = bluetoothAdapter.getRemoteDevice(Utilities.BLUETOOTH_MODULE_MAC_ADDRESS);
            createBluetoothSocket(bluetoothDevice, Build.VERSION.SDK_INT);
            bluetoothAdapter.cancelDiscovery();
            establishConnectionWithTheBluetoothModule();
        } catch (BluetoothException bex) {
            bex.manageException(mainActivity);
        }
    }

    public void createBluetoothSocket(BluetoothDevice bluetoothDevice, int buildVersion) throws BluetoothException {
        try {
            if (buildVersion >= Utilities.MINIMUM_SDK_VERSION) {
                createInsecureRfCommSocketToServiceRecord(bluetoothDevice);
            } else
                Utilities.bluetoothSocket = bluetoothDevice.createInsecureRfcommSocketToServiceRecord(Utilities.SPP_UUID_SERVICE);
        } catch (Exception ex) {
            throw new BluetoothExceptionExitApp(Utilities.FATAL_ERROR, ex.getMessage());
        }
    }

    public void createInsecureRfCommSocketToServiceRecord(BluetoothDevice bluetoothDevice) throws Exception {
        final Method method = bluetoothDevice.getClass().getMethod("createInsecureRfcommSocketToServiceRecord", UUID.class);
        Utilities.bluetoothSocket = (BluetoothSocket) method.invoke(bluetoothDevice, Utilities.SPP_UUID_SERVICE);
    }

    public void establishConnectionWithTheBluetoothModule() {
        try {
            Utilities.bluetoothSocket.connect();
        } catch (IOException e) {
            disconnectFromBluetoothDevice();
        }
    }

    public void disconnectFromBluetoothDevice() {
        try {
            disconnectBluetoothModule();
        } catch (BluetoothException bex) {
            bex.printStackTrace();
            bex.manageException(mainActivity);
        }
    }

    private void disconnectBluetoothModule() throws BluetoothException {
        if(Utilities.bluetoothSocket != null) {
            try {
                Utilities.bluetoothSocket.close();
            } catch (IOException e) {
                throw new BluetoothExceptionExitApp(Utilities.FATAL_ERROR, Utilities.UNABLE_TO_CLOSE_SOCKET);
            }
        }
    }
}
