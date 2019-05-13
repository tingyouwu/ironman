package com.wty.app.bluetoothcar.base;

import android.app.Application;
import android.content.Context;

import com.kw.app.commonlib.CommonModuleManager;
import com.kw.app.ormlib.OrmModuleManager;

public class MyApplication extends Application {

	private static Context mApplication;

	@Override
	public void onCreate() {
		super.onCreate();

		mApplication = this.getApplicationContext();

		CommonModuleManager.init(mApplication);
		OrmModuleManager.init(mApplication);
	}

	/**
	 * 功能描述：获得一个全局的application对象
	 **/
	public static Context getInstance(){
		return mApplication;
	}

}
