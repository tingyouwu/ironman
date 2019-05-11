package com.wty.app.wifilamp.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.wty.app.wifilamp.R;
import com.wty.app.wifilamp.adapter.MyPagerAdapter;
import com.wty.app.wifilamp.eventbus.WifiEvent;
import com.wty.app.wifilamp.fragment.AirFragment;
import com.wty.app.wifilamp.fragment.LEDFragment;
import com.wty.app.wifilamp.fragment.WindowFragment;
import com.wty.app.wifilamp.widget.CustomViewpager;
import com.wty.app.wifilamp.wifi.ControlLight;
import com.wty.app.wifilamp.wifi.LightCode;

import net.lucode.hackware.magicindicator.MagicIndicator;
import net.lucode.hackware.magicindicator.ViewPagerHelper;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.CommonNavigatorAdapter;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerTitleView;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.indicators.LinePagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.ColorTransitionPagerTitleView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 描述：控制主页面
 **/
public class MainActivity extends BaseActivity implements View.OnClickListener{

    @BindView(R.id.back_wifi_list) ImageView backWifiList;
    @BindView(R.id.indicator) MagicIndicator magicIndicator;
    @BindView(R.id.viewpager) CustomViewpager viewPager;

    private Handler handler;
    private sendThread sendThread;
    private String sendMessage = "";
    private Toast toast = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        initView();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        super.onKeyDown(keyCode, event);
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                exitBy2Click();
                break;
            default:
                return super.onKeyDown(keyCode, event);
        }
        return false;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(WifiEvent event){
        switch (event.getType()){
            case LightCode.Type_Air:
            case LightCode.Type_Led:
            case LightCode.Type_Window:
                String code = (String)(event.getHashMap().get(LightCode.Code));
                sendSwitchMessage(code);
                break;
            default:
                break;
        }
    }

    private void initView(){
        handler = new Handler();
        if(sendThread == null){
            sendThread = new sendThread();
        }
        backWifiList.setOnClickListener(this);

        final List<String> tabNames = new ArrayList<>();
        final List<Fragment> fragments = new ArrayList<>();

        tabNames.add(getString(R.string.tab_air));
        tabNames.add(getString(R.string.tab_led));
        tabNames.add(getString(R.string.tab_window));

        fragments.add(new AirFragment());
        fragments.add(new LEDFragment());
        fragments.add(new WindowFragment());

        /**
         * 禁止viewpager 滑动
         */
        viewPager.setPagingEnabled(false);

        CommonNavigator commonNavigator = new CommonNavigator(this);
        commonNavigator.setAdjustMode(true);
        commonNavigator.setAdapter(new CommonNavigatorAdapter() {
            @Override
            public int getCount() {
                return tabNames.size();
            }

            @Override
            public IPagerTitleView getTitleView(Context context, final int index) {
                ColorTransitionPagerTitleView colorTransitionPagerTitleView = new ColorTransitionPagerTitleView(context);
                colorTransitionPagerTitleView.setNormalColor(context.getResources().getColor(R.color.tabUnSelected));
                colorTransitionPagerTitleView.setSelectedColor(context.getResources().getColor(R.color.white));
                colorTransitionPagerTitleView.setText(tabNames.get(index));
                colorTransitionPagerTitleView.setTextSize(18);
                colorTransitionPagerTitleView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        viewPager.setCurrentItem(index);
                    }
                });
                return colorTransitionPagerTitleView;
            }

            @Override
            public IPagerIndicator getIndicator(Context context) {
                LinePagerIndicator indicator = new LinePagerIndicator(context);
                indicator.setMode(LinePagerIndicator.MODE_WRAP_CONTENT);
                indicator.setColors(context.getResources().getColor(R.color.white));
                return indicator;
            }
        });

        magicIndicator.setNavigator(commonNavigator);

        MyPagerAdapter pagerAdapter = new MyPagerAdapter(getSupportFragmentManager(), tabNames, fragments);
        viewPager.setAdapter(pagerAdapter);

        ViewPagerHelper.bind(magicIndicator, viewPager);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                magicIndicator.onPageScrolled(position, positionOffset, positionOffsetPixels);
            }

            @Override
            public void onPageSelected(int position) {
                magicIndicator.onPageSelected(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                magicIndicator.onPageScrollStateChanged(state);
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back_wifi_list:
                /**
                 * 跳转到wifi列表
                 */
                Intent intent = new Intent(MainActivity.this, WifiConnectActivity.class);
                startActivity(intent);
                break;
        }
    }

    private class sendThread implements Runnable {
        @Override
        public void run() {
            sendMessage(sendMessage);
        }
    }

    /**
     * 发送数据
     * @param message A string of text to send.
     */
    private void sendMessage(String message) {
        Log.d("Message:",message);
        if(!ControlLight.newInstance().isConnected()){
            if(toast == null){
                toast = Toast.makeText(getApplicationContext(), getResources().getText(R.string.no_wifi_fail), Toast.LENGTH_SHORT);
            }else{
                toast.setText(getResources().getText(R.string.no_wifi_fail));
            }
            toast.show();
            return;
        }
        // Check that there's actually something to send
        if (message.length() > 0) {
            byte[] send = message.getBytes();
            ControlLight.newInstance().getTransceiver().send(send);
        }
    }

    /**
     * 开关数据
     **/
    private void sendSwitchMessage(int type,int client,int state){
        JSONObject jb = new JSONObject();
        try {
            jb.put(LightCode.Type,type);
            jb.put(LightCode.Client,client);
            jb.put(LightCode.Data,state);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        sendMessage = jb.toString();
        handler.removeCallbacks(sendThread);
        handler.postDelayed(sendThread, 20);
    }

    /**
     * 开关数据
     **/
    private void sendSwitchMessage(String code){
        sendMessage = code;
        handler.removeCallbacks(sendThread);
        handler.postDelayed(sendThread, 20);
    }

    /**
     * 双击退出函数
     */
    private static Boolean isExit = false;
    private void exitBy2Click() {
        Timer tExit;
        if (!isExit) {
            isExit = true;
            Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
            tExit = new Timer();
            tExit.schedule(new TimerTask() {
                @Override
                public void run() {
                    isExit = false; // 取消退出
                }
            }, 2000); // 如果2秒钟内没有按下返回键，则启动定时器取消掉刚才执行的任务

        } else {
            finish();
        }
    }
}
