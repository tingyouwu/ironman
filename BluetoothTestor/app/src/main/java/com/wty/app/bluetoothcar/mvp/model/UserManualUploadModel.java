package com.wty.app.bluetoothcar.mvp.model;

import com.wty.app.bluetoothcar.data.BloodSugarDALEx;
import com.wty.app.bluetoothcar.mvp.contract.IUserManualUploadContract;
import com.wty.lib.widget.utils.ICallBack;

/**
 * @author wty
 */
public class UserManualUploadModel implements IUserManualUploadContract.IUserManualUploadModel {

    @Override
    public void uploadData(BloodSugarDALEx dalEx, ICallBack<String> callBack) {
        boolean success = dalEx.saveOrUpdate();
        if(success){
            callBack.onSuccess("提交成功");
        }else {
            callBack.onFaild("提交失败");
        }
    }
}
