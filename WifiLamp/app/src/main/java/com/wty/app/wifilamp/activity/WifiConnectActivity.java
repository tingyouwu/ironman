package com.wty.app.wifilamp.activity;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.tbruyelle.rxpermissions.RxPermissions;
import com.wty.app.wifilamp.R;
import com.wty.app.wifilamp.adapter.WifiListAdapter;
import com.wty.app.wifilamp.util.GpsUtil;
import com.wty.app.wifilamp.wifi.ControlLight;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.functions.Action1;

import static android.os.Build.VERSION_CODES.M;

/**
 * 描述：Wifi连接页面
 */
public class WifiConnectActivity extends BaseActivity {

    private static final int PRIVATE_CODE = 1315;//开启GPS权限

    @BindView(R.id.main_setting) ImageView main_setting;
    @BindView(R.id.wifi_connect_icon) ImageView wifiConnectIcon;
    @BindView(R.id.wifi_connect_state) ImageView wifiState;
    @BindView(R.id.wifi_state_ll) LinearLayout wifiStateLL;
    @BindView(R.id.wifi_state_tv_1) TextView wifistate;
    @BindView(R.id.button_enter) Button enter;
    @BindView(R.id.wifi_list) ListView listView;

    private WifiListAdapter adapter;
    private List<ScanResult> list = new ArrayList<>();
    private Animation operatingAnim;
    private boolean isConnecting;
    WifiManager wifiManager;

    private BroadcastReceiver receiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)) {
                List<ScanResult> result = wifiManager.getScanResults();
                if(result == null || result.size()==0){
                    if (Build.VERSION.SDK_INT >= M) {
                        Toast.makeText(WifiConnectActivity.this, "未搜索到wifi信号！请确定是否打开GPS", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(WifiConnectActivity.this, "未搜索到wifi信号！", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Map<String,ScanResult> wifimap = new LinkedHashMap<>();
                    for(ScanResult wifi:result){
                        if(wifimap.containsKey(wifi.SSID))continue;
                        wifimap.put(wifi.SSID,wifi);
                    }
                    List<ScanResult> list = new ArrayList<>();
                    list.addAll(wifimap.values());

                    WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
                    WifiInfo wifiInfo = wifiManager.getConnectionInfo();
                    adapter.setCurrentWifiSSID(wifiInfo.getSSID());
                    adapter.refreshList(list);
                }
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi_state);
        ButterKnife.bind(this);
        initView();

        if (Build.VERSION.SDK_INT >= M) {
            RxPermissions.getInstance(WifiConnectActivity.this)
                    .request(Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.ACCESS_FINE_LOCATION)
                    .subscribe(new Action1<Boolean>() {
                        @Override
                        public void call(Boolean aBoolean) {
                            if (aBoolean) {
                                initWifi();
                            }
                        }
                    });
        } else {
            initWifi();
        }

    }

    private void initWifi() {
        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if (!wifiManager.isWifiEnabled()) {
            Toast.makeText(getApplicationContext(), "wifi未打开！正在打开...", Toast.LENGTH_SHORT).show();
            wifiManager.setWifiEnabled(true);
        }

        adapter = new WifiListAdapter(this,list);
        listView.setAdapter(adapter);
        IntentFilter filter = new IntentFilter();
        filter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        registerReceiver(receiver, filter);
    }

    private void initView() {

        operatingAnim = AnimationUtils.loadAnimation(this, R.anim.rotate);
        operatingAnim.setInterpolator(new LinearInterpolator());

        enter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                if(!ControlLight.newInstance().isConnected()){
//                    Toast.makeText(WifiConnectActivity.this,getResources().getText(R.string.no_wifi_fail),Toast.LENGTH_LONG).show();
//                    return;
//                }

                Intent intent = new Intent(WifiConnectActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        main_setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(WifiConnectActivity.this, SettingActivity.class);
                startActivity(intent);
            }
        });

        wifiState.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                connectWiFiLamp();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case PRIVATE_CODE:
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(wifiManager != null){
            checkGps();
            wifiManager.startScan();
        }

        /**
         * 在onPause()中判断wifi连接状态
         *暂时不做wifi状态监听（wifi确认链接后返回页面，或者重启app，才会生效）
         */
        connectWiFiLamp();
    }


    private void checkGps(){

        if(!GpsUtil.isOPen(this)){
            //需要提示用户去开启定位服务
            new AlertDialog.Builder(this)
                    .setTitle("是否打开GPS")
                    .setMessage("扫描WIFI信息，需要打开GPS开关")
                    // 拒绝, 退出应用
                    .setNegativeButton("取消",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    finish();
                                }
                            })

                    .setPositiveButton("确定",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    //跳转GPS设置界面
                                    Intent intent = new Intent();
                                    intent.setAction(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                    startActivityForResult(intent, PRIVATE_CODE);
                                }
                            })

                    .setCancelable(false)
                    .show();
        }
    }

    /**
     * 连接wifi灯
     **/
    private void connectWiFiLamp(){

        if(isConnecting)return;

        if(ControlLight.newInstance().isConnected()){
            //如果已经连接成功了
            connectSucces();
            return;
        }

        ControlLight.setOnConnectStateListener(new ControlLight.ConnectStateListener() {

            @Override
            public void startConnect() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        connectStart();
                    }
                });
            }

            @Override
            public void connectSuccess() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        connectSucces();
                    }
                });
            }

            @Override
            public void connectFailed() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        connectFail();
                    }
                });
            }
        });

        ControlLight.connectLight();
    }

    /**
     * 连接开始
     **/
    private void connectStart(){
        isConnecting = true;
        wifiState.setImageResource(R.mipmap.wifi_list_icon);
        wifiConnectIcon.startAnimation(operatingAnim);
        wifistate.setText(getResources().getText(R.string.connectting));
    }

    /**
     * 连接成功
     */
    public void connectSucces() {
        isConnecting = false;
        wifiState.setImageResource(R.mipmap.wifi_connect_y);
        wifiConnectIcon.clearAnimation();
        wifistate.setText(getResources().getText(R.string.connectsuccess));
    }

    /**
     *连接失败
     */
    public void connectFail() {
        isConnecting = false;
        wifiState.setImageResource(R.mipmap.wifi_connect_n);
        wifiConnectIcon.clearAnimation();
        wifistate.setText(getResources().getText(R.string.connectfail));
        Toast.makeText(this,getResources().getText(R.string.no_wifi_fail),Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(receiver != null){
            unregisterReceiver(receiver);
        }
    }
}
