package com.bomboverk.boat.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class DatabaseHelper extends SQLiteOpenHelper {

    private Context context;

    private static final String DATABASE = "db_boat";
    private static final String TBWAYS = "tb_ways";

    public DatabaseHelper(Context context) {
        super(context, DATABASE, null, 1);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String dbcreateways = "CREATE TABLE IF NOT EXISTS " + TBWAYS
                + "(keyname TEXT NOT NULL,"
                + "pathway TEXT NOT NULL)";

        db.execSQL(dbcreateways);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public int checkExistingWays() {
        int count = 1;
        Cursor cursor = getReadableDatabase().rawQuery("SELECT COUNT(*) FROM " + TBWAYS, null);

        while (cursor.moveToNext()) {
            count = cursor.getInt(0);
        }
        cursor.close();

        return count;
    }

    public void insertWay(String type, String uri) {
        ContentValues values = new ContentValues();
        values.put("keyname", type);
        values.put("pathway", uri);
        getWritableDatabase().insert(TBWAYS, null, values);
    }

    public String getWays(String way) {
        String pathWay = "noneway";
        Cursor cursor = getReadableDatabase().rawQuery("SELECT * FROM " + TBWAYS + " WHERE keyname = '" + way + "'", null);

        while (cursor.moveToNext()) {
            pathWay = cursor.getString(cursor.getColumnIndex("pathway"));
        }
        cursor.close();

        return pathWay;
    }

    public void deleteWays(String way) {
        SQLiteDatabase db = getWritableDatabase();
        String[] args = {way};
        db.delete(TBWAYS, "keyname=?", args);
    }

    public void updateWays(String type, String uri) {
        ContentValues values = new ContentValues();

        values.put("pathway", uri);

        String[] idParaAlterar = {type};
        getWritableDatabase().update(TBWAYS, values, "keyname=?", idParaAlterar);
    }

}
