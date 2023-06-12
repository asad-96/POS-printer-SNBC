package com.example.possdkprodemo;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.app.Activity;
import android.os.Bundle;
import android.os.Message;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.possdkpro.POSSDK;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends Activity {

    public Button button;
    public Button bt_connect;
    public Button bt_printSample;
    public Button bt_barcodeSample;
    public Button bt_imageSample;
    public Button bt_search;

    public Spinner sp_printname;
    public Spinner sp_serialportname;
    public Spinner sp_serialportbaud;

    public LinearLayout ll_portname;
    public LinearLayout ll_simple;

    public ListView lv_devicesInfo;
    public TextView tv_tips;    public LinearLayout ll_portbaud;
    public LinearLayout ll_netname;

    public EditText ed_ipaddress;

    public RadioButton rb_usb;
    public RadioButton rb_serial;
    public RadioButton rb_bluetooth;
    public RadioButton rb_net;

    PrintTest printTest;
    public String status;
    public Boolean isConnect;
    ArrayAdapter<String> adtDevices;
    List<String> lstDevices = new ArrayList<String>();
    int nReturn;
    final String[] printName = {"BTP-U80 PLUS","BTP-N60","BTP-N80","BTP-N90","BT-NH80M","BTP-M300"};
    static final String[] PERMISSION = new String[]{
            Manifest.permission.READ_CONTACTS,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_CALL_LOG,
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setPermissions();
        ll_portname = findViewById(R.id.ll_portname);
        ll_portbaud = findViewById(R.id.ll_portbaud);
        ll_netname = findViewById(R.id.ll_netname);
        ll_simple = findViewById(R.id.ll_simple);
        ll_portname.setVisibility(View.GONE);
        ll_portbaud.setVisibility(View.GONE);
        ll_netname.setVisibility(View.GONE);
        tv_tips = findViewById(R.id.tv_tips);

        //搜索列表
        lv_devicesInfo = findViewById(R.id.lv_devicesInfo);
        lv_devicesInfo.setFocusable(false);
        adtDevices = new ArrayAdapter<String>(MainActivity.this,
                android.R.layout.simple_list_item_1, lstDevices);
        lv_devicesInfo.setAdapter(adtDevices);
        lv_devicesInfo.setOnItemClickListener(new ItemClickEvent());

        //初始化
        POSSDK possdkpro = new POSSDK(MainActivity.this);
        printTest = new PrintTest(possdkpro);
        isConnect = false;//是否已经连接标识
        nReturn = 0;
        status = "";

        //严苛模式，网口需要
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                .detectDiskReads().detectDiskWrites().detectNetwork()
                .penaltyLog().build());

        StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
                .detectLeakedSqlLiteObjects().penaltyLog().penaltyDeath()
                .build());


        //打印机名称
        sp_printname = findViewById(R.id.sp_printname);

        ArrayAdapter<String> adapter_printName = new ArrayAdapter<>(this,android.R.layout.simple_spinner_item,printName);
        adapter_printName.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp_printname.setAdapter(adapter_printName);
        sp_printname.setSelection(0);

        //串口名称
        sp_serialportname = findViewById(R.id.sp_serialportname);
        final String[] portName = {"dev/ttymxc0", "dev/ttymxc1", "dev/ttymxc2", "dev/ttymxc3", "dev/ttymxc4", "dev/ttyS0", "dev/ttyS1", "dev/ttyS2","dev/ttyS3", "dev/ttyGS0", "dev/ttyGS1", "dev/ttyGS2","dev/ttyGS3"};
        ArrayAdapter<String> adapterportName = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, portName);
        ((ArrayAdapter) adapterportName).setDropDownViewResource(android.R.layout.simple_spinner_item);
        sp_serialportname.setAdapter(adapterportName);
        sp_serialportname.setSelection(0);

        //波特率
        sp_serialportbaud = findViewById(R.id.sp_serialportbaud);
        final String[] portbaud_items = {"9600","19200","38400","57600","115200"};
        ArrayAdapter<String> adapter_portbaud = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, portbaud_items);
        adapter_portbaud.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp_serialportbaud.setAdapter(adapter_portbaud);
        sp_serialportbaud.setSelection(4);

        //选择usb
        rb_usb = findViewById(R.id.rb_usb);
        rb_usb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ll_portname.setVisibility(View.GONE);
                ll_portbaud.setVisibility(View.GONE);
                ll_netname.setVisibility(View.GONE);
                bt_connect.setVisibility(View.GONE);
                bt_search.setVisibility(View.VISIBLE);
                tv_tips.setVisibility(View.VISIBLE);
                rb_serial.setChecked(false);
                rb_bluetooth.setChecked(false);
                rb_net.setChecked(false);
                tv_tips.setText(getResources().getString(R.string.ClickButtonUSB));
            }
        });

        //选择串口
        rb_serial = findViewById(R.id.rb_serial);
        rb_serial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ll_portname.setVisibility(View.VISIBLE);
                ll_portbaud.setVisibility(View.VISIBLE);
                ll_netname.setVisibility(View.GONE);
                bt_connect.setVisibility(View.VISIBLE);
                bt_search.setVisibility(View.GONE);
                tv_tips.setVisibility(View.GONE);
                rb_usb.setChecked(false);
                rb_bluetooth.setChecked(false);
                rb_net.setChecked(false);
            }
        });

        //蓝牙
        rb_bluetooth = findViewById(R.id.rb_bluetooth);
        rb_bluetooth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ll_portname.setVisibility(View.GONE);
                ll_portbaud.setVisibility(View.GONE);
                ll_netname.setVisibility(View.GONE);
                bt_connect.setVisibility(View.GONE);
                bt_search.setVisibility(View.VISIBLE);
                tv_tips.setVisibility(View.VISIBLE);
                rb_usb.setChecked(false);
                rb_serial.setChecked(false);
                rb_net.setChecked(false);
                tv_tips.setText(getResources().getString(R.string.ClickButtonBlueThooth));

            }
        });

        //网口
        rb_net = findViewById(R.id.rb_net);
        rb_net.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ll_portname.setVisibility(View.GONE);
                ll_portbaud.setVisibility(View.GONE);
                ll_netname.setVisibility(View.VISIBLE);
                bt_connect.setVisibility(View.VISIBLE);
                bt_search.setVisibility(View.VISIBLE);
                tv_tips.setVisibility(View.VISIBLE);
                rb_usb.setChecked(false);
                rb_serial.setChecked(false);
                rb_bluetooth.setChecked(false);
                tv_tips.setText(getResources().getString(R.string.ClickButtonNet));
            }
        });


        //搜索
        bt_search = findViewById(R.id.bt_search);
        bt_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lstDevices.clear();
                adtDevices.notifyDataSetChanged(); //Refresh the list
                String[] deviceInfo = new String[]{"",""};
                int nNum = 0;
                if (rb_usb.isChecked()) { //usb
                    nNum = printTest.EnumDevice(1,deviceInfo,deviceInfo.length);
                    if (nNum > 0) {
                        String[] usbInfo = deviceInfo[0].split("@");
                        for (int i = 0; i < usbInfo.length; i++) {
                            lstDevices.add(usbInfo[i]);
                        }
                        adtDevices.notifyDataSetChanged(); //Refresh the list
                    }else{
                        Toast.makeText(MainActivity.this,getResources().getString(R.string.DeviceNotFound),Toast.LENGTH_LONG).show();
                    }
                }
                if (rb_net.isChecked()) { //网口
                    Toast.makeText(MainActivity.this,getResources().getString(R.string.Searching),Toast.LENGTH_LONG).show();
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            int nNum = printTest.EnumDevice(2,deviceInfo,deviceInfo.length);
                            if (nNum > 0) {
                                String[] usbInfo = deviceInfo[0].split("@");
                                for (int i = 0; i < usbInfo.length; i++) {
                                    lstDevices.add(usbInfo[i]);
                                }
                                mhandler.obtainMessage(3).sendToTarget();
                            }else{
                                mhandler.obtainMessage(4).sendToTarget();

                            }
                        }
                    }).start();
                }

                //蓝牙
                if (rb_bluetooth.isChecked()) {
                    nNum = printTest.EnumDevice(3,deviceInfo,deviceInfo.length);
                    if (nNum > 0) {
                        String[] usbInfo = deviceInfo[0].split("@");
                        for (int i = 0; i < usbInfo.length; i++) {
                            lstDevices.add(usbInfo[i]);
                        }
                        adtDevices.notifyDataSetChanged(); //Refresh the list
                    }else{
                        Toast.makeText(MainActivity.this,getResources().getString(R.string.DeviceNotFound),Toast.LENGTH_LONG).show();
                    }
                }
            }
        });


        //连接
        ed_ipaddress = findViewById(R.id.ed_ipaddress);
        bt_connect = findViewById(R.id.bt_connect);
        bt_connect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String connectName = String.valueOf(bt_connect.getText());
                if(connectName.contains("断开连接") ||connectName.contains("Disconnect")){
                    int nReturn = printTest.ClosePrinter();
                    Toast.makeText(MainActivity.this,getResources().getString(R.string.closeportsuccess),Toast.LENGTH_LONG).show();;
                    isConnect = false;
                    bt_connect.setText(getResources().getString(R.string.at_connect));
                    setButtonEnable(false);
                }else{
                    //选中usb
                    if ((rb_usb.isChecked()) && (!isConnect)){
                        String modelName = printName[sp_printname.getSelectedItemPosition()];
                        int nReturn = printTest.OpenPrinter(modelName,"USB");
                        if (nReturn == 0) {
                            Toast.makeText(MainActivity.this,getResources().getString(R.string.openportsuccess),Toast.LENGTH_LONG).show();;
                            isConnect = true;
                            bt_connect.setText(getResources().getString(R.string.at_disconnect));
                            setButtonEnable(true);
                        }else {
                            Toast.makeText(MainActivity.this,getResources().getString(R.string.openportfail)+"nReturn = "+nReturn,Toast.LENGTH_LONG).show();;
                        }
                    }
                    //选中网口
                    if ((rb_net.isChecked()) && (!isConnect)){
                        String modelName = printName[sp_printname.getSelectedItemPosition()];
                        int nReturn = printTest.OpenPrinter(modelName,ed_ipaddress.getText().toString());
                        if (nReturn == 0) {
                            Toast.makeText(MainActivity.this,getResources().getString(R.string.openportsuccess),Toast.LENGTH_LONG).show();;
                            isConnect = true;
                            bt_connect.setText(getResources().getString(R.string.at_disconnect));
                            setButtonEnable(true);
                        }else {
                            Toast.makeText(MainActivity.this,getResources().getString(R.string.openportfail)+"nReturn = "+nReturn,Toast.LENGTH_LONG).show();;
                        }
                    }

                    //选中串口
                    if (rb_serial.isChecked() && (!isConnect)) {
                        String modelName = printName[sp_printname.getSelectedItemPosition()];
                        // "COM2:115200"
                        String printer_portname = portName[sp_serialportname.getSelectedItemPosition()];
                        int printer_baudrate = Integer.parseInt(portbaud_items[sp_serialportbaud.getSelectedItemPosition()]);
                        int nReturn = printTest.OpenPrinter(modelName,printer_portname+"|"+printer_baudrate);
                        if (nReturn == 0) {
                            Toast.makeText(MainActivity.this,getResources().getString(R.string.openportsuccess),Toast.LENGTH_LONG).show();;
                            isConnect = true;
                            bt_connect.setText(getResources().getString(R.string.at_disconnect));
                            setButtonEnable(true);
                        }else {
                            Toast.makeText(MainActivity.this,getResources().getString(R.string.openportfail)+"nReturn = "+nReturn,Toast.LENGTH_LONG).show();;
                        }
                    }
                }
            }
        });

        //打印样张
        bt_printSample = findViewById(R.id.bt_printSample);
        bt_printSample.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                byte[] errorStatus = new byte[100];
                Arrays.fill(errorStatus,(byte)0x00);
                int[] statusLength = new int[]{0x00,0x00};
                int nReturn = 0;
                String status = "";
                nReturn = printTest.Sample_Restaurant(errorStatus,statusLength);
                status = new String(errorStatus,0,statusLength[0]);
                if (status.contains("Normal")) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    printTest.Sample_Restaurant_En(errorStatus,statusLength);
                    printTest.SampleXML(MainActivity.this);
                    nReturn = printTest.SampleXMLPageMode(MainActivity.this);
                }

                if (nReturn == 0) {
                    Toast.makeText(MainActivity.this,nReturn+getResources().getString(R.string.success)+getResources().getString(R.string.status)+status,Toast.LENGTH_LONG).show();;

                }else {
                    Toast.makeText(MainActivity.this,getResources().getString(R.string.fail)+getResources().getString(R.string.status)+status,Toast.LENGTH_LONG).show();
                }
            }
        });

        //bt_barcodeSample;
        bt_barcodeSample = findViewById(R.id.bt_barcodeSample);
        bt_barcodeSample.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                byte[] errorStatus = new byte[100];
                Arrays.fill(errorStatus,(byte)0x00);
                int[] statusLength = new int[]{0x00,0x00};

                String status = "";
                int nReturn = printTest.Sample_PrintBarCode(errorStatus,statusLength);
                status = new String(errorStatus,0,statusLength[0]);
                if (nReturn == 0) {
                    Toast.makeText(MainActivity.this,getResources().getString(R.string.success)+getResources().getString(R.string.status)+status,Toast.LENGTH_LONG).show();;

                }else {
                    Toast.makeText(MainActivity.this,getResources().getString(R.string.fail)+getResources().getString(R.string.status)+status,Toast.LENGTH_LONG).show();;
                }
            }
        });
        //bt_imageSample;
        bt_imageSample = findViewById(R.id.bt_imageSample);
        bt_imageSample.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this,getResources().getString(R.string.Printing),Toast.LENGTH_LONG).show();;

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        byte[] errorStatus = new byte[100];
                        Arrays.fill(errorStatus,(byte)0x00);
                        int[] statusLength = new int[]{0x00,0x00};

                        nReturn = printTest.Sample_PrintImage(MainActivity.this,errorStatus,statusLength);
                        status = new String(errorStatus,0,statusLength[0]);
                        mhandler.obtainMessage(5).sendToTarget();
                    }
                }).start();
            }
        });


        button = findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              /*  POSSDK possdk = new POSSDK(MainActivity.this);
                int handle = possdk.OpenPrinter("BTP-S80","USBAPI|824");
                possdk.PrintText(handle, "Hello World,你好，世界！", "Alignment=1");
                possdk.PrintText(handle, "Hello World,你好，世界！", "Font=FontA|Alignment=1|Bold=1|Italics=1|Underline=1");
                String alginFileName = String.valueOf(Environment.getExternalStorageDirectory())+"/StreamImage/Look.bmp";
                Log.e("11111",alginFileName);
                possdk.PrintImageFile(handle, alginFileName, "x=0");

                possdk.FeedLines(handle,10);*/

/*                possdk.PaperCut(handle,0,0);
                possdk.ClosePrinter(handle);*/

            }
        });
    }


    //设置样张控件是否可用
    public void setButtonEnable(boolean enable){
        bt_printSample.setEnabled(enable);
        bt_barcodeSample.setEnabled(enable);
        bt_imageSample.setEnabled(enable);
    }
    public static String bytesToHexString(byte[] src) {
        StringBuilder stringBuilder = new StringBuilder("");
        if (src == null || src.length <= 0) {
            return null;
        }
        for (int i = 0; i < src.length; i++) {
            int v = src[i] & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
        }
        return stringBuilder.toString();
    }
    private Handler mhandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    Toast.makeText(MainActivity.this,getResources().getString(R.string.openportsuccess),Toast.LENGTH_LONG).show();;
                    isConnect = true;
                    bt_connect.setVisibility(View.VISIBLE);
                    tv_tips.setVisibility(View.GONE);
                    bt_connect.setText(getResources().getString(R.string.at_disconnect));
                    setButtonEnable(true);
                    break;
                case 2:
                    Toast.makeText(MainActivity.this,getResources().getString(R.string.openportfail)+"nReturn = "+nReturn,Toast.LENGTH_LONG).show();
                    break;
                case 3:
                    adtDevices.notifyDataSetChanged(); //Refresh the list
                    break;
                case 4:
                    Toast.makeText(MainActivity.this,getResources().getString(R.string.DeviceNotFound),Toast.LENGTH_LONG).show();
                    break;
                case 5:
                    if (nReturn == 0) {
                        Toast.makeText(MainActivity.this,getResources().getString(R.string.success)+getResources().getString(R.string.status)+status,Toast.LENGTH_LONG).show();;
                    }else {
                        Toast.makeText(MainActivity.this,getResources().getString(R.string.fail)+getResources().getString(R.string.status)+status,Toast.LENGTH_LONG).show();
                    }
                    break;



            }
        };
    };

    class ItemClickEvent implements AdapterView.OnItemClickListener {

        public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {

            String str = lstDevices.get(arg2);
            String modelName = printName[sp_printname.getSelectedItemPosition()];
            printTest.ClosePrinter();
            //usb
            if (rb_usb.isChecked()) {

                //打开端口
                nReturn = printTest.OpenPrinter(modelName,str);
                if (nReturn == 0) {
                    Toast.makeText(MainActivity.this,getResources().getString(R.string.openportsuccess),Toast.LENGTH_LONG).show();;
                    isConnect = true;
                    bt_connect.setVisibility(View.VISIBLE);
                    tv_tips.setVisibility(View.GONE);
                    bt_connect.setVisibility(View.VISIBLE);
                    bt_connect.setText(getResources().getString(R.string.at_disconnect));
                    setButtonEnable(true);
                }else {
                    Toast.makeText(MainActivity.this,getResources().getString(R.string.openportfail)+"nReturn = "+nReturn,Toast.LENGTH_LONG).show();;
                }
            }


            //网口
            if (rb_net.isChecked()) {
                String[] ipAdd = str.split("\\|");
                if (ipAdd.length == 2) {
                    str = ipAdd[1];
                }
                nReturn = printTest.OpenPrinter(modelName,str);//打开端口
                if (nReturn == 0) {
                    Toast.makeText(MainActivity.this,getResources().getString(R.string.openportsuccess),Toast.LENGTH_LONG).show();;
                    isConnect = true;
                    bt_connect.setVisibility(View.VISIBLE);
                    tv_tips.setVisibility(View.GONE);
                    bt_connect.setText(getResources().getString(R.string.at_disconnect));
                    setButtonEnable(true);
                }else {
                    Toast.makeText(MainActivity.this,getResources().getString(R.string.openportfail)+"nReturn = "+nReturn,Toast.LENGTH_LONG).show();;
                }
            }

            //蓝牙
            if (rb_bluetooth.isChecked()) {
                String[] macAdd = str.split("\\|");
                if (macAdd.length == 2) {
                    str = macAdd[1];
                }
                nReturn = printTest.OpenPrinter(modelName,str);//打开端口
                if (nReturn == 0) {
                    Toast.makeText(MainActivity.this,getResources().getString(R.string.openportsuccess),Toast.LENGTH_LONG).show();;
                    isConnect = true;
                    bt_connect.setVisibility(View.VISIBLE);
                    tv_tips.setVisibility(View.GONE);
                    bt_connect.setVisibility(View.VISIBLE);
                    bt_connect.setText(getResources().getString(R.string.at_disconnect));
                    setButtonEnable(true);
                }else {
                    Toast.makeText(MainActivity.this,getResources().getString(R.string.openportfail)+"nReturn = "+nReturn,Toast.LENGTH_LONG).show();;
                }
            }
        }
    }
    private void setPermissions() {
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,PERMISSION,1);
        }else{
            Log.i("TAG","setPermissions ok");
        }
    }
}