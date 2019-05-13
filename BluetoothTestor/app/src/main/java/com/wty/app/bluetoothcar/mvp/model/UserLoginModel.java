package com.wty.app.bluetoothcar.mvp.model;

import android.content.Context;

import com.kw.app.commonlib.utils.PreferenceUtil;
import com.kw.app.widget.ICallBack;
import com.wty.app.bluetoothcar.data.UserDALEx;
import com.wty.app.bluetoothcar.mvp.contract.IUserLoginContract;

/**
 * @author wty
 */
public class UserLoginModel implements IUserLoginContract.IUserLoginModel {

    @Override
    public void login(final Context context, final String name, final String psw, final boolean isAutoLogin, final ICallBack<UserDALEx> callBack) {

//        CloudManager.getInstance().getUserManager().login(context, name, psw, new ICallBack<UserDALEx>() {
//            @Override
//            public void onSuccess(UserDALEx user) {
//                callBack.onSuccess(user);
//                saveUserPreference(isAutoLogin,psw, user);
//            }
//
//            @Override
//            public void onFaild(String msg) {
//                callBack.onFaild(msg);
//            }
//        });
    }

    /**
     * @Decription 保存用户数据到preference
     **/
    private void saveUserPreference(boolean isAutoLogin,String psw,UserDALEx user){
//        PreferenceUtil.getInstance().writePreferences(PreferenceUtil.LastName, user.getNickname());
//        PreferenceUtil.getInstance().writePreferences(PreferenceUtil.LastAccount, user.getUserid());
//        PreferenceUtil.getInstance().writePreferences(PreferenceUtil.LogoUrl, user.getLogourl());
//        if(isAutoLogin){
//            PreferenceUtil.getInstance().writePreferences(PreferenceUtil.IsAutoLogin, true);
//            PreferenceUtil.getInstance().writePreferences(PreferenceUtil.LastPassword,psw);
//        }else{
//            PreferenceUtil.getInstance().writePreferences(PreferenceUtil.IsAutoLogin, false);
//            PreferenceUtil.getInstance().writePreferences(PreferenceUtil.LastPassword, "");
//        }
    }

}
