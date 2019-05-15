package com.wty.app.bluetoothcar.mvp.presenter;

import android.content.Context;

import com.wty.app.bluetoothcar.data.UserDALEx;
import com.wty.app.bluetoothcar.mvp.contract.IUserRegisterContract;
import com.wty.app.bluetoothcar.mvp.model.UserRegisterModel;
import com.wty.lib.widget.mvp.presenter.BasePresenter;
import com.wty.lib.widget.utils.ICallBack;
import com.wty.lib.widget.utils.OnDismissCallbackListener;

import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * 注册presenter
 * @author wty
 */
public class UserRegisterPresenter extends BasePresenter<IUserRegisterContract.IUserRegisterView> {

    private IUserRegisterContract.IUserRegisterModel mUserRegisterModel;

    public UserRegisterPresenter(){
        mUserRegisterModel = new UserRegisterModel();
    }

    public void register(Context context, final UserDALEx data){
        mView.showLoading("请稍候，正在注册中...");

        mUserRegisterModel.register(context,data, new ICallBack<String>() {
            @Override
            public void onSuccess(final String userid) {
                mView.dismissLoading(new OnDismissCallbackListener("注册成功") {
                    @Override
                    public void onCallback() {
                        mView.finishActivity(userid);
                    }
                });
            }

            @Override
            public void onFaild(String msg) {
                mView.dismissLoading(new OnDismissCallbackListener(msg, SweetAlertDialog.ERROR_TYPE));
            }
        });
    }

}
