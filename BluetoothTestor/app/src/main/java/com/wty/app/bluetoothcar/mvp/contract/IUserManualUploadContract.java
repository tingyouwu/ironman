package com.wty.app.bluetoothcar.mvp.contract;

import com.wty.app.bluetoothcar.data.BloodSugarDALEx;
import com.wty.lib.widget.mvp.model.IBaseModel;
import com.wty.lib.widget.mvp.view.IBaseView;
import com.wty.lib.widget.utils.ICallBack;
import com.wty.lib.widget.utils.OnDismissCallbackListener;

/**
 * 人工收集协议
 * @author wty
 */
public interface IUserManualUploadContract {

    interface IUserManualUploadModel extends IBaseModel {
        void uploadData(BloodSugarDALEx dalEx, ICallBack<String> callBack);
    }

    interface IUserManualUploadView extends IBaseView {
        void showLoading(String loadmsg);
        void dismissLoading(OnDismissCallbackListener callback);
    }
}
