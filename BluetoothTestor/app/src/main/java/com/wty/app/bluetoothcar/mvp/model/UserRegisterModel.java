package com.wty.app.bluetoothcar.mvp.model;

import android.content.Context;

import com.kw.app.ormlib.OrmModuleManager;
import com.wty.app.bluetoothcar.base.AppConstant;
import com.wty.app.bluetoothcar.data.UserDALEx;
import com.wty.app.bluetoothcar.mvp.contract.IUserRegisterContract;
import com.wty.lib.widget.utils.ICallBack;

/**
 * @author wty
 */
public class UserRegisterModel implements IUserRegisterContract.IUserRegisterModel {

    @Override
    public void register(final Context context, final UserDALEx user, final ICallBack<String> callBack) {

        OrmModuleManager.getInstance().setCurrentDBName(AppConstant.DBName.Common_DB);

        //已经存在该用户
        if(UserDALEx.get().isExist(user.getUserid())){
            callBack.onFaild("已经存在该用户");
        }else {
            //把用户存储到公共库
            user.saveOrUpdate();
            callBack.onSuccess(user.getUserid());
        }
    }
}
