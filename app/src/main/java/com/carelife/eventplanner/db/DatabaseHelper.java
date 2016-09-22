package com.carelife.eventplanner.db;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.carelife.eventplanner.dom.Plan;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "event.db";//数据库名称
    private static final int SCHEMA_VERSION = 1;//版本号,则是升级之后的,升级方法请看onUpgrade方法里面的判断

    public DatabaseHelper(Context context) {//构造函数,接收上下文作为参数,直接调用的父类的构造函数
        super(context, DATABASE_NAME, null, SCHEMA_VERSION);
    }

    private static DatabaseHelper instance;

    public static DatabaseHelper getInstance(Context context) {
        if(instance == null) {
            instance = new DatabaseHelper(context);
        }
        return instance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {//创建的是一个午餐订餐的列表,id,菜名,地址等等
        db.execSQL("CREATE TABLE events (_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "title TEXT, " +
                "startDate INTEGER, " +
                "endDate INTEGER, " +
                "location TEXT, " +
                "venue TEXT, " +
                "note TEXT, " +
                "attendString TEXT, " +
                "recordPath TEXT);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public List<Plan> getAll() {
        List<Plan> planList = new ArrayList<>();
        Cursor cursor = getReadableDatabase().rawQuery("SELECT * FROM events", null);
        if(cursor == null) {
            return planList;
        }
        while (cursor.moveToNext())
        {
            planList.add(Plan.fromCursor(cursor));
        }
        cursor.close();
        return planList;
    }

    public Plan getById(long id) {//根据点击事件获取id,查询数据库
        Cursor cursor = (getReadableDatabase()
                .rawQuery("SELECT * FROM events WHERE _id=?", new String[]{id+""}));
        Plan plan = null;
        if(cursor == null) {
            return null;
        }
        while (cursor.moveToNext())
        {
            plan = Plan.fromCursor(cursor);
        }
        cursor.close();
        return plan;
    }

    public void delete(long id) {
        getWritableDatabase().execSQL("DELETE FROM events WHERE _id=?", new String[]{id+""});
    }

    public void insertOrUpdate(Plan plan) {
        ContentValues cv = makeContent(plan);

        if(getById(plan.get_id()) == null) {
            getWritableDatabase().insert("events", null, cv);
        } else {
            getWritableDatabase().update("events", cv, "_id=?",
                    new String[]{plan.get_id()+""});
        }
    }

    private ContentValues makeContent(Plan plan) {
        ContentValues cv = new ContentValues();
        cv.put("title", plan.getTitle());
        cv.put("startDate", plan.getStartDate());
        cv.put("endDate", plan.getEndDate());
        cv.put("location", plan.getLocation());
        cv.put("venue", plan.getVenue());
        cv.put("note", plan.getNote());
        cv.put("attendString", plan.getAttendString());
        cv.put("recordPath", plan.getRecordPath());
        return cv;
    }
}