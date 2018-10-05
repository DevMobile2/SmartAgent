package com.ramesh.smartagent.sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.ramesh.smartagent.pojos.Dependency;

public class SQLiteDatabaseHandler extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 3;
    private static final String DATABASE_NAME = "ConfigDB.db";
    private static final String TABLE_NAME = "Configs";

    public SQLiteDatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String CREATION_TABLE = "CREATE TABLE Configs ( "
                + "sno INTEGER PRIMARY KEY AUTOINCREMENT, " + "id TEXT,"+"name TEXT, "
                + "type TEXT, " + "sizeInBytes INTEGER,"+"cdn_path TEXT)";
        sqLiteDatabase.execSQL(CREATION_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        this.onCreate(sqLiteDatabase);
    }

    public void addConfig(Dependency dependency) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("id", dependency.getId());
        values.put("name", dependency.getName());
        values.put("type", dependency.getType());
        values.put("sizeInBytes",dependency.getSizeInBytes());
        values.put("cdn_path",dependency.getCdn_path());
        // insert
        db.insert(TABLE_NAME,null, values);
        db.close();
    }
}
