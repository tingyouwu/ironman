package com.wty.app.bluetoothcar.mvp.contract;

import android.content.Context;

import com.kw.app.commonlib.mvp.model.IBaseModel;
import com.kw.app.commonlib.mvp.view.IBaseView;
import com.kw.app.widget.ICallBack;
import com.kw.app.widget.view.sweetdialog.OnDismissCallbackListener;
import com.wty.app.bluetoothcar.data.UserDALEx;

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
        boolean checkNet();
        void showNoNet();
        void finishActivity();
        void showFailed(String msg);
    }

}
