package com.example.arthur.arcboxv2;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Arthur on 28.11.16.
 */

public class DBHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "ArcDB";
    public static final String TABLE_ORDER = "arcboxes";

    public static final String KEY_ID = "_id";
    public static final String KEY_NAME = "name";
    public static final String KEY_WEIGHT = "weight";
    public static final String KEY_FROM = "fromm";
    public static final String KEY_TO = "too";
    public static final String KEY_FIO = "fio";
    public static final String KEY_EMAIL = "email";
    public static final String KEY_PHONE = "phone";


    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + TABLE_ORDER + " (" + KEY_ID
                + " integer primary key autoincrement," + KEY_NAME + " text," + KEY_WEIGHT + " text," + KEY_FROM
                + " text," + KEY_TO + " text," + KEY_FIO + " text," + KEY_EMAIL + " text," + KEY_PHONE
                + " text" + ");");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(TABLE_ORDER);
        onCreate(db);
    }
}
