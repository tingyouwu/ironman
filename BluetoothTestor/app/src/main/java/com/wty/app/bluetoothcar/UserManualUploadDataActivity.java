package com.wty.app.bluetoothcar;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.TextView;

import com.codbking.widget.DatePickDialog;
import com.codbking.widget.OnChangeLisener;
import com.codbking.widget.OnSureLisener;
import com.codbking.widget.bean.DateType;
import com.kw.app.commonlib.utils.AppLogUtil;
import com.kw.app.commonlib.utils.NumberUtil;
import com.kw.app.commonlib.utils.TimeUtil;
import com.wty.app.bluetoothcar.data.BloodSugarDALEx;
import com.wty.app.bluetoothcar.mvp.contract.IUserManualUploadContract;
import com.wty.app.bluetoothcar.mvp.presenter.UserManualUploadPresenter;
import com.wty.lib.widget.activity.BaseActivity;
import com.wty.lib.widget.utils.OnDismissCallbackListener;

import java.util.Date;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;

import static cn.pedant.SweetAlert.SweetAlertDialog.NORMAL_TYPE;

/**
 * @author wty
 * @Description 用户数据人工采集
 **/
public class UserManualUploadDataActivity extends BaseActivity<UserManualUploadPresenter> implements IUserManualUploadContract.IUserManualUploadView {

    @Bind(R.id.et_data)
    EditText et_data;
    @Bind(R.id.btn_date)
    TextView btn_date;

    @OnClick(R.id.btn_date)
    void selectDate(){
        DatePickDialog dialog = new DatePickDialog(UserManualUploadDataActivity.this);
        //设置上下年分限制
        dialog.setYearLimt(1);
        //设置标题
        dialog.setTitle("选择日期");
        //设置类型
        dialog.setType(DateType.TYPE_YMD);
        //设置消息体的显示格式，日期格式
        dialog.setMessageFormat("yyyy-MM-dd");
        //设置选择回调
        dialog.setOnChangeLisener(new OnChangeLisener() {
            @Override
            public void onChanged(Date date) {

            }
        });
        //设置点击确定按钮回调
        dialog.setOnSureLisener(new OnSureLisener() {
            @Override
            public void onSure(Date date) {
                AppLogUtil.d("" + TimeUtil.dateToString(date,TimeUtil.FORMAT_YEAR_MONTH_DAY));
                btn_date.setText(TimeUtil.dateToString(date,TimeUtil.FORMAT_YEAR_MONTH_DAY));
            }
        });
        dialog.show();
    }

    @OnClick(R.id.btn_commit)
    void commit(){
        if(super.submit()){
            onToast(new OnDismissCallbackListener("是否确认提交?",NORMAL_TYPE) {
                @Override
                public void onCallback() {
                    mPresenter.uploadData(UserManualUploadDataActivity.this,getData());
                }
            });
        }
    }

    public static void startActivity(Activity activity) {
        Intent intent = new Intent(activity, UserManualUploadDataActivity.class);
        activity.startActivity(intent);
    }

    @Override
    public UserManualUploadPresenter getPresenter() {
        return new UserManualUploadPresenter();
    }

    @Override
    public void onInitView(Bundle savedInstanceState) {
        getDefaultNavigation().setTitle("人工采集");
    }

    @Override
    public int getLayoutResource() {
        return R.layout.activity_upload_manual;
    }

    @Override
    protected List<String> validate() {
        List<String> list = super.validate();
        if(TextUtils.isEmpty(et_data.getText().toString()) || !NumberUtil.isDoubleOrFloat(et_data.getText().toString())){
            list.add("请填写正确的血糖数值");
        }else if(TextUtils.isEmpty(btn_date.getText().toString())){
            list.add("请选择测量日期");
        }
        return list;
    }

    public BloodSugarDALEx getData(){
        BloodSugarDALEx dalEx = new BloodSugarDALEx();
        dalEx.setDate(btn_date.getText().toString());
        dalEx.setLevel(Float.valueOf(et_data.getText().toString()));
        return dalEx;
    }
}
