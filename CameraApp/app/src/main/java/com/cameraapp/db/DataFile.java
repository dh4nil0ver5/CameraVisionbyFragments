package com.cameraapp.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DataFile extends SQLiteOpenHelper {

    public DataFile(@Nullable Context context,
                    @Nullable String name,
                    @Nullable SQLiteDatabase.CursorFactory factory,
                    int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(new ListVariabel().CREATE_NAMETBL);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    public void onSaveList(String COL_PATH, int COL_SIZE, String COL_TYPE){
        SQLiteDatabase db = getReadableDatabase();
        db.execSQL("INSERT INTO "+new ListVariabel().NAMETBL+" (" +
                new ListVariabel().COL_PATH+", "+
                new ListVariabel().COL_SIZE+", "+
                new ListVariabel().COL_TYPE+" ) VALUES ('"+COL_PATH+"', " +
                COL_SIZE+", '"+COL_TYPE+"' )");
    }
}
