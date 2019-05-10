package com.wty.app.wifilamp.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.wty.app.wifilamp.R;
import com.wty.app.wifilamp.eventbus.WifiEvent;
import com.wty.app.wifilamp.wifi.LightCode;

import org.greenrobot.eventbus.EventBus;

/**
 * 描述：空调控制
 */
public class AirFragment extends Fragment implements View.OnClickListener {
    private View rootView;
    ImageView off;
    ImageView on;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_air, container, false);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView();
    }

    private void initView() {
        off = rootView.findViewById(R.id.off);
        on = rootView.findViewById(R.id.on);
        off.setOnClickListener(this);
        on.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.off:
                /*** 灯总开关：关闭*/
                off.setVisibility(View.INVISIBLE);
                on.setVisibility(View.VISIBLE);
                sendEvent(LightCode.Switch_Off);
                break;
            case R.id.on:
                /*** 灯总开关：开启*/
                off.setVisibility(View.VISIBLE);
                on.setVisibility(View.INVISIBLE);
                sendEvent(LightCode.Switch_On);
                break;
        }
    }

    private void sendEvent(int state){
        WifiEvent event = new WifiEvent(LightCode.Type_Air);
        event.appendHashParam(LightCode.Client,0);
        event.appendHashParam(LightCode.Data,state);
        EventBus.getDefault().post(event);
    }

}
