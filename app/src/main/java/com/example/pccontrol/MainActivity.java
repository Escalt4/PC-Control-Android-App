package com.example.pccontrol;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class MainActivity extends AppCompatActivity implements DialogResult {
    static final String LOG_TAG = "MyApp";

    BlockingQueue<String> queue = new LinkedBlockingQueue<String>();

    SharedPreferences settings;
    SharedPreferences.Editor prefEditor;

    Button buttonOff;
    Button buttonSleep;
    Button buttonHibernate;
    Button buttonOn;

    String macAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportActionBar().hide();

        settings = getSharedPreferences("Settings", MODE_PRIVATE);
        prefEditor = settings.edit();
        macAddress = settings.getString("macAddress", "fc:aa:14:ba:50:1d");


        buttonOff = findViewById(R.id.buttonOff);
        buttonSleep = findViewById(R.id.buttonSleep);
        buttonHibernate = findViewById(R.id.buttonHibernate);
        buttonOn = findViewById(R.id.buttonOn);

//        editTextMacAddress = findViewById(R.id.editTextMacAddress);
//        editTextMacAddress.setText(macAddress);
//        editTextMacAddress.addTextChangedListener(new TextWatcher() {
//            public void afterTextChanged(Editable s) {
//
//            }
//
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//            }
//
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//            }
//        });

        socketTread();

        EventBus.getDefault().register(this);
    }


    public void socketTread() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        DatagramSocket socket = new DatagramSocket(5005);
                        socket.setBroadcast(true);
                        socket.setSoTimeout(250);

                        String data;
                        while (true) {
                            try {
                                byte[] buffer;
                                if (!queue.isEmpty()) {
                                    buffer = queue.take().getBytes();
                                } else {
                                    buffer = ("ConnectionTest").getBytes();
                                }

                                DatagramPacket sendPacket = new DatagramPacket(buffer, buffer.length, InetAddress.getByName("255.255.255.255"), 4445);
                                socket.send(sendPacket);


                                byte[] recvBuf = new byte[256];
                                DatagramPacket receivePacket = new DatagramPacket(recvBuf, recvBuf.length);
                                socket.receive(receivePacket);
                                data = new String(receivePacket.getData()).trim();

                            } catch (Exception ex) {
                                data = "Failed";
//                                Log.e(LOG_TAG, Log.getStackTraceString(ex));
                                Log.e(LOG_TAG, ex.getMessage());
                            }

                            EventBus.getDefault().postSticky(new GetResult(data));

                            SystemClock.sleep(250);
                        }
                    } catch (Exception ex) {
//                        Log.e(LOG_TAG, Log.getStackTraceString(ex));
                        Log.e(LOG_TAG, ex.getMessage());
                    }
                }
            }
        }).start();
    }


    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void onGetResult(GetResult event) {
        String messege = event.getMessege();

        switch (messege) {
            case "TestPass":
                buttonOff.setEnabled(true);
                buttonSleep.setEnabled(true);
                buttonHibernate.setEnabled(true);
                break;

            case "OffSuccess":
                Toast.makeText(getApplicationContext(), "Компьютер выключен", Toast.LENGTH_SHORT).show();
                break;

            case "SleepSuccess":
                Toast.makeText(getApplicationContext(), "Компьютер переведен в сон", Toast.LENGTH_SHORT).show();
                break;

            case "HibernateSuccess":
                Toast.makeText(getApplicationContext(), "Компьютер переведен в гибернацию", Toast.LENGTH_SHORT).show();
                break;

            case "Failed":
                buttonOff.setEnabled(false);
                buttonSleep.setEnabled(false);
                buttonHibernate.setEnabled(false);
                break;
        }
    }

    public void wakeOnLan() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    byte[] macBytes = new byte[6];
                    String[] hex = macAddress.split("(\\:|\\-)");

                    for (int i = 0; i < 6; i++) {
                        macBytes[i] = (byte) Integer.parseInt(hex[i], 16);
                    }

                    byte[] bytes = new byte[6 + 16 * macBytes.length];
                    for (int i = 0; i < 6; i++) {
                        bytes[i] = (byte) 0xff;
                    }
                    for (int i = 6; i < bytes.length; i += macBytes.length) {
                        System.arraycopy(macBytes, 0, bytes, i, macBytes.length);
                    }

                    DatagramSocket datagramSocket = new DatagramSocket();
                    datagramSocket.setBroadcast(true);
                    DatagramPacket datagramPacket = new DatagramPacket(bytes, bytes.length, InetAddress.getByName("255.255.255.255"), 9);
                    for (int i = 0; i < 5; i++) {
                        datagramSocket.send(datagramPacket);
                        SystemClock.sleep(10);
                    }
                    datagramSocket.close();
                } catch (Exception ex) {
                    Log.e(LOG_TAG, Log.getStackTraceString(ex));
//                    Log.e(LOG_TAG, ex.getMessage());
                }
            }
        }).start();
    }

    public void showDialog(String title, String command) {
        ConfirmDialogFragment confirmDialog = new ConfirmDialogFragment();
        Bundle args = new Bundle();
        args.putString("title", title);
        args.putString("command", command);
        confirmDialog.setArguments(args);
        confirmDialog.show(getSupportFragmentManager(), "custom");
    }

    @Override
    public void doCommand(String command) {
        switch (command) {
            case "Off":
            case "Sleep":
            case "Hibernate":
                queue.offer(command);
                break;
            case "On":
                wakeOnLan();
                Toast.makeText(getApplicationContext(), "Компьютер включен", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    @Override
    public void changePreferences(String key, String value) {
        prefEditor.putString(key, value);
        prefEditor.apply();

        macAddress = settings.getString("macAddress", "fc:aa:14:ba:50:1d");
    }

    // Обработка нажатий кнопок
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.buttonSettings:
                EditPrefDialogFragment editPrefDialog = new EditPrefDialogFragment();
                Bundle args = new Bundle();
                args.putString("key", "macAddress");
                args.putString("value", macAddress);
                editPrefDialog.setArguments(args);
                editPrefDialog.show(getSupportFragmentManager(), "custom");
                break;

            case R.id.buttonOff:
                showDialog("Компьютер будет выключен", "Off");
                break;

            case R.id.buttonSleep:
                showDialog("Компьютер будет переведен в режим сна", "Sleep");
                break;

            case R.id.buttonHibernate:
                showDialog("Компьютер будет переведен в гибернацию", "Hibernate");
                break;

            case R.id.buttonOn:
                showDialog("Компьютер будет включен", "On");
                break;

        }
    }
}