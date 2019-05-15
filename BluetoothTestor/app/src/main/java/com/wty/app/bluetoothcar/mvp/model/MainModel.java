package com.wty.app.bluetoothcar.mvp.model;

import com.wty.app.bluetoothcar.data.BloodSugarDALEx;
import com.wty.app.bluetoothcar.mvp.contract.IMainContract;
import com.wty.lib.widget.utils.ICallBack;

import java.util.List;

/**
 * @author wty
 */
public class MainModel implements IMainContract.IMainModel {

    @Override
    public void refresh(ICallBack<List<BloodSugarDALEx>> callBack) {
       List<BloodSugarDALEx> list = BloodSugarDALEx.get().getLast20DaysByDate();
       callBack.onSuccess(list);
    }
}
