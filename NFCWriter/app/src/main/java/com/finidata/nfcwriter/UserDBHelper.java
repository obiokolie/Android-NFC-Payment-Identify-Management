package com.finidata.nfcwriter;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by OBINNA OKOLIE on 8/15/2017.
 */
public class UserDBHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "USERINFO.DB";
    public static final int DATABASE_VERSION = 1;
    public static final String CREATE_QUERY = "CREATE TABLE "+ DBContract.NewUserInfo.USER_TABLE_NAME+"(" + DBContract.NewUserInfo.USER_FIRST_NAME+" TEXT," +
            DBContract.NewUserInfo.USER_LAST_NAME+" TEXT,"+ DBContract.NewUserInfo.USER_EMAIL+" TEXT,"+ DBContract.NewUserInfo.USER_OCCUPATION+" TEXT,"+
            DBContract.NewUserInfo.USER_LOCATION+" TEXT,"+ DBContract.NewUserInfo.USER_GENDER+" TEXT,"+ DBContract.NewUserInfo.USER_PHONE+" TEXT,"+
            DBContract.NewUserInfo.USER_PICTURE+" TEXT);";

    public UserDBHelper (Context context){
        super (context, DATABASE_NAME, null, DATABASE_VERSION);
        Log.e("Database Created", "Database created /open");
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_QUERY);
        Log.e("DATABASE Operation","Table created....");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void addInformation(String fname, String lname,String email, String occupation,String location,String gender, String phone, String picture,  SQLiteDatabase db)
    {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DBContract.NewUserInfo.USER_FIRST_NAME, fname);
        contentValues.put(DBContract.NewUserInfo.USER_LAST_NAME, lname);
        contentValues.put(DBContract.NewUserInfo.USER_EMAIL, email);
        contentValues.put(DBContract.NewUserInfo.USER_OCCUPATION, occupation);
        contentValues.put(DBContract.NewUserInfo.USER_LOCATION, location);
        contentValues.put(DBContract.NewUserInfo.USER_GENDER, gender);
        contentValues.put(DBContract.NewUserInfo.USER_PHONE, phone);
        contentValues.put(DBContract.NewUserInfo.USER_PICTURE, picture);
        db.insert(DBContract.NewUserInfo.USER_TABLE_NAME, null, contentValues);
        Log.e("DATABASE Operation","One row inserted....");
    }

}
