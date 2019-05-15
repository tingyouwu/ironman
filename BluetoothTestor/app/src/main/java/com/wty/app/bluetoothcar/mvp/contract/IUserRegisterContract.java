package com.wty.app.bluetoothcar.mvp.contract;

import android.content.Context;

import com.wty.app.bluetoothcar.data.UserDALEx;
import com.wty.lib.widget.mvp.model.IBaseModel;
import com.wty.lib.widget.mvp.view.IBaseView;
import com.wty.lib.widget.utils.ICallBack;
import com.wty.lib.widget.utils.OnDismissCallbackListener;

/**
 * @author wty
 */
public interface IUserRegisterContract {

    interface IUserRegisterModel extends IBaseModel {
        void register(Context context, UserDALEx user, ICallBack<String> callBack);
    }

    interface IUserRegisterView extends IBaseView {
        void showLoading(String loadmsg);
        void dismissLoading(OnDismissCallbackListener callback);
        void finishActivity(String userid);
    }
}
