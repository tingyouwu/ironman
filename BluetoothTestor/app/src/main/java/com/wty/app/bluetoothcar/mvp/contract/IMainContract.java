package com.wty.app.bluetoothcar.mvp.contract;

import com.wty.app.bluetoothcar.data.BloodSugarDALEx;
import com.wty.lib.widget.mvp.model.IBaseModel;
import com.wty.lib.widget.mvp.view.IBaseView;
import com.wty.lib.widget.utils.ICallBack;
import com.wty.lib.widget.utils.OnDismissCallbackListener;

import java.util.List;

/**
 * 主页面协议
 * @author wty
 */
public interface IMainContract {

    interface IMainModel extends IBaseModel {
        void refresh(ICallBack<List<BloodSugarDALEx>> callBack);
    }

    interface IMainView extends IBaseView {
        void showLoading(String loadmsg);
        void dismissLoading(OnDismissCallbackListener callback);
        void refreshChart(List<BloodSugarDALEx> list);
    }
}
