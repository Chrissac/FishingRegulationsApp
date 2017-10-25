package info.android.sqlite.helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by csacripante on 27/07/2017.
 */

public class DatabaseHelper extends SQLiteOpenHelper {
    // Logcat tag
    private static final String LOG = "DatabaseHelper";

    // Database Version
    private static final int DATABASE_VERSION = 2;

    // Database Name
    private static final String DATABASE_NAME = "FishingRegulationsDBManager";

    // Table Names
    private static final String TABLE_USERS = "users";
    //private static final String TABLE_TAG = "tags";
    //private static final String TABLE_TODO_TAG = "todo_tags";

    // Common column names
    private static final String KEY_ID = "id";
    private static final String KEY_CREATED_AT = "created_at";

    // NOTES Table - column nmaes
    private static final String KEY_USERNAME = "userName";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_PASSWORD = "password";
    private static final String KEY_DB_ID = "dbId";

    // TAGS Table - column names
    //private static final String KEY_TAG_NAME = "tag_name";

    // NOTE_TAGS Table - column names
    private static final String KEY_USERS_ID = "users_id";
    //private static final String KEY_TAG_ID = "tag_id";

    // Table Create Statements
    // users table create statement
    private static final String CREATE_TABLE_USERS = "CREATE TABLE IF NOT EXISTS "
            + TABLE_USERS + "(" + KEY_ID + " INTEGER PRIMARY KEY,"
            + KEY_USERNAME + " TEXT,"
            + KEY_EMAIL + " TEXT,"
            + KEY_PASSWORD + " TEXT,"
            + KEY_CREATED_AT + " DATETIME,"
            + KEY_DB_ID + " INTEGER NULL"+ ")";

    // Tag table create statement
/*    private static final String CREATE_TABLE_TAG = "CREATE TABLE " + TABLE_TAG
            + "(" + KEY_ID + " INTEGER PRIMARY KEY," + KEY_TAG_NAME + " TEXT,"
            + KEY_CREATED_AT + " DATETIME" + ")";*/

    // todo_tag table create statement
/*    private static final String CREATE_TABLE_TODO_TAG = "CREATE TABLE "
            + TABLE_TODO_TAG + "(" + KEY_ID + " INTEGER PRIMARY KEY,"
            + KEY_TODO_ID + " INTEGER," + KEY_TAG_ID + " INTEGER,"
            + KEY_CREATED_AT + " DATETIME" + ")";*/

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        // creating required tables
        db.execSQL(CREATE_TABLE_USERS);
        //db.execSQL("INSERT INTO users  (userName,email,password,created_at) VALUES ('CHRISSAC','CHRIS.SAC@HOTMAIL.COM','PASSWORD','"+getDateTime()+"')");
/*        db.execSQL(CREATE_TABLE_TAG);
        db.execSQL(CREATE_TABLE_TODO_TAG);*/
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // on upgrade drop older tables
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
/*      db.execSQL("DROP TABLE IF EXISTS " + TABLE_TAG);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TODO_TAG);*/

        // create new tables
        onCreate(db);
    }

     /*
     * CREATE SINGLE USER``
     */
    public long createUser(users user) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_USERNAME, user.getUserName());
        values.put(KEY_EMAIL, user.getEmail());
        values.put(KEY_PASSWORD, user.getPassword());
        values.put(KEY_CREATED_AT, getDateTime());
        values.put(KEY_DB_ID, user.getdb_id());

        // insert row
        long user_id = db.insert(TABLE_USERS, null, values);


        /* for (long tag_id : tag_ids) {
            createTodoTag(todo_id, tag_id);
        }*/

        return user_id;
    }
    /*
     * get single user
     */
    public users getUser(long user_id) {
        SQLiteDatabase db = this.getReadableDatabase();

        String selectQuery = "SELECT  * FROM " + TABLE_USERS + " WHERE "
                + KEY_ID + " = " + user_id;

        Log.e(LOG, selectQuery);

        Cursor c = db.rawQuery(selectQuery, null);

        if (c != null)
            c.moveToFirst();

        users user = new users();
        user.setId(c.getInt(c.getColumnIndex(KEY_ID)));
        user.setUserName((c.getString(c.getColumnIndex(KEY_USERNAME))));
        user.setEmail((c.getString(c.getColumnIndex(KEY_EMAIL))));
        user.setPassword((c.getString(c.getColumnIndex(KEY_PASSWORD))));
        user.setCreated_at((c.getString(c.getColumnIndex(KEY_CREATED_AT))));

        return user;
    }
    /*
     * getting all users
     * */
    public List<users> getAllUsers() {
        List<users> users = new ArrayList<users>();
        String selectQuery = "SELECT  * FROM " + TABLE_USERS;

        Log.e(LOG, selectQuery);

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
                users user = new users();
                user.setId(c.getInt(c.getColumnIndex(KEY_ID)));
                user.setUserName((c.getString(c.getColumnIndex(KEY_USERNAME))));
                user.setEmail((c.getString(c.getColumnIndex(KEY_EMAIL))));
                user.setPassword((c.getString(c.getColumnIndex(KEY_PASSWORD))));
                user.setCreated_at((c.getString(c.getColumnIndex(KEY_CREATED_AT))));

                // adding to todo list
                users.add(user);
            } while (c.moveToNext());
        }
        return users;
    }
    /*
     * Updating a user
     */
    public int updateUser(users user) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_USERNAME, user.getUserName());
        values.put(KEY_PASSWORD, user.getPassword());

        // updating row
        return db.update(TABLE_USERS, values, KEY_ID + " = ?",
                new String[] { String.valueOf(user.getId()) });
    }

    /*
     * Deleting a user
     */
    public void deleteUser(long user_id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_USERS, KEY_ID + " = ?",
                new String[] { String.valueOf(user_id) });

    }


    // closing database
    public void closeDB() {
        SQLiteDatabase db = this.getReadableDatabase();
        if (db != null && db.isOpen())
            db.close();
    }
    /**
     * get datetime
     * */
    private String getDateTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        Date date = new Date();
        return dateFormat.format(date);
    }


}
