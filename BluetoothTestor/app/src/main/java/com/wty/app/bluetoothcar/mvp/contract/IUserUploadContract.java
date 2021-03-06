package com.wty.app.bluetoothcar.mvp.contract;

import com.wty.app.bluetoothcar.data.BloodSugarDALEx;
import com.wty.lib.widget.mvp.model.IBaseModel;
import com.wty.lib.widget.mvp.view.IBaseView;
import com.wty.lib.widget.utils.ICallBack;
import com.wty.lib.widget.utils.OnDismissCallbackListener;

/**
 * @author wty
 */
public interface IUserUploadContract {

    interface IUserUploadModel extends IBaseModel {
        void uploadData(BloodSugarDALEx dalEx,ICallBack<String> callBack);
    }

    interface IUserUploadView extends IBaseView {
        void showLoading(String loadmsg);
        void dismissLoading(OnDismissCallbackListener callback);
    }
}
