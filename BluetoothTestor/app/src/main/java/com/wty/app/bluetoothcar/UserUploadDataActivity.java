package com.wty.app.bluetoothcar;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.widget.CompoundButton;
import android.widget.EditText;

import com.wty.app.bluetoothcar.data.UserDALEx;
import com.wty.app.bluetoothcar.mvp.contract.IUserRegisterContract;
import com.wty.app.bluetoothcar.mvp.presenter.UserRegisterPresenter;
import com.wty.app.bluetoothcar.view.CheckBoxLabel;
import com.wty.lib.widget.activity.BaseActivity;

import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * @author wty
 * @Description 用户数据采集
 **/
public class UserUploadDataActivity extends BaseActivity<UserRegisterPresenter> implements IUserRegisterContract.IUserRegisterView {

    public static final String USERID = "userid";

    @Bind(R.id.et_name)
    EditText etName;
    @Bind(R.id.et_psw)
    EditText etPsw;
    @Bind(R.id.et_psw_confirm)
    EditText etPswConfirm;
    @Bind(R.id.btn_showpsw)
    CheckBoxLabel btnShowpsw;

    @OnClick(R.id.btn_sign)
    void sign(){
        //注册
        if(super.submit()){
            mPresenter.register(UserUploadDataActivity.this,getSubmitData());
        }
    }

    public static void startActivity(Activity activity) {
        Intent intent = new Intent(activity, UserUploadDataActivity.class);
        activity.startActivity(intent);
    }

    @Override
    public UserRegisterPresenter getPresenter() {
        return new UserRegisterPresenter();
    }

    @Override
    public void onInitView(Bundle savedInstanceState) {
        getDefaultNavigation().setTitle("注册账号");

        btnShowpsw.setOnCheckChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if(isChecked) {
                    // 显示为普通文本
                    etPsw.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    etPswConfirm.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                } else {
                    // 显示为密码
                    etPsw.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    etPswConfirm.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                }
            }
        });

    }

    @Override
    protected boolean isEnableStatusBar() {
        return true;
    }

    @Override
    protected List<String> validate() {
        List<String> list = super.validate();
        if(TextUtils.isEmpty(etName.getText().toString())){
            list.add("请填写正确手机号码");
        }else if(TextUtils.isEmpty(etPsw.getText().toString()) || TextUtils.isEmpty(etPswConfirm.getText().toString())){
            list.add("请填写密码");
        }else if(!etPsw.getText().toString().equals(etPswConfirm.getText().toString())){
            list.add("两次填写密码不一致");
        }
        return list;
    }

    @Override
    public int getLayoutResource() {
        return R.layout.activity_register;
    }

    private UserDALEx getSubmitData(){
        UserDALEx user = new UserDALEx();
        user.setUserid(etName.getText().toString());
        user.setPassword(etPsw.getText().toString());
        return user;
    }

    @Override
    public void finishActivity(String userid) {
        Intent intent = new Intent();
        intent.putExtra(USERID,userid);
        setResult(RESULT_OK, intent);
        finish();
    }
}
