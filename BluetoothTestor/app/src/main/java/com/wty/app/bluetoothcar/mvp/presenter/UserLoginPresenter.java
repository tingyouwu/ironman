package com.wty.app.bluetoothcar.mvp.presenter;

import android.content.Context;

import com.kw.app.commonlib.utils.AppLogUtil;
import com.wty.app.bluetoothcar.data.UserDALEx;
import com.wty.app.bluetoothcar.mvp.contract.IUserLoginContract;
import com.wty.app.bluetoothcar.mvp.model.UserLoginModel;
import com.wty.lib.widget.mvp.presenter.BasePresenter;
import com.wty.lib.widget.utils.ICallBack;
import com.wty.lib.widget.utils.OnDismissCallbackListener;

import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * @author wty
 */
public class UserLoginPresenter extends BasePresenter<IUserLoginContract.IUserLoginView> {

    private IUserLoginContract.IUserLoginModel mUserLoginModel;

    public UserLoginPresenter(){
        mUserLoginModel = new UserLoginModel();
    }

    /**
     * 登录验证过程：
     * 1.先去bmob服务器验证一下账号和密码
     * 2.再通过账号和密码 去融云获取token
     * 3.通过token连接上融云服务器
     **/
    public void login(final Context context, final String name, final String psw, final boolean isAutoLogin){

        mView.showLoading("正在验证用户名...");
        mUserLoginModel.login(context,name, psw, isAutoLogin,new ICallBack<UserDALEx>() {
            @Override
            public void onSuccess(UserDALEx user) {
                AppLogUtil.d("验证账号密码成功");
            }

            @Override
            public void onFaild(String msg) {
                AppLogUtil.d(msg);
                mView.dismissLoading(new OnDismissCallbackListener(msg, SweetAlertDialog.ERROR_TYPE));
            }
        });
    }

}
