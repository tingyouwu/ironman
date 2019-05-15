package com.wty.app.bluetoothcar.mvp.model;

import android.content.Context;

import com.kw.app.commonlib.utils.PreferenceUtil;
import com.kw.app.ormlib.OrmModuleManager;
import com.wty.app.bluetoothcar.base.AppConstant;
import com.wty.app.bluetoothcar.data.UserDALEx;
import com.wty.app.bluetoothcar.mvp.contract.IUserLoginContract;
import com.wty.lib.widget.utils.ICallBack;

/**
 * @author wty
 */
public class UserLoginModel implements IUserLoginContract.IUserLoginModel {

    @Override
    public void login(final Context context, final String name, final String psw, final boolean isAutoLogin, final ICallBack<String> callBack) {
        //切换数据库
        OrmModuleManager.getInstance().setCurrentDBName(AppConstant.DBName.Common_DB);
        UserDALEx user = UserDALEx.get().findById(name);
        if(user == null){
            callBack.onFaild("不存在该账号");
        }else if(!user.getPassword().equals(psw)){
            callBack.onFaild("账号密码不正确");
        }else {
            saveUserPreference(isAutoLogin,psw,name);
            callBack.onSuccess(name);
        }
    }

    /**
     * @Decription 保存用户数据到preference
     **/
    private void saveUserPreference(boolean isAutoLogin,String psw,String userid){
        PreferenceUtil.getInstance().writePreferences(PreferenceUtil.LastAccount, userid);
        if(isAutoLogin){
            PreferenceUtil.getInstance().writePreferences(PreferenceUtil.IsAutoLogin, true);
            PreferenceUtil.getInstance().writePreferences(PreferenceUtil.LastPassword,psw);
        }else{
            PreferenceUtil.getInstance().writePreferences(PreferenceUtil.IsAutoLogin, false);
            PreferenceUtil.getInstance().writePreferences(PreferenceUtil.LastPassword, "");
        }
    }

}
