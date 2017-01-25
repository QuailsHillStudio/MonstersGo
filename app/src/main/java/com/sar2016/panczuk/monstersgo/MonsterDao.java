package com.sar2016.panczuk.monstersgo;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;

/**
 * Created by alexandra on 16/01/17.
 */

public class MonsterDao {

    private static final String MONSTERS_TABLE = "monsters_table";

    // COLUMNS
    private static final String COL_ID = "ID";
    private static final int NUM_COL_ID = 0;

    private static final String COL_CREATED_AT = "created_at";
    private static final int NUM_COL_CREATED_AT = 1;

    private static final String COL_TXT = "txt";
    private static final int NUM_COL_TXT = 2;

    private static final String COL_USER = "user";
    private static final int NUM_COL_USER = 3;

    private static final String COL_LEVEL = "level";
    private static final int NUM_COL_LEVEL = 4;

    private SQLiteDatabase db;
    private MonsterSQLite myMonsterDB;

    public MonsterDao(Context context) {
        myMonsterDB = new MonsterSQLite(context);
    }

    // Open DB writing mode
    public void open() {
        db = myMonsterDB.getWritableDatabase();
    }

    public void close() {
        db.close();
    }

    public SQLiteDatabase getDB() {
        return db;
    }

    public long insertMonster(Monster monster) {
        ContentValues values = new ContentValues();
        values.put(COL_TXT, monster.getName());
        values.put(COL_USER, monster.getUser());
        values.put(COL_LEVEL, monster.getLevel());
        long id = -1;
        try {
            id = db.insertOrThrow(MONSTERS_TABLE, null, values);
        }catch(Exception e){
            Log.e("Error inserting", e.getMessage());
            e.printStackTrace();
        }

        return id;
    }

    public int updateMonster(long id, Monster monster) {
        ContentValues values = new ContentValues();
        values.put(COL_TXT, monster.getName());
        values.put(COL_USER, monster.getUser());
        values.put(COL_LEVEL, monster.getLevel());
        return db.update(MONSTERS_TABLE, values, COL_ID + " = " + id, null);
    }

    // get Tweet by ID
    public Monster getMonsterById(int id) {
        Cursor c = db.query(MONSTERS_TABLE, new String[] {COL_ID, COL_CREATED_AT, COL_TXT, COL_USER, COL_LEVEL}, COL_ID + " = " + id,
                null, null, null, null);
        return cursorToMonster(c);
    }

    // Remove Tweet by id
    public int removeMonsterById(int id) {
        return db.delete(MONSTERS_TABLE, COL_ID + " = " + id, null);
    }

    // get all Tweets
    public ArrayList<Monster> getAll() {
        Cursor c = db.query(MONSTERS_TABLE, new String[] {COL_ID, COL_CREATED_AT, COL_TXT, COL_USER, COL_LEVEL},
                null, null, null, null, null);

        ArrayList<Monster> monsters = new ArrayList<Monster>();

        c.moveToFirst();
        for (int i = 0; i < c.getCount(); i++) {
            c.moveToPosition(i);
            Monster monster = cursorToMonster(c);
            monsters.add(monster);
        }

        c.close();
        return monsters;
    }

    // get all Tweets
    public ArrayList<Monster> getAllFromUsers(String user) {
        Cursor c = db.query(MONSTERS_TABLE, new String[] {COL_ID, COL_CREATED_AT, COL_TXT, COL_USER, COL_LEVEL},
                COL_USER+"=?", new String[] { user }, null, null, null);

        ArrayList<Monster> monsters = new ArrayList<Monster>();

        c.moveToFirst();
        for (int i = 0; i < c.getCount(); i++) {
            c.moveToPosition(i);
            Monster monster = cursorToMonster(c);
            monsters.add(monster);
        }

        c.close();
        return monsters;
    }

    public void deleteAll() {
        db.delete(MONSTERS_TABLE, null, null);
    }

    private Monster cursorToMonster(Cursor c) {
        if (c.getCount() == 0)
            return null;

        // Create tweet object from DB
        Monster monster = new Monster(c.getString(NUM_COL_TXT));
        monster.setId(c.getInt(NUM_COL_ID));
        monster.setUser(c.getString(NUM_COL_USER));
        monster.setLevel(c.getInt(NUM_COL_LEVEL));

        return monster;
    }
}
