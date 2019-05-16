package com.wty.app.bluetoothcar.mvp.presenter;

import android.content.Context;

import com.wty.app.bluetoothcar.data.BloodSugarDALEx;
import com.wty.app.bluetoothcar.data.UserDALEx;
import com.wty.app.bluetoothcar.mvp.contract.IUserManualUploadContract;
import com.wty.app.bluetoothcar.mvp.contract.IUserRegisterContract;
import com.wty.app.bluetoothcar.mvp.model.UserManualUploadModel;
import com.wty.app.bluetoothcar.mvp.model.UserRegisterModel;
import com.wty.lib.widget.mvp.presenter.BasePresenter;
import com.wty.lib.widget.utils.ICallBack;
import com.wty.lib.widget.utils.OnDismissCallbackListener;

import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * 人工采集presenter
 * @author wty
 */
public class UserManualUploadPresenter extends BasePresenter<IUserManualUploadContract.IUserManualUploadView> {

    private IUserManualUploadContract.IUserManualUploadModel mUserManualUploadModel;

    public UserManualUploadPresenter(){
        mUserManualUploadModel = new UserManualUploadModel();
    }

    public void uploadData(Context context, final BloodSugarDALEx data){
        mView.showLoading("请稍候，正在提交中...");

        mUserManualUploadModel.uploadData(data, new ICallBack<String>() {
            @Override
            public void onSuccess(final String msg) {
                mView.dismissLoading(new OnDismissCallbackListener("提交成功") {
                    @Override
                    public void onCallback() {
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
