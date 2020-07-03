package com.kaadas.lock.activity.device.wifilock.newadd;

import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.kaadas.lock.MyApplication;
import com.kaadas.lock.R;
import com.kaadas.lock.activity.choosecountry.CountrySortModel;
import com.kaadas.lock.activity.choosecountry.SideBar;
import com.kaadas.lock.activity.choosewifi.WiFiListAdapter;
import com.kaadas.lock.activity.choosewifi.WifiBean;
import com.kaadas.lock.activity.device.wifilock.add.WifiLockHelpActivity;
import com.kaadas.lock.mvp.mvpbase.BaseAddToApplicationActivity;
import com.kaadas.lock.utils.KeyConstants;
import com.kaadas.lock.utils.LogUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import android.content.Context;
import android.widget.Toast;


public class WifiLockScanWifiListActivity extends BaseAddToApplicationActivity {

    @BindView(R.id.back)
    ImageView back;
    @BindView(R.id.help)
    ImageView help;
    @BindView(R.id.lv_wifi_list)
    ListView lv_wifi_list;

    private String wifiName;  //wifi名字
    private String macAddress;  //mac地址
    private int rssi;  //rssi的值
    private String capability;  //capabilities的值
    private int imageid;  //capabilities值对应图片id
    private String apName;  //AP名字
    private WifiManager wifiManager;  //WiFi管理
    ArrayList<ScanResult> list;    //存放周围wifi热点对象的列表
    List<WifiBean> wifiBeanList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi_list_choose);
        ButterKnife.bind(this);

        getInfo();
        setListener();
    }
    /**
     * 获取WiFi名称、强度、Mac地址、AP地址
     */
    private void getInfo(){
        wifiManager = (WifiManager) MyApplication.getInstance().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = wifiManager.getConnectionInfo();
        wifiManager.startScan();  //开始扫描AP
        list = (ArrayList<ScanResult>) wifiManager.getScanResults();
        ListView listView = (ListView) findViewById(R.id.lv_wifi_list);  //获得界面的列表
        wifiBeanList = new ArrayList<>();
        if (list == null) {
            Toast.makeText(this, "当前周围无WiFi", Toast.LENGTH_LONG).show();
        }else {
            for(int i=0;i<list.size();i++) {
                wifiName = list.get(i).SSID;
                rssi = list.get(i).level;
                macAddress = info.getMacAddress();
                capability = list.get(i).capabilities;
//                String name = "WiFi名称："+wifiName+"   RSSI："+rssi+"";
//                String bottom = "MAC地址："+list.get(i).BSSID+"";
                String name = wifiName;
                if (capability.contains("WEP")) {
                    imageid = R.mipmap.wifi_encryption;
                } else if (capability.contains("PSK")) {
                    imageid = R.mipmap.wifi_encryption;
                } else if (capability.contains("EAP")) {
                    imageid = R.mipmap.wifi_encryption;
                } else {
                    //不加密
                    imageid = R.mipmap.wifi_unencryption;
                }
                if (!TextUtils.isEmpty(name)) {
                    wifiBeanList.add(new WifiBean(imageid, name));
//                LogUtils.e("--kaadas--name=="+ name);
                }
            }
            listView.setAdapter(new WiFiListAdapter(this,wifiBeanList));
        }
    }
    @OnClick({R.id.back, R.id.help})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.help:
                startActivity(new Intent(this, WifiLockHelpActivity.class));
                break;
        }
    }
    /****
     * 添加监听
     */
    private void setListener() {

        lv_wifi_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long arg3) {

                // 点击后返回
                String wifiName = wifiBeanList.get(position).name;
//                LogUtils.e("--kaadas--name=="+ name);
                Intent intent = new Intent();
                intent.putExtra(KeyConstants.CHOOSE_WIFI_NAME, wifiName);
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }
}
