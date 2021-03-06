package co.example.junjen.mobileinstagram.network;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.ParcelUuid;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;
import java.util.UUID;

/**
 * Created by Tou on 10/11/2015.
 */
public class Bluetooth {


    private OutputStream outputStream;
    private InputStream inStream;

    private void Bluetooth() {
        BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();
        try{
            if (btAdapter != null) {
                if (btAdapter.isEnabled()) {
                    Set<BluetoothDevice> bondedDevices = btAdapter.getBondedDevices();

                    if(bondedDevices.size() > 0){
                        BluetoothDevice[] devices = (BluetoothDevice[]) bondedDevices.toArray();
                        BluetoothDevice device = devices[0];
                        ParcelUuid[] uuids = device.getUuids();
                        BluetoothSocket socket = device.createRfcommSocketToServiceRecord(uuids[0].getUuid());
                        socket.connect();
                        outputStream = socket.getOutputStream();
                        inStream = socket.getInputStream();
                    }

                    Log.e("error", "No appropriate paired devices.");
                }else{
                    Log.e("error", "BluetoothSwipeService is disabled.");
                }
            }

        }catch (IOException e){
            e.printStackTrace();
        }
    }

    public void write(String s) throws IOException {
        outputStream.write(s.getBytes());
//        outputStream.write("hola mundo".getBytes());
    }

    public void run() {
        final int BUFFER_SIZE = 1024;
        byte[] buffer = new byte[BUFFER_SIZE];
        int bytes = 0;
        int b = BUFFER_SIZE;

        while (true) {
            try {
                bytes = inStream.read(buffer, bytes, BUFFER_SIZE - bytes);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
