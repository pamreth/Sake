package com.pamela.sake.exceptions;

import android.app.Activity;

import com.pamela.sake.MainActivity;


public class BluetoothExceptionExitApp extends BluetoothException {

    public BluetoothExceptionExitApp(String type, String message) {
        super(type, message);
    }

    @Override
    public void manageException(Activity activity) {
        MainActivity mainActivity = (MainActivity) activity;
        mainActivity.exitAppWithError(super.getType(), super.getMessage());
    }
}
