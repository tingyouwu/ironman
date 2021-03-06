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
 * 描述：窗户控制
 */
public class WindowFragment extends Fragment implements View.OnClickListener {
    private View rootView;
    ImageView off_1,off_2;
    ImageView on_1,on_2;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_window, container, false);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView();
    }

    private void initView() {
        off_1 = rootView.findViewById(R.id.off_1);
        on_1 = rootView.findViewById(R.id.on_1);
        off_1.setOnClickListener(this);
        on_1.setOnClickListener(this);

        off_2 = rootView.findViewById(R.id.off_2);
        on_2 = rootView.findViewById(R.id.on_2);
        off_2.setOnClickListener(this);
        on_2.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.off_1:
                /*** 灯总开关：关闭*/
                off_1.setVisibility(View.INVISIBLE);
                on_1.setVisibility(View.VISIBLE);
                sendEvent(1,LightCode.Switch_Off,"+IPD,0,2:40");
                break;
            case R.id.on_1:
                /*** 灯总开关：开启*/
                off_1.setVisibility(View.VISIBLE);
                on_1.setVisibility(View.INVISIBLE);
                sendEvent(1,LightCode.Switch_On,"+IPD,0,2:41");
                break;
            case R.id.off_2:
                /*** 灯总开关：关闭*/
                off_2.setVisibility(View.INVISIBLE);
                on_2.setVisibility(View.VISIBLE);
                sendEvent(2,LightCode.Switch_Off,"+IPD,0,2:50");
                break;
            case R.id.on_2:
                /*** 灯总开关：开启*/
                off_2.setVisibility(View.VISIBLE);
                on_2.setVisibility(View.INVISIBLE);
                sendEvent(2,LightCode.Switch_On,"+IPD,0,2:51");
                break;
        }
    }

    private void sendEvent(int client,int state,String code){
        WifiEvent event = new WifiEvent(LightCode.Type_Window);
        event.appendHashParam(LightCode.Client,client);
        event.appendHashParam(LightCode.Data,state);
        event.appendHashParam(LightCode.Code,code);
        EventBus.getDefault().post(event);
    }
}
