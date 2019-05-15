package com.wty.app.bluetoothcar;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kw.app.commonlib.utils.AppLogUtil;
import com.kw.app.commonlib.utils.CommonUtil;
import com.kw.app.commonlib.utils.PreferenceUtil;
import com.kw.app.ormlib.OrmModuleManager;
import com.tbruyelle.rxpermissions.RxPermissions;
import com.wty.app.bluetoothcar.base.AppConstant;
import com.wty.app.bluetoothcar.base.MyApplication;
import com.wty.app.bluetoothcar.data.UserDALEx;
import com.wty.app.bluetoothcar.mvp.contract.IUserLoginContract;
import com.wty.app.bluetoothcar.mvp.presenter.UserLoginPresenter;
import com.wty.app.bluetoothcar.view.LoginInputView;
import com.wty.lib.widget.activity.BaseActivity;

import butterknife.Bind;
import butterknife.OnClick;
import rx.functions.Action1;

/**
 * @author wty
 * @Description 注册/登陆界面
 **/
public class LoginActivity extends BaseActivity<UserLoginPresenter> implements IUserLoginContract.IUserLoginView{

    public static final String IS_ChangeClient= "IS_ChangeClient";

    @Bind(R.id.login_icon)
    ImageView mloginIcon;
    @Bind(R.id.login_inputview)
    LoginInputView mloginInputview;
    @Bind(R.id.login_version)
    TextView tv_version;
    @Bind(R.id.rl_content)
    RelativeLayout contentlayout;

    boolean isChangeClient;

    @OnClick(R.id.login_signup)
    void goToRegisterActivity(){
        UserRegisterActivity.startActivity(this, AppConstant.ActivityResult.Request_Register);
    }

    public static void startActivity(Activity activity,boolean isChangeClient) {
        Intent intent = new Intent(activity, LoginActivity.class);
        intent.putExtra(IS_ChangeClient,isChangeClient);
        activity.startActivity(intent);
    }

    private String userid;

    @Override
    public UserLoginPresenter getPresenter() {
        return new UserLoginPresenter();
    }

    @Override
    public void onInitView(Bundle savedInstanceState) {
        isChangeClient = getIntent().getBooleanExtra(IS_ChangeClient,false);

        CommonUtil.keyboardControl(LoginActivity.this, false, mloginInputview.getAccountInput());

        final boolean isAutoLogin = PreferenceUtil.getInstance().isAutoLogin();
        String psw = PreferenceUtil.getInstance().getLastPassword();
        String userid = PreferenceUtil.getInstance().getLastAccount();

        if(PreferenceUtil.getInstance().isFirstLogin() && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            //如果是第一次安装应用程序 适配android6.0动态权限
            RxPermissions.getInstance(this)
                    .request(Manifest.permission.READ_PHONE_STATE,
                            Manifest.permission.READ_EXTERNAL_STORAGE)
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
                if (true) {
                    mPresenter.login(MyApplication.getInstance(),mloginInputview.getAccount().toString(), mloginInputview.getPassword().toString(), mloginInputview.isRememberPsw());
                }
            }
        });

        tv_version.setText("V"+ CommonUtil.getVersion(this)+"."+CommonUtil.getVersionCode(this));

        if (userid != null) {
            mloginInputview.setAccount(userid);
            mloginInputview.setPassword(psw);
            if (!TextUtils.isEmpty(psw)) {
                mloginInputview.setIsRememberPsw(true);
            } else {
                mloginInputview.setIsRememberPsw(false);
            }
        } else {// 第一次使用，默认不记住密码
            mloginInputview.setIsRememberPsw(false);
        }

        if(isAutoLogin && !isChangeClient){//自动登录就调整到主页面
            contentlayout.setVisibility(View.GONE);
            finishActivity();
        }else{
            contentlayout.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected boolean isEnableStatusBar() {
        return true;
    }

    @Override
    public int getLayoutResource() {
        return R.layout.activity_login;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == AppConstant.ActivityResult.Request_Register) {
                //切换数据库
                OrmModuleManager.getInstance().setCurrentDBName(AppConstant.DBName.Common_DB);
                userid = data.getStringExtra(UserRegisterActivity.USERID);
                AppLogUtil.d(userid);
                UserDALEx user = UserDALEx.get().findById(userid);
                mloginInputview.getAccountInput().setText(user.getUserid());
            }
        }
    }

    @Override
    public void finishActivity() {
        new Handler().postDelayed(new Runnable(){

            public void run() {
                NewMainActivity.startActivity(LoginActivity.this);
                AppLogUtil.d("跳转到主页面 ");
                finish();
            }

        }, 500);

    }

    @Override
    public void showFailed(String msg) {
        contentlayout.setVisibility(View.VISIBLE);
        super.showFailed(msg);
    }
}
