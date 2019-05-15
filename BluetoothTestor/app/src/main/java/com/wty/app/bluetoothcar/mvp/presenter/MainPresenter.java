package com.wty.app.bluetoothcar.mvp.presenter;

import com.kw.app.commonlib.utils.AppLogUtil;
import com.wty.app.bluetoothcar.data.BloodSugarDALEx;
import com.wty.app.bluetoothcar.mvp.contract.IMainContract;
import com.wty.app.bluetoothcar.mvp.model.MainModel;
import com.wty.lib.widget.mvp.presenter.BasePresenter;
import com.wty.lib.widget.utils.ICallBack;
import com.wty.lib.widget.utils.OnDismissCallbackListener;

import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * @author wty
 */
public class MainPresenter extends BasePresenter<IMainContract.IMainView> {

    private IMainContract.IMainModel mMainModel;

    public MainPresenter(){
        mMainModel = new MainModel();
    }

    /**
     * 刷新数据
     **/
    public void refreshData(){
        mView.showLoading("获取数据中...");
        mMainModel.refresh(new ICallBack<List<BloodSugarDALEx>>() {
            @Override
            public void onSuccess(List<BloodSugarDALEx> data) {
                mView.refreshChart(data);
                mView.dismissLoading(null);
            }

            @Override
            public void onFaild(String msg) {
                AppLogUtil.d(msg);
                mView.dismissLoading(new OnDismissCallbackListener(msg, SweetAlertDialog.ERROR_TYPE));
            }
        });
    }
}
