package com.wty.app.bluetoothcar;

import android.Manifest;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kw.app.commonlib.utils.CommonUtil;
import com.kw.app.commonlib.utils.PreferenceUtil;
import com.kw.app.ormlib.OrmModuleManager;
import com.kw.app.widget.activity.BaseActivity;
import com.tbruyelle.rxpermissions.RxPermissions;
import com.wty.app.bluetoothcar.base.AppConstant;
import com.wty.app.bluetoothcar.base.MyApplication;
import com.wty.app.bluetoothcar.data.UserDALEx;
import com.wty.app.bluetoothcar.mvp.contract.IUserLoginContract;
import com.wty.app.bluetoothcar.mvp.presenter.UserLoginPresenter;
import com.wty.app.bluetoothcar.view.LoginInputView;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import rx.functions.Action1;

/**
 * @author wty
 * @Description 注册/登陆界面
 **/
public class LoginActivity extends BaseActivity<UserLoginPresenter> implements IUserLoginContract.IUserLoginView{

    @BindView(R.id.login_icon)
    ImageView mloginIcon;
    @BindView(R.id.login_inputview)
    LoginInputView mloginInputview;
    @BindView(R.id.login_version)
    TextView tv_version;
    @BindView(R.id.rl_content)
    RelativeLayout contentlayout;

    @OnClick(R.id.login_signup)
    void goToRegisterActivity(){
//        UserRegisterActivity.startUserRegisterActivity(this, AppConstant.ActivityResult.Request_Register);
    }

    private String userid;

    @Override
    public UserLoginPresenter getPresenter() {
        return new UserLoginPresenter();
    }

    @Override
    public void onInitView(Bundle savedInstanceState) {
        getDefaultNavigation().setTitle("");
        getDefaultNavigation().getRootView().setBackgroundColor(Color.TRANSPARENT);
        getDefaultNavigation().getLeftButton().hide();
        CommonUtil.keyboardControl(LoginActivity.this, false, mloginInputview.getAccountInput());

        final boolean isAutoLogin = PreferenceUtil.getInstance().isAutoLogin();
        String name = PreferenceUtil.getInstance().getLastName();
        String psw = PreferenceUtil.getInstance().getLastPassword();
        String userid = PreferenceUtil.getInstance().getLastAccount();

        if(PreferenceUtil.getInstance().isFirstLogin() && Build.VERSION.SDK_INT == Build.VERSION_CODES.M){
            //如果是第一次安装应用程序 适配android6.0动态权限
            RxPermissions.getInstance(this)
                    .request(Manifest.permission.READ_PHONE_STATE,
                            Manifest.permission.WRITE_CONTACTS,
                            Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.RECORD_AUDIO)
                    .subscribe(new Action1<Boolean>() {
                        @Override
                        public void call(Boolean aBoolean) {
                            if(aBoolean){
                                // All requested permissions are granted
                            }else{
                                // At least one permission is denied
                            }
                        }
                    });
            PreferenceUtil.getInstance().writePreferences(PreferenceUtil.IsFirstLogin,false);
        }

        //点击登陆后做的事情
        mloginInputview.setOnLoginAction(new LoginInputView.OnLoginActionListener() {
            @Override
            public void onLogin() {
                CommonUtil.keyboardControl(LoginActivity.this, false, mloginInputview.getAccountInput());
                if (submit()) {
                    mPresenter.login(MyApplication.getInstance(),mloginInputview.getAccount().toString(), mloginInputview.getPassword().toString(), mloginInputview.isRememberPsw());
                }
            }
        });

        tv_version.setText("V"+ CommonUtil.getVersion(this)+"."+CommonUtil.getVersionCode(this));

        if (name != null) {
            mloginInputview.setAccount(name);
            mloginInputview.setPassword(psw);
            if (!TextUtils.isEmpty(psw)) {
                mloginInputview.setIsRememberPsw(true);
            } else {
                mloginInputview.setIsRememberPsw(false);
            }
        } else {// 第一次使用，默认不记住密码
            mloginInputview.setIsRememberPsw(false);
        }

        if(isAutoLogin){//自动登录就调整到主页面
            UserDALEx user = new UserDALEx();
            user.setUserid(userid);
            user.setNickname(name);

            contentlayout.setVisibility(View.GONE);
//            mPresenter.loginAuto(LoginActivity.this,user);
        }else{
            contentlayout.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected List<String> validate() {
        List<String> list = super.validate();
        if(!TextUtils.isEmpty(mloginInputview.validata()))
            list.add(mloginInputview.validata());
        return list;
    }

    @Override
    public int getLayoutResource() {
        return R.layout.activity_login;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == AppConstant.ActivityResult.Request_Register) {
//                userid = data.getStringExtra(UserRegisterActivity.USERID);
//                UserDALEx user = UserDALEx.get().findById(userid);
//                mloginInputview.getAccountInput().setText(user.getNickname());
            }
        }
    }

    @Override
    public void finishActivity() {
        //登录成功之后设置一下当前数据库名字
        OrmModuleManager.getInstance().setCurrentDBName(PreferenceUtil.getInstance().getLastAccount());
        new Handler().postDelayed(new Runnable(){

            public void run() {
//                MainActivity.startMainActivity(LoginActivity.this);
                finish();
            }

        }, 500);

    }

    @Override
    public void showNoNet() {
        contentlayout.setVisibility(View.VISIBLE);
        super.showNoNet();
    }

    @Override
    public void showFailed(String msg) {
        contentlayout.setVisibility(View.VISIBLE);
        super.showFailed(msg);
    }
}
