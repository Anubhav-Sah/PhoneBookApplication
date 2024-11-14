package com.example.phonebookapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "phonebook.db";
    private static final int DATABASE_VERSION = 1;

    private static final String TABLE_CONTACTS = "contacts";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_NAME = "name";
    private static final String COLUMN_PHONE = "phone";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE_CONTACTS = "CREATE TABLE " + TABLE_CONTACTS + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_NAME + " TEXT,"
                + COLUMN_PHONE + " TEXT" + ")";
        db.execSQL(CREATE_TABLE_CONTACTS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CONTACTS);
        onCreate(db);
    }

    public void addContact(Context context, String name, String phone) {
        SQLiteDatabase db = this.getWritableDatabase();

        // Check if contact with the same name already exists
        Cursor cursor = db.query(TABLE_CONTACTS, new String[]{COLUMN_ID, COLUMN_NAME, COLUMN_PHONE},
                COLUMN_NAME + " = ?", new String[]{name}, null, null, null);

        if (cursor != null && cursor.getCount() > 0) {
            // Contact already exists, so don't insert
            cursor.close();

            // Show a Toast message to the user
            Toast.makeText(context, "Contact already exists!", Toast.LENGTH_SHORT).show();
        } else {
            // Contact doesn't exist, so insert
            ContentValues values = new ContentValues();
            values.put(COLUMN_NAME, name);
            values.put(COLUMN_PHONE, phone);

            db.insert(TABLE_CONTACTS, null, values);
        }
        db.close();
    }
    public boolean isContactExists(Context context, String name) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_CONTACTS, new String[]{COLUMN_ID},
                COLUMN_NAME + " = ?", new String[]{name}, null, null, null);

        boolean exists = cursor != null && cursor.getCount() > 0;
        if (cursor != null) {
            cursor.close();
        }
        db.close();

        return exists;
    }



    public void updateContact(int id, String name, String phone) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, name);
        values.put(COLUMN_PHONE, phone);

        db.update(TABLE_CONTACTS, values, COLUMN_ID + " = ?", new String[]{String.valueOf(id)});
        db.close();
    }

    public void deleteContact(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_CONTACTS, COLUMN_ID + " = ?", new String[]{String.valueOf(id)});
        db.close();
    }

    // Modify this method to return contacts in alphabetical order by name
    public List<Contact> getAllContacts() {
        List<Contact> contactList = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();
        // Query to get all contacts sorted by name in ascending order without case sensitivity
        Cursor cursor = db.query(TABLE_CONTACTS, new String[]{COLUMN_ID, COLUMN_NAME, COLUMN_PHONE},
                null, null, null, null, COLUMN_NAME + " COLLATE NOCASE ASC");

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    Contact contact = new Contact(
                            cursor.getInt(cursor.getColumnIndex(COLUMN_ID)),
                            cursor.getString(cursor.getColumnIndex(COLUMN_NAME)),
                            cursor.getString(cursor.getColumnIndex(COLUMN_PHONE))
                    );
                    contactList.add(contact);
                } while (cursor.moveToNext());
            }
            cursor.close();
        }
        db.close();
        return contactList;
    }


    // New method to search contacts by name
    public List<Contact> searchContacts(String query) {
        List<Contact> contactList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_CONTACTS,
                new String[]{COLUMN_ID, COLUMN_NAME, COLUMN_PHONE},
                COLUMN_NAME + " LIKE ?",
                new String[]{"%" + query + "%"},
                null, null, null);

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    Contact contact = new Contact(
                            cursor.getInt(cursor.getColumnIndex(COLUMN_ID)),
                            cursor.getString(cursor.getColumnIndex(COLUMN_NAME)),
                            cursor.getString(cursor.getColumnIndex(COLUMN_PHONE))
                    );
                    contactList.add(contact);
                } while (cursor.moveToNext());
            }
            cursor.close();
        }
        db.close();
        return contactList;
    }
}
