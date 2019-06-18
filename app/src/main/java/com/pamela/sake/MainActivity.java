package com.pamela.sake;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.pamela.sake.bluetooth.BluetoothHandler;
import com.pamela.sake.bluetooth.ConnectedThread;
import com.pamela.sake.commons.Utilities;

public class MainActivity extends AppCompatActivity {

    private Switch bluetoothSwitch;
    private TextView bluetoothState, display;
    private BroadcastReceiver broadcastReceiver;
    private BluetoothHandler bluetoothHandler;
    private ConnectedThread connectedThread;
    private Handler handler;
    private StringBuilder DataStringIN = new StringBuilder();

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bluetoothSwitch = this.findViewById(R.id.bluetoothOnOff);
        bluetoothState = this.findViewById(R.id.moduleState);
        display =  this.findViewById(R.id.idDisplay);
        this.handler = new Handler() {
            public void handleMessage(android.os.Message msg) {
                if (msg.what == Utilities.RECEIVE_MESSAGE) {
                    String readMessage = (String) msg.obj;
                    DataStringIN.append(readMessage);

                    int endOfLineIndex = DataStringIN.indexOf("#");

                    if (endOfLineIndex > 0) {
                        String dataInPrint = DataStringIN.substring(0, endOfLineIndex);
                        display.setText("Datos \n\n" + dataInPrint);//<-<- PARTE A MODIFICAR >->->
                        DataStringIN.delete(0, DataStringIN.length());
                    }
                }
            }
        };
        initializeBluetoothHandler(new BluetoothHandler(BluetoothAdapter.getDefaultAdapter(), this));
    }

    @Override
    protected void onResume() {
        super.onResume();
        CompoundButton.OnCheckedChangeListener onCheckedChangeListener =
                initializeOnCheckedChangeListener(bluetoothSwitch);
        setCheckedStateListenerToBluetoothSwitch(bluetoothSwitch, onCheckedChangeListener);
        createBroadcastReceiverToListenToBluetoothAdapterState();
        registerBroadcastReceiverToBluetoothEvents();
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterBroadcastReceiverToBluetoothEvents();
    }


    private CompoundButton.OnCheckedChangeListener initializeOnCheckedChangeListener(final Switch bluetoothSwitch) {
        CompoundButton.OnCheckedChangeListener onCheckedChangeListener = new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    continueConnecting(bluetoothHandler.connectToBluetoothDevice());
                }
                else {
                    bluetoothHandler.disconnectFromBluetoothDevice();
                    unregisterBroadcastReceiverToBluetoothEvents();
                    bluetoothState.setText("Desconectado");
                }
            }
        };
        return onCheckedChangeListener;
    }

    public void setCheckedStateListenerToBluetoothSwitch(final Switch bluetoothSwitch, CompoundButton.OnCheckedChangeListener onCheckedChangeListener) {
        bluetoothSwitch.setOnCheckedChangeListener(onCheckedChangeListener);
    }

    public void continueConnecting(Boolean isContinue) {
        if(isContinue) {
            bluetoothState.setText("Conectado");
            bluetoothHandler.continueConnecting();
            createDataStreamToTalkToTheServer();
        }
    }

    private void createDataStreamToTalkToTheServer() {
        connectedThread = new ConnectedThread(Utilities.bluetoothSocket, this.handler);
        connectedThread.start();
        Log.d(Utilities.TAG, "... Listening ... ");
    }

    private void unregisterBroadcastReceiverToBluetoothEvents() {
        try {
            this.unregisterReceiver(broadcastReceiver);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void initializeBluetoothHandler(BluetoothHandler handler) {
        bluetoothHandler = handler;
    }

    public void promptUserToEnableBluetooth() {
        Intent bluetoothEnableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        this.startActivityForResult(bluetoothEnableIntent, Utilities.ENABLE_BLUETOOTH_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == Utilities.ENABLE_BLUETOOTH_REQUEST && resultCode == RESULT_OK) {
            continueConnecting(bluetoothHandler.connectToBluetoothDevice());
        }

        if(requestCode == Utilities.ENABLE_BLUETOOTH_REQUEST && resultCode == RESULT_CANCELED) {
            unregisterBroadcastReceiverToBluetoothEvents();
            bluetoothHandler.disconnectFromBluetoothDevice();
        }
    }

    public void exitAppWithError(String errorType, String errorMessage) {
        String messageToShow = errorType + Utilities.ERROR_MESSAGE_JOIN_CHAR + errorMessage;
        Toast.makeText(getBaseContext(), messageToShow, Toast.LENGTH_SHORT);
        unregisterBroadcastReceiverToBluetoothEvents();
        bluetoothHandler.disconnectFromBluetoothDevice();
        finish();
    }

    public void createBroadcastReceiverToListenToBluetoothAdapterState() {
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);
                Boolean bluetoothAdapterOFF = (state == BluetoothAdapter.STATE_OFF ||  state == BluetoothAdapter.STATE_TURNING_OFF);
                if(action == BluetoothAdapter.ACTION_STATE_CHANGED && bluetoothAdapterOFF) {
                    unregisterBroadcastReceiverToBluetoothEvents();
                    bluetoothHandler.disconnectFromBluetoothDevice();
                    bluetoothSwitch.setChecked(false);
                }
            }
        };
    }

    public void registerBroadcastReceiverToBluetoothEvents() {
        IntentFilter bluetoothStateChangedFilter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
        this.registerReceiver(broadcastReceiver, bluetoothStateChangedFilter);
    }

    @Override
    public void onBackPressed() {
        unregisterBroadcastReceiverToBluetoothEvents();
        bluetoothHandler.disconnectFromBluetoothDevice();
        bluetoothState.setText("Desconectado");
        bluetoothSwitch.setChecked(false);
        super.onBackPressed();
    }

}