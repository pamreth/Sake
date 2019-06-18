package com.pamela.sake.exceptions;

import android.app.Activity;

import com.pamela.sake.MainActivity;
import com.pamela.sake.commons.Utilities;

/**
 * Created by lemme on 5/12/15.
 */
public class BluetoothExceptionAdapterDisabled extends BluetoothException {

    public BluetoothExceptionAdapterDisabled() {
        super(Utilities.REQUEST_USER, Utilities.BLUETOOTH_ADAPTER_DISABLED);
    }

    @Override
    public void manageException(Activity activity) {
        MainActivity mainActivity = (MainActivity) activity;
        mainActivity.promptUserToEnableBluetooth();
    }
}
