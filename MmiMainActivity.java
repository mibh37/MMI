package com.example.lee.mmi2;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Scanner;

public class MainActivity extends AppCompatActivity {

    UsbManager mUsbManager;
    protected static final int menu_about = Menu.FIRST+2;
    protected static final int menu_quit = Menu.FIRST;
    private static final String TAG = "tag";
    private EditText field_steps;
    private EditText field_motor_acc;
    private EditText field_motor_dec;
    private EditText field_timer_delay_break;
    private CompoundButton switch_rpm_roller;
    private Button button_write;
    private Button button_read;
    private TextView field_usbInfo;
    String i="";
    String rpm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setField();
        setToggleListeners();
        isExternalStorageReady();
        getDevice();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        super.onCreateOptionsMenu(menu);
        menu.add(0, menu_about, 0, "裝置訊息");
        //menu.add(0, itemID, 0, 顯示在按紐的字串)
        menu.add(0, menu_quit, 0, "結束");
        return true;
    }
    @Override
    public  boolean onOptionsItemSelected(MenuItem item){
        super.onOptionsItemSelected(item);
        switch(item.getItemId()){
            case menu_about:
                openoptionsdialog();
                break;
            case menu_quit:
                break;
        }
        return true;
    }

    private void setField() {
        field_steps = (EditText) findViewById(R.id.value_steps);
        field_motor_acc = (EditText) findViewById(R.id.value_motor_acc);
        field_motor_dec = (EditText) findViewById(R.id.value_motor_dec);
        field_timer_delay_break = (EditText) findViewById(R.id.value_time_delay_break);
        switch_rpm_roller = (Switch) findViewById(R.id.switch_rpm_roller);
        button_write = (Button) findViewById(R.id.write);
        button_read = (Button) findViewById(R.id.read);
        field_usbInfo = (TextView) findViewById(R.id.usb_info);
    }
    private void setListeners() {
        button_write.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                getOutputStream();
            }
        });
        button_read.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                getInputStream();
            }
        });
    }
    private void setToggleListeners(){
        switch_rpm_roller.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    rpm = "100";
                } else{
                    rpm = "50";
                }
            }
        });
    }

    private void openoptionsdialog() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
        dialog.setTitle("USB裝置訊息")
                .setMessage(i)
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .show();
    }

    public void getDevice() //這個method進行Discover a device and then enumerating devices.
    {
        mUsbManager = (UsbManager) getSystemService(Context.USB_SERVICE);
        HashMap<String, UsbDevice> deviceList = mUsbManager.getDeviceList();
        Log.d(TAG, deviceList.size() + " USB device(s) found");
        Iterator<UsbDevice> deviceIterator = deviceList.values().iterator();
        //i = deviceList.size() + "USB device(s) found";
        //field_usbInfo.setText(i);
        while (deviceIterator.hasNext()) {
            UsbDevice device = deviceIterator.next();
            i += "\n" + "DeviceID: " + device.getDeviceId() + "\n"
                    + "DeviceName: " + device.getDeviceName() + "\n"
                    + "DeviceClass: " + device.getDeviceClass() + " - "
                    + "DeviceSubClass: " + device.getDeviceSubclass() + "\n"
                    + "VendorID: " + device.getVendorId() + "\n"
                    + "ProductID: " + device.getProductId() + "\n";
            Log.d("8555", "" + device); //如果寫成Log.d("1", device); 會錯誤,所以()內的參數只要加上一段字串就好像可以了
        }
        //field_usbInfo.setText(deviceList.size() + "USB device(s) found");
    }
/*
    public void sendData(){
        UsbDevice device = (UsbDevice) getIntent().getParcelableExtra(UsbManager.EXTRA_DEVICE);
        UsbInterface intf = device.getInterface(0);
        UsbEndpoint endpointIn = intf.getEndpoint(0);
        UsbEndpoint endpointOut = intf.getEndpoint(1);
        UsbDeviceConnection connection = mUsbManager.openDevice(device);
        connection.claimInterface(intf, true);
        //byte[] buffer = new byte[] {(byte) 'H','E','L','L','O'};
        //int out = connection.bulkTransfer(endpointIn, buffer, buffer.length, 3000);
    }
*/
    public void isExternalStorageReady(){
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)){
            setListeners();
        } else {
            Toast.makeText(MainActivity.this, "USB裝置未連接", Toast.LENGTH_SHORT).show();
        }
    }
    public void getOutputStream()
    {
        //this.deleteFile("textFile.txt");
        //File file = new File(Context.getImgPath("text.txt"));
        //String outputFile = "MyAndroid.txt";
        //String content = "Hello Java Code Geeks";
        String content = field_steps.getText() + "\n"
                        +field_motor_acc.getText() + "\n"
                        +field_motor_dec.getText() + "\n"
                        +field_timer_delay_break.getText() + "\n"
                        +rpm;
        //byte[] bytes = content.getBytes();
        String root = Environment.getExternalStorageDirectory().toString(); //這個檔案路徑是手機內部的路徑,但是與sdcard上相通,在這個路徑建立檔案,sdcard也會有.
        //String root = "/storage/usb/storage"; //usb storage路徑
        //File file = new File(root + "/text_dir", "textFile.txt");
        File file = new File(root + "/" + "textFile" + "/" + "MyAndroid.txt");
        //field_usbInfo.setText("P:" + file.getAbsolutePath());
        //File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "albumName");
        if(!file.mkdirs()){ // !:把boolean反向的符號, 當file.mkdirs=false, if(!false)=if(true)然後執行loop.
            Log.e("123", "Directory not created");
            //file.delete(); //只刪除最後面的那一層資料夾.
        }

        try {
            FileWriter mFileWriter = new FileWriter(file);
            //OutputStream mFileWriter = new FileOutputStream(file + "/" + outputFile);
            mFileWriter.write(content);
            mFileWriter.close();
            Toast.makeText(MainActivity.this, "設定完成", Toast.LENGTH_SHORT).show();
        }
        catch (IOException e){
            Log.e("123", "File building failed:" + e.toString());
        }
/*
        try {
            //OutputStream out = new FileOutputStream(OUTPUT_FILE);
            //FileOutputStream out = openFileOutput(OUTPUT_FILE, 0);
            //OutputStreamWriter fw = new OutputStreamWriter(out);
            //out.write(bytes);
            //out.write(bytes[0]);
            //out.write(bytes,4,10);
            //fw.close();
        } catch (IOException e) {
            Log.e("123", "File write failed:" + e.toString());
        }
*/
    }

    public void getInputStream(){
        try{
            String root = Environment.getExternalStorageDirectory().toString();
            FileInputStream file = new FileInputStream(root + "/" + "textFile" + "/" + "MyAndroid.txt");
            Scanner scanner = new Scanner(file);
            if(scanner.hasNext()){
                field_steps.setText(scanner.nextLine());
                field_motor_acc.setText(scanner.nextLine());
                field_motor_dec.setText(scanner.nextLine());
                field_timer_delay_break.setText(scanner.nextLine());
                rpm = scanner.nextLine();
                if(rpm.equals("100")){
                    switch_rpm_roller.setChecked(true);
                }
            }
            scanner.close();
            Toast.makeText(MainActivity.this, "讀取完成", Toast.LENGTH_SHORT).show();
            /*
            String root = Environment.getExternalStorageDirectory().toString();
            FileReader file = new FileReader(root + "/" + "textFile" + "/" + "MyAndroid.txt");
            BufferedReader mBufferedReader = new BufferedReader(file);
            String readText = "";
            String textLine = mBufferedReader.readLine();
            while(textLine!=null){
                readText += textLine+"\n";
                textLine = mBufferedReader.readLine();
            }
            */
        }
        catch (IOException e){
            Log.e("123", "File not found" + e.toString());
        }
    }


}
