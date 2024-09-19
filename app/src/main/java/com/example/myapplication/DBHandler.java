package com.example.myapplication;

import java.time.Instant;
import android.content.ContentValues;
import android.content.Context;
import java.util.Map;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;
import java.util.HashMap;

public class DBHandler extends SQLiteOpenHelper {
    // Variables for respiratory rate and heart rate
    private String m_respiratory_rate;
    private Map<String, String> db_map = new HashMap<>();

    // Database constants
    private static final String kDB_NAME = "myapp"; // Database name
    private static String timestamp;

    // Table name and database version
    private static final String kSUPER_TABLE = "symptom";
    private static final int kDB_VERSION = 1;

    private String m_heart_rate;

    // Constructor for initializing the database
    public DBHandler(Context context) {
        super(context, kDB_NAME, null, kDB_VERSION);

        SQLiteDatabase sqlite_db = this.getWritableDatabase();
        if(TextUtils.isEmpty(timestamp)) {
            this.timestamp = Instant.now().toString();
        }
    }

    // Method to insert symptom data into the database
    public void addDBItems() {
        ContentValues values = new ContentValues();

        SQLiteDatabase sqlite_db = this.getWritableDatabase();

        // Add symptom data to ContentValues, using the map and replacing underscores in keys
        values.put(Consts.Shortness_of_Breath, db_map.get(replaceScoresFunc(Consts.Shortness_of_Breath)));
        values.put(Consts.Soar_Throat, db_map.get(replaceScoresFunc(Consts.Soar_Throat)));
        values.put(Consts.Fever, db_map.get(replaceScoresFunc(Consts.Fever)));
        values.put(Consts.Diarrhea, db_map.get(replaceScoresFunc(Consts.Diarrhea)));
        values.put(Consts.Feeling_tired, db_map.get(replaceScoresFunc(Consts.Feeling_tired)));
        values.put(Consts.Nausea, db_map.get(replaceScoresFunc(Consts.Nausea)));
        values.put("timestamp", this.timestamp);
        values.put(Consts.HEART_RATE_COL, this.m_heart_rate);
        values.put(Consts.RESPIRATORY_RATE_COL, this.m_respiratory_rate);
        values.put(Consts.Cough, db_map.get(replaceScoresFunc(Consts.Cough)));
        values.put(Consts.Headache, db_map.get(replaceScoresFunc(Consts.Headache)));
        values.put(Consts.Loss_of_smell_or_taste, db_map.get(replaceScoresFunc(Consts.Loss_of_smell_or_taste)));
        values.put(Consts.Muscle_Ache, db_map.get(replaceScoresFunc(Consts.Muscle_Ache)));

        // Insert the row into the database, replacing any existing data with the same timestamp
        sqlite_db.insertWithOnConflict(kSUPER_TABLE, null, values, SQLiteDatabase.CONFLICT_REPLACE);
        sqlite_db.close(); // Close the database
    }

    // Method to set the map that holds symptom data
    public void setDBMap(Map<String, String> db_map) {
        this.db_map = new HashMap<>(db_map);
    }

    // Method to set the respiratory rate
    public void Respiratory_rate_SET_Func(String val) {
        this.m_respiratory_rate = val;
    }

    // Utility method to replace underscores with spaces in symptom names
    public String replaceScoresFunc(String input) {
        String result = input.replaceAll("_", " ");
        return result;
    }

    // Method to handle database upgrade (not relevant in this version)
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        // Drop the table if it exists and recreate it
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + kSUPER_TABLE);
        onCreate(sqLiteDatabase);
    }

    // Method to create the symptom table in the database
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        // SQL command to create the table with the appropriate columns
        String tableCreation = "CREATE TABLE " + kSUPER_TABLE + "("
                + "timestamp" + " TEXT PRIMARY KEY,"
                + Consts.Headache + " TEXT,"
                + Consts.Loss_of_smell_or_taste + " TEXT,"
                + Consts.Fever + " TEXT,"
                + Consts.Diarrhea + " TEXT,"
                + Consts.Feeling_tired + " TEXT,"
                + Consts.Muscle_Ache + " TEXT,"
                + Consts.Shortness_of_Breath + " TEXT,"
                + Consts.Soar_Throat + " TEXT,"
                + Consts.HEART_RATE_COL + " TEXT,"
                + Consts.RESPIRATORY_RATE_COL + " TEXT,"
                + Consts.Cough + " TEXT,"
                + Consts.Nausea + " TEXT)";

        sqLiteDatabase.execSQL(tableCreation);
    }

    // Method to set the heart rate value
    public void Heart_rate_SET_FUNC(String val) {
        this.m_heart_rate = val;
    }
}
