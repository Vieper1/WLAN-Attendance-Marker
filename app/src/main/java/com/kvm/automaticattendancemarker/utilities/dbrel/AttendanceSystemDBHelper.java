package com.kvm.automaticattendancemarker.utilities.dbrel;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;

/**
 * Created by Viper GTS on 9/18/2016.
 */

public class AttendanceSystemDBHelper extends SQLiteOpenHelper
{
    // v1.0
    private static final String DATABASE_NAME = "AttendanceBase.db";
    private static final int DATABASE_VERSION = 11;




    // IF YOU CHANGE THE TABLE NAME(S) HERE, CHANGE IT IN THE STRINGS TOO
    //////////////////////////////////////////////////////////////////////////////// TABLE NAMES
    public static final String TABLE_0_NAME = "student_list";
    public static final String TABLE_1_NAME = "class_main";
    public static final String TABLE_2_NAME = "batch_main";
    public static final String TABLE_3_NAME = "class_attendance";
    public static final String TABLE_4_NAME = "practical_attendance";
    public static final String TABLE_5_NAME = "subject_list";
    public static final String TABLE_6_NAME = "session_list";
    //////////////////////////////////////////////////////////////////////////////// TABLE NAMES





    // Table 0 - student_list
    public static final String TABLE_0_COL_0 = "_id";
    public static final String TABLE_0_COL_1 = "name";
    public static final String TABLE_0_COL_2 = "unique_id";
    public static final String TABLE_0_COL_3 = "roll_no";
    public static final String TABLE_0_COL_4 = "_class_id";
    public static final String TABLE_0_COL_5 = "_batch_id";
    public static final String TABLE_0_COL_6 = "ssid";
    public static final String TABLE_0_COL_7 = "email_id";
    public static final String TABLE_0_COL_8 = "photo_url";


    // Table 1 - class_main                                        // Create new with common ref_id for Class and Batch & Type
    public static final String TABLE_1_COL_0 = "_id";
    public static final String TABLE_1_COL_1 = "branch";
    public static final String TABLE_1_COL_2 = "year";
    public static final String TABLE_1_COL_3 = "last_roll_no";


    // Table 2 - batch_main
    public static final String TABLE_2_COL_0 = "id";
    public static final String TABLE_2_COL_1 = "batch_name";
    public static final String TABLE_2_COL_2 = "_class_id";
    public static final String TABLE_2_COL_3 = "student_count";    // Redundant Column


    // Table 5 - subject_list
    public static final String TABLE_5_COL_0 = "_id";
    public static final String TABLE_5_COL_1 = "subject";
    public static final String TABLE_5_COL_2 = "_class_id";
    public static final String TABLE_5_COL_3 = "_batch_id";


    // Table 6 - session_list
    public static final String TABLE_6_COL_0 = "_id";
    public static final String TABLE_6_COL_1 = "_subject_id";
    public static final String TABLE_6_COL_2 = "_class_batch_id";
    public static final String TABLE_6_COL_3 = "type";
    public static final String TABLE_6_COL_4 = "students_present_csv";
    public static final String TABLE_6_COL_5 = "timestamp_start";
    public static final String TABLE_6_COL_6 = "timestamp_end";
    public static final String TABLE_6_COL_7 = "students_absent_csv";












    ////////////////////////////////////////////////////////////////////// Temp unusability
    // Table 3 - class_attendance
    public static final String TABLE_3_COL_0 = "_id";
    public static final String TABLE_3_COL_1 = "_student_id";
    public static final String TABLE_3_COL_2 = "_subject_id";
    public static final String TABLE_3_COL_3 = "_class_id";    // Redundant Column
    public static final String TABLE_3_COL_4 = "attended";
    public static final String TABLE_3_COL_5 = "total";


    // Table 4 - practical_attendance
    public static final String TABLE_4_COL_0 = "_id";
    public static final String TABLE_4_COL_1 = "_student_id";
    public static final String TABLE_4_COL_2 = "_subject_id";
    public static final String TABLE_4_COL_3 = "_batch_id";    // Redundant Column
    public static final String TABLE_4_COL_4 = "attended";
    public static final String TABLE_4_COL_5 = "total";
    ////////////////////////////////////////////////////////////////////// Temp unusability



    // Create Queries
    // v1.0
    private final String CREATE_TABLE_0_QUERY = String.format(
            "CREATE TABLE %s(%s INTEGER PRIMARY KEY, %s TEXT NOT NULL, %s TEXT, %s INTEGER NOT NULL, %s INTEGER, %s TEXT, %s TEXT, %s TEXT, %s TEXT)",

            TABLE_0_NAME,

            TABLE_0_COL_0,
            TABLE_0_COL_1,
            TABLE_0_COL_2,
            TABLE_0_COL_3,
            TABLE_0_COL_4,
            TABLE_0_COL_5,
            TABLE_0_COL_6,
            TABLE_0_COL_7,
            TABLE_0_COL_8
    );

    private final String CREATE_TABLE_1_QUERY = String.format(
            "CREATE TABLE %s(%s INTEGER PRIMARY KEY, %s TEXT NOT NULL, %s INTEGER NOT NULL, %s INTEGER NOT NULL)",

            TABLE_1_NAME,

            TABLE_1_COL_0,
            TABLE_1_COL_1,
            TABLE_1_COL_2,
            TABLE_1_COL_3
    );

    private final String CREATE_TABLE_2_QUERY = String.format(
            "CREATE TABLE %s(%s INTEGER PRIMARY KEY, %s TEXT NOT NULL, %s INTEGER, %s INTEGER NOT NULL)",

            TABLE_2_NAME,

            TABLE_2_COL_0,
            TABLE_2_COL_1,
            TABLE_2_COL_2,
            TABLE_2_COL_3
    );

    private final String CREATE_TABLE_3_QUERY = String.format(
            "CREATE TABLE %s(%s INTEGER PRIMARY KEY, %s INTEGER, %s TEXT NOT NULL, %s INTEGER, %s INTEGER, %s INTEGER)",

            TABLE_3_NAME,

            TABLE_3_COL_0,
            TABLE_3_COL_1,
            TABLE_3_COL_2,
            TABLE_3_COL_3,
            TABLE_3_COL_4,
            TABLE_3_COL_5
    );

    private final String CREATE_TABLE_4_QUERY = String.format(
            "CREATE TABLE %s(%s INTEGER PRIMARY KEY, %s INTEGER, %s TEXT NOT NULL, %s INTEGER, %s INTEGER, %s INTEGER)",

            TABLE_4_NAME,

            TABLE_4_COL_0,
            TABLE_4_COL_1,
            TABLE_4_COL_2,
            TABLE_4_COL_3,
            TABLE_4_COL_4,
            TABLE_4_COL_5
    );

    private final String CREATE_TABLE_5_QUERY = String.format(
            "CREATE TABLE %s(%s INTEGER PRIMARY KEY, %s TEXT, %s TEXT, %s TEXT)",

            TABLE_5_NAME,

            TABLE_5_COL_0,
            TABLE_5_COL_1,
            TABLE_5_COL_2,
            TABLE_5_COL_3
    );



    // V1.1
    private final String CREATE_TABLE_6_QUERY = String.format(
            "CREATE TABLE %s(%s INTEGER PRIMARY KEY, %s INTEGER, %s INTEGER, %s TEXT, %s TEXT, %s TEXT, %s TEXT, %s TEXT)",

            TABLE_6_NAME,

            TABLE_6_COL_0,
            TABLE_6_COL_1,
            TABLE_6_COL_2,
            TABLE_6_COL_3,
            TABLE_6_COL_4,
            TABLE_6_COL_5,
            TABLE_6_COL_6,
            TABLE_6_COL_7
    );








    //////////////////////////////////////////////////////////////////////////////// Overridden Methods
    private SQLiteDatabase db;

    public AttendanceSystemDBHelper(Context context)
    {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        db = this.getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        db.execSQL(CREATE_TABLE_0_QUERY);
        db.execSQL(CREATE_TABLE_1_QUERY);
        db.execSQL(CREATE_TABLE_2_QUERY);
        db.execSQL(CREATE_TABLE_3_QUERY);
        db.execSQL(CREATE_TABLE_4_QUERY);
        db.execSQL(CREATE_TABLE_5_QUERY);
        db.execSQL(CREATE_TABLE_6_QUERY);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_0_NAME);
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_1_NAME);
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_2_NAME);
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_3_NAME);
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_4_NAME);
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_5_NAME);
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_6_NAME);
        onCreate(db);
    }
    //////////////////////////////////////////////////////////////////////////////// Overridden Methods




    //////////////////////////////////////////////////////////////////////////////// Custom Methods
    public Cursor querySpecific(String table_name, String[] projection, String selection, String[] selectionArgs, String groupBy, String having, String sortOrder)
    {
        switch(table_name)
        {
            case TABLE_0_NAME:
                return db.query(TABLE_0_NAME, projection, selection, selectionArgs, groupBy, having, sortOrder);

            case TABLE_1_NAME:
                return db.query(TABLE_1_NAME, projection, selection, selectionArgs, null, null, sortOrder);

            case TABLE_2_NAME:
                return db.query(TABLE_2_NAME, projection, selection, selectionArgs, null, null, sortOrder);

            case TABLE_3_NAME:
                return db.query(TABLE_3_NAME, projection, selection, selectionArgs, null, null, sortOrder);

            case TABLE_4_NAME:
                return db.query(TABLE_4_NAME, projection, selection, selectionArgs, null, null, sortOrder);

            case TABLE_5_NAME:
                return db.query(TABLE_5_NAME, projection, selection, selectionArgs, null, null, sortOrder);

            case TABLE_6_NAME:
                return db.query(TABLE_6_NAME, projection, selection, selectionArgs, null, null, sortOrder);

            default:
                return null;
        }
    }

    public Cursor rawQueryGetFull(String table_name)
    {
        switch(table_name)
        {
            case TABLE_0_NAME:
                return db.rawQuery("select * from "+TABLE_0_NAME+";", null);

            case TABLE_1_NAME:
                return db.rawQuery("select * from "+TABLE_1_NAME+";", null);

            case TABLE_2_NAME:
                return db.rawQuery("select * from "+TABLE_2_NAME+";", null);

            case TABLE_3_NAME:
                return db.rawQuery("select * from "+TABLE_3_NAME+";", null);

            case TABLE_4_NAME:
                return db.rawQuery("select * from "+TABLE_4_NAME+";", null);

            case TABLE_5_NAME:
                return db.rawQuery("select * from "+TABLE_5_NAME+";", null);

            case TABLE_6_NAME:
                return db.rawQuery("select * from "+TABLE_6_NAME+";", null);

            default:
                return null;
        }
    }

    public Uri insertSpecific(Uri uri, String table_name, ContentValues values)
    {
        switch(table_name)
        {
            case TABLE_0_NAME:
                if(db.insert(TABLE_0_NAME, null, values)==-1)
                {
                    throw new RuntimeException("InsertionFailed@"+TABLE_0_NAME);
                }
                break;

            case TABLE_1_NAME:
                if(db.insert(TABLE_1_NAME, null, values)==-1)
                {
                    throw new RuntimeException("InsertionFailed@"+TABLE_1_NAME);
                }
                break;


            case TABLE_2_NAME:
                if(db.insert(TABLE_2_NAME, null, values)==-1)
                {
                    throw new RuntimeException("InsertionFailed@"+TABLE_2_NAME);
                }
                break;


            case TABLE_3_NAME:
                if(db.insert(TABLE_3_NAME, null, values)==-1)
                {
                    throw new RuntimeException("InsertionFailed@"+TABLE_3_NAME);
                }
                break;


            case TABLE_4_NAME:
                if(db.insert(TABLE_4_NAME, null, values)==-1)
                {
                    throw new RuntimeException("InsertionFailed@"+TABLE_4_NAME);
                }
                break;

            case TABLE_5_NAME:
                if(db.insert(TABLE_5_NAME, null, values)==-1)
                {
                    throw new RuntimeException("InsertionFailed@"+TABLE_5_NAME);
                }
                break;

            case TABLE_6_NAME:
                if(db.insert(TABLE_6_NAME, null, values)==-1)
                {
                    throw new RuntimeException("InsertionFailed@"+TABLE_6_NAME);
                }
                break;

            default:
                return null;
        }

        return null;
    }

    public int deleteSpecific(Uri uri, String table_name, String selection, String[] selectionArgs)
    {
        switch(table_name)
        {
            case TABLE_0_NAME:
                return db.delete(TABLE_0_NAME, selection, selectionArgs);

            case TABLE_1_NAME:
                return db.delete(TABLE_1_NAME, selection, selectionArgs);

            case TABLE_2_NAME:
                return db.delete(TABLE_2_NAME, selection, selectionArgs);

            case TABLE_3_NAME:
                return db.delete(TABLE_3_NAME, selection, selectionArgs);

            case TABLE_4_NAME:
                return db.delete(TABLE_4_NAME, selection, selectionArgs);

            case TABLE_5_NAME:
                return db.delete(TABLE_5_NAME, selection, selectionArgs);

            case TABLE_6_NAME:
                return db.delete(TABLE_6_NAME, selection, selectionArgs);

            default:
                return 0;
        }
    }

    public int updateSpecific(Uri uri, ContentValues values, String selection, String[] selectionArgs)
    {
        return 0;
    }

    public int updateClassMainUsingID(ContentValues contentValues, String classId)
    {

        return db.update(TABLE_1_NAME, contentValues, "_id="+classId, null);
    }

    public int updateStudentsListUsingRollNo(ContentValues contentValues, String rollNo, String classBatchId)
    {
        String colRollNo = TABLE_0_COL_3;
        String colClassId = TABLE_0_COL_4;
        String where = String.format("%s = ? AND %s = ?", colRollNo, colClassId);
        String[] whereArgs = new String[]{rollNo, classBatchId};

        return db.update(TABLE_0_COL_0, contentValues, where, whereArgs);
    }


    @Override
    public synchronized void close()
    {
        super.close();
        db.close();
    }
    //////////////////////////////////////////////////////////////////////////////// Custom Methods
}
