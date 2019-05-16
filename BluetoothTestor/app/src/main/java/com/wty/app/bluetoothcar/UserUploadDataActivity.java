package com.wty.app.bluetoothcar;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.kw.app.commonlib.utils.AppLogUtil;
import com.kw.app.commonlib.utils.NumberUtil;
import com.kw.app.commonlib.utils.TimeUtil;
import com.tbruyelle.rxpermissions.RxPermissions;
import com.wty.app.bluetoothcar.base.MyApplication;
import com.wty.app.bluetoothcar.bluetooth.BluetoothChatService;
import com.wty.app.bluetoothcar.bluetooth.DeviceListActivity;
import com.wty.app.bluetoothcar.data.BloodSugarDALEx;
import com.wty.app.bluetoothcar.mvp.contract.IUserUploadContract;
import com.wty.app.bluetoothcar.mvp.presenter.UserUploadPresenter;
import com.wty.lib.widget.activity.BaseActivity;
import com.wty.lib.widget.utils.OnDismissCallbackListener;
import com.wty.lib.widget.view.xrecyclerview.XRecyclerView;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.Bind;
import butterknife.OnClick;
import rx.functions.Action1;

import static cn.pedant.SweetAlert.SweetAlertDialog.NORMAL_TYPE;
import static com.wty.app.bluetoothcar.bluetooth.BluetoothChatService.DEVICE_NAME;
import static com.wty.app.bluetoothcar.bluetooth.BluetoothChatService.MESSAGE_DEVICE_NAME;
import static com.wty.app.bluetoothcar.bluetooth.BluetoothChatService.MESSAGE_READ;
import static com.wty.app.bluetoothcar.bluetooth.BluetoothChatService.MESSAGE_STATE_CHANGE;
import static com.wty.app.bluetoothcar.bluetooth.BluetoothChatService.MESSAGE_TOAST;
import static com.wty.app.bluetoothcar.bluetooth.BluetoothChatService.TOAST;

/**
 * @author wty
 * @Description 用户数据采集
 **/
public class UserUploadDataActivity extends BaseActivity<UserUploadPresenter> implements IUserUploadContract.IUserUploadView {

    private static final int REQUEST_CONNECT_DEVICE = 1;
    private static final int REQUEST_ENABLE_BT = 2;

    private BluetoothAdapter mBluetoothAdapter = null;
    private BluetoothChatService mChatService = null;
    private String mConnectedDeviceName = null;

    @Bind(R.id.tv_detail)
    TextView tvDetail;
    @Bind(R.id.listview)
    XRecyclerView listview;

    @OnClick(R.id.btn_connect)
    void sign(){
        Intent serverIntent = new Intent(UserUploadDataActivity.this, DeviceListActivity.class);
        startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE);
    }

    public static void startActivity(Activity activity) {
        Intent intent = new Intent(activity, UserUploadDataActivity.class);
        activity.startActivity(intent);
    }

    @Override
    public UserUploadPresenter getPresenter() {
        return new UserUploadPresenter();
    }

    @Override
    public void onInitView(Bundle savedInstanceState) {
        getDefaultNavigation().setTitle("采集数据");
        getDefaultNavigation().getRightButton().setButton("人工采集", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               UserManualUploadDataActivity.startActivity(UserUploadDataActivity.this);
            }
        });
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            Toast.makeText(this, "手机无蓝牙设备", Toast.LENGTH_SHORT).show();
        }else{
            if(!mBluetoothAdapter.isEnabled()){
                if (Build.VERSION.SDK_INT >= 23) {
                    RxPermissions.getInstance(UserUploadDataActivity.this)
                            .request(Manifest.permission.ACCESS_COARSE_LOCATION,
                                    Manifest.permission.ACCESS_FINE_LOCATION)
                            .subscribe(new Action1<Boolean>() {
                                @Override
                                public void call(Boolean aBoolean) {
                                    if (aBoolean) {
                                        Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                                        startActivityForResult(enableIntent,REQUEST_ENABLE_BT);
                                    }
                                }
                            });
                } else {
                    Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(enableIntent,REQUEST_ENABLE_BT);
                }
            }else{
                // Initialize the BluetoothChatService to perform bluetooth connections
                mChatService = new BluetoothChatService(this, mHandler);
            }
        }
    }

    @Override
    public int getLayoutResource() {
        return R.layout.activity_upload;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode){
            case REQUEST_CONNECT_DEVICE:
                if (resultCode == Activity.RESULT_OK) {
                    String address = data.getExtras()
                            .getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS);
                    BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
                    if(mChatService == null){
                        mChatService = new BluetoothChatService(this, mHandler);
                        mChatService.start();
                    }
                    mChatService.connect(device);
                }
                break;

            case REQUEST_ENABLE_BT:
                if (resultCode == Activity.RESULT_OK) {
                    Toast.makeText(this, R.string.bt_enabled_success, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, R.string.bt_not_enabled_leaving, Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mChatService != null) mChatService.stop();
        if(mBluetoothAdapter != null && mBluetoothAdapter.isEnabled()){
            mBluetoothAdapter.disable();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mChatService != null && mBluetoothAdapter != null && mBluetoothAdapter.isEnabled()) {
            // Only if the state is STATE_NONE, do we know that we haven't started already
            if (mChatService.getState() == BluetoothChatService.STATE_NONE) {
                mChatService.start();
            }
        }
    }

    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MESSAGE_STATE_CHANGE:
                    switch (msg.arg1) {
                        case BluetoothChatService.STATE_CONNECTED:
                            break;
                        case BluetoothChatService.STATE_CONNECTING:
                            Toast.makeText(MyApplication.getInstance(), "正在连接该蓝牙设备", Toast.LENGTH_SHORT).show();
                            break;
                        case BluetoothChatService.STATE_LISTEN:
                        case BluetoothChatService.STATE_NONE:
                            break;
                    }
                    break;
                case MESSAGE_READ:
                    byte[] readBuf = (byte[]) msg.obj;
                    // construct a string from the valid bytes in the buffer
                    String readMessage = new String(readBuf, 0, msg.arg1);
                    tvDetail.setText(readMessage);
                    try {
                        JSONObject jsonObject = new JSONObject(readMessage);
                        final String value = jsonObject.optString("value");
                        AppLogUtil.d(readMessage);
                        if(!TextUtils.isEmpty(value) && NumberUtil.isDoubleOrFloat(value)){
                            //收到有效数据
                            onToast(new OnDismissCallbackListener("确认接收该数据?",NORMAL_TYPE) {
                                @Override
                                public void onCallback() {
                                    BloodSugarDALEx dalEx = new BloodSugarDALEx();
                                    dalEx.setDate(TimeUtil.getFormatToday(TimeUtil.FORMAT_YEAR_MONTH_DAY));
                                    dalEx.setLevel(Float.valueOf(value));
                                    mPresenter.uploadData(dalEx);
                                }
                            });
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    Toast.makeText(MyApplication.getInstance(), readMessage,
                            Toast.LENGTH_LONG).show();
                    break;
                case MESSAGE_DEVICE_NAME:
                    // save the connected device's name
                    mConnectedDeviceName = msg.getData().getString(DEVICE_NAME);
                    Toast.makeText(MyApplication.getInstance(), "连接上 "
                            + mConnectedDeviceName, Toast.LENGTH_SHORT).show();
                    break;
                case MESSAGE_TOAST:
                    Toast.makeText(MyApplication.getInstance(), msg.getData().getString(TOAST),
                            Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    /**
     * Sends a message.
     * @param message A string of text to send.
     */
    private void sendMessage(String message) {
        if (mBluetoothAdapter == null) {
            Toast.makeText(this, "手机无蓝牙设备", Toast.LENGTH_SHORT).show();
            return;
        }

        // Check that we're actually connected before trying anything
        if (mChatService == null ||(mChatService.getState() != BluetoothChatService.STATE_CONNECTED)) {
            Toast.makeText(this, R.string.not_connected, Toast.LENGTH_SHORT).show();
            return;
        }
        // Check that there's actually something to send
        if (message.length() > 0) {
            byte[] send = message.getBytes();
            mChatService.write(send);
        }
    }
}
