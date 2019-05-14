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
public interface IUserLoginContract {

    interface IUserLoginModel extends IBaseModel {
        void login(Context context, String name, String psw, boolean isAutoLogin, ICallBack<UserDALEx> callBack);
    }

    interface IUserLoginView extends IBaseView {
        void showLoading(String loadmsg);
        void dismissLoading(OnDismissCallbackListener callback);
        void finishActivity();
        void showFailed(String msg);
    }

}
