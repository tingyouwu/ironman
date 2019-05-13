package com.wty.app.bluetoothcar.data;

import com.kw.app.ormlib.SqliteBaseDALEx;
import com.kw.app.ormlib.annotation.DatabaseField;
import com.kw.app.ormlib.annotation.DatabaseField.FieldType;
import com.kw.app.ormlib.annotation.SqliteDao;

/**
 * 用户信息
 * @author wty
 */
public class UserDALEx extends SqliteBaseDALEx {

	@DatabaseField(primaryKey = true,Type = FieldType.VARCHAR)
	private String userid;

	@DatabaseField(Type = FieldType.VARCHAR)
	private String username; // 用户的用户名

	@DatabaseField(Type = FieldType.VARCHAR)
	private String password;


	public static UserDALEx get() {
		return SqliteDao.getDao(UserDALEx.class);
	}


	public String getUserid() {
		return userid;
	}

	public void setUserid(String userid) {
		this.userid = userid;
	}

	public String getNickname() {
		return username;
	}

	public void setNickname(String nickname) {
		this.username = nickname;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
}
