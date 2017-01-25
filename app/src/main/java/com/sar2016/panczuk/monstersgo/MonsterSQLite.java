package com.sar2016.panczuk.monstersgo;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class MonsterSQLite extends SQLiteOpenHelper {
    private static final int VERSION_DB = 2;
    private static final String DB_NAME = "monsterGo.db";

    private static final String MONSTERS_TABLE = "monsters_table";
    private static final String ID = "ID";
    private static final String CREATED_AT = "created_at";
    private static final String TXT = "txt";
    private static final String USER = "user";
    private static final String LEVEL = "level";


    private static final String CREATE_DB = "CREATE TABLE "  + MONSTERS_TABLE
            + " (" +
                ID + " INTEGER PRIMARY KEY, " +
                CREATED_AT + " DATETIME, " +
                TXT + " TEXT NOT NULL, " +
                USER + " TEXT NOT NULL," +
                LEVEL + " INT NOT NULL" +
            ");";

    public MonsterSQLite(Context context) {
        super(context, DB_NAME, null, VERSION_DB);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(CREATE_DB);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + MONSTERS_TABLE + ";");
        onCreate(sqLiteDatabase);
    }
}
