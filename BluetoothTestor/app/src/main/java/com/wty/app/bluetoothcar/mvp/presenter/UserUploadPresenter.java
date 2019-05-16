package com.wty.app.bluetoothcar.mvp.presenter;

import com.wty.app.bluetoothcar.data.BloodSugarDALEx;
import com.wty.app.bluetoothcar.mvp.contract.IUserUploadContract;
import com.wty.app.bluetoothcar.mvp.model.UserUploadModel;
import com.wty.lib.widget.mvp.presenter.BasePresenter;
import com.wty.lib.widget.utils.ICallBack;
import com.wty.lib.widget.utils.OnDismissCallbackListener;

import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * 蓝牙上传presenter
 * @author wty
 */
public class UserUploadPresenter extends BasePresenter<IUserUploadContract.IUserUploadView> {

    private IUserUploadContract.IUserUploadModel mUserUploadModel;

    public UserUploadPresenter(){
        mUserUploadModel = new UserUploadModel();
    }

    public void uploadData(BloodSugarDALEx dalEx){
        mView.showLoading("请稍候，正在提交数据....");
        mUserUploadModel.uploadData(dalEx, new ICallBack<String>() {
            @Override
            public void onSuccess(String data) {
                mView.dismissLoading(new OnDismissCallbackListener("注册成功"));
            }

            @Override
            public void onFaild(String msg) {
                mView.dismissLoading(new OnDismissCallbackListener(msg, SweetAlertDialog.ERROR_TYPE));
            }
        });
    }
}
