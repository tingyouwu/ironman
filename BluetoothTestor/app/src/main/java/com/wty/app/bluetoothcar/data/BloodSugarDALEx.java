package com.wty.app.bluetoothcar.data;

import com.kw.app.ormlib.SqliteBaseDALEx;
import com.kw.app.ormlib.annotation.DatabaseField;
import com.kw.app.ormlib.annotation.DatabaseField.FieldType;
import com.kw.app.ormlib.annotation.SqliteDao;

import java.util.List;

/**
 * @Desc 血糖数据表
 **/

public class BloodSugarDALEx extends SqliteBaseDALEx {

    @DatabaseField(primaryKey = true,Type = FieldType.VARCHAR)
    private String date;
    @DatabaseField(Type = FieldType.REAL)
    private float level;//发送者的uid

    public static BloodSugarDALEx get() {
        return SqliteDao.getDao(BloodSugarDALEx.class);
    }

    public List<BloodSugarDALEx> getAllListByDate(){
        String sql = String.format("select * from %s order by date(date) asc",TABLE_NAME);
        return findList(sql);
    }

    public List<BloodSugarDALEx> getLast20DaysByDate(){
        String sql = String.format("select * from %s order by date(date) desc limit 20",TABLE_NAME);
        return findList(sql);
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public float getLevel() {
        return level;
    }

    public void setLevel(float level) {
        this.level = level;
    }
}
