package com.example.newteachverse;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class EventDatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "teachverse.db";
    private static final int DATABASE_VERSION = 2;


    private static final String TABLE_EVENTS = "events";
    private static final String TABLE_STUDENTS = "students";
    private static final String TABLE_JOINED_EVENTS = "joined_events";


    private static final String KEY_EVENT_ID = "id";
    private static final String KEY_EVENT_NAME = "name";
    private static final String KEY_EVENT_DATE = "date";
    private static final String KEY_EVENT_TYPE = "type";
    private static final String KEY_EVENT_DESCRIPTION = "description";
    private static final String KEY_EVENT_LOCATION = "location";
    private static final String KEY_EVENT_IMAGE_URI = "image_uri";


    private static final String KEY_STUDENT_ID = "id";
    private static final String KEY_STUDENT_NAME = "name";
    private static final String KEY_STUDENT_EMAIL = "email";
    private static final String KEY_STUDENT_PASSWORD = "password";


    private static final String KEY_JOIN_ID = "id";
    private static final String KEY_JOIN_EVENT_ID = "event_id";
    private static final String KEY_JOIN_STUDENT_ID = "student_id";

    public EventDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Create tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_EVENTS_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_EVENTS + " ("
                + KEY_EVENT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + KEY_EVENT_NAME + " TEXT,"
                + KEY_EVENT_DATE + " TEXT,"
                + KEY_EVENT_TYPE + " TEXT,"
                + KEY_EVENT_DESCRIPTION + " TEXT,"
                + KEY_EVENT_LOCATION + " TEXT,"
                + KEY_EVENT_IMAGE_URI + " TEXT"
                + ")";

        String CREATE_STUDENTS_TABLE = "CREATE TABLE " + TABLE_STUDENTS + "("
                + KEY_STUDENT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + KEY_STUDENT_NAME + " TEXT,"
                + KEY_STUDENT_EMAIL + " TEXT UNIQUE,"
                + KEY_STUDENT_PASSWORD + " TEXT"
                + ")";

        String CREATE_JOINED_EVENTS_TABLE = "CREATE TABLE " + TABLE_JOINED_EVENTS + "("
                + KEY_JOIN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + KEY_JOIN_EVENT_ID + " INTEGER,"
                + KEY_JOIN_STUDENT_ID + " INTEGER,"
                + "FOREIGN KEY(" + KEY_JOIN_EVENT_ID + ") REFERENCES " + TABLE_EVENTS + "(" + KEY_EVENT_ID + ") ON DELETE CASCADE,"
                + "FOREIGN KEY(" + KEY_JOIN_STUDENT_ID + ") REFERENCES " + TABLE_STUDENTS + "(" + KEY_STUDENT_ID + ") ON DELETE CASCADE"
                + ")";

        db.execSQL(CREATE_EVENTS_TABLE);
        db.execSQL(CREATE_STUDENTS_TABLE);
        db.execSQL(CREATE_JOINED_EVENTS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            db.execSQL("ALTER TABLE " + TABLE_EVENTS + " ADD COLUMN " + KEY_EVENT_IMAGE_URI + " TEXT");
        }
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_JOINED_EVENTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_EVENTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_STUDENTS);
        onCreate(db);
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        if (!db.isReadOnly()) {
            db.execSQL("PRAGMA foreign_keys=ON;");
        }
    }



    public long addEvent(Event event) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("name", event.getName());
        values.put("date", event.getDate());
        values.put("type", event.getType());
        values.put("description", event.getDescription());
        values.put("location", event.getLocation());
        values.put("image_uri", event.getImageUri() != null ? event.getImageUri() : "");

        long id = db.insert("events", null, values);
        db.close();
        Log.d("AddEvent", id != -1 ? "Event inserted successfully: " + id : "Event insertion failed");
        return id;
    }

    public List<Event> getAllEvents() {
        List<Event> eventList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM events", null);

        if (cursor.moveToFirst()) {
            do {
                Event event = new Event();
                event.setId(cursor.getInt(cursor.getColumnIndexOrThrow("id")));
                event.setName(cursor.getString(cursor.getColumnIndexOrThrow("name")));
                event.setDate(cursor.getString(cursor.getColumnIndexOrThrow("date")));
                event.setType(cursor.getString(cursor.getColumnIndexOrThrow("type")));
                event.setDescription(cursor.getString(cursor.getColumnIndexOrThrow("description")));
                event.setLocation(cursor.getString(cursor.getColumnIndexOrThrow("location")));
                int imageUriIndex = cursor.getColumnIndex(KEY_EVENT_IMAGE_URI);
                if (imageUriIndex != -1) {
                    event.setImageUri(cursor.getString(imageUriIndex));
                }
                // If isJoined column exists, set it; otherwise, skip
                int isJoinedIndex = cursor.getColumnIndex("isJoined");
                if (isJoinedIndex != -1) {
                    event.setJoined(cursor.getInt(isJoinedIndex) == 1);
                }
                eventList.add(event);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        Log.d("EventDB", "Fetched " + eventList.size() + " events");
        return eventList;
    }

    public void updateEvent(Event event) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_EVENT_NAME, event.getName());
        values.put(KEY_EVENT_DATE, event.getDate());
        values.put(KEY_EVENT_TYPE, event.getType());
        values.put(KEY_EVENT_DESCRIPTION, event.getDescription());
        values.put(KEY_EVENT_LOCATION, event.getLocation());
        values.put(KEY_EVENT_IMAGE_URI, event.getImageUri());
        db.update(TABLE_EVENTS, values, KEY_EVENT_ID + "=?", new String[]{String.valueOf(event.getId())});
    }

    public void deleteEvent(int eventId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_EVENTS, KEY_EVENT_ID + "=?", new String[]{String.valueOf(eventId)});
    }



    public long addStudent(String name, String email, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_STUDENT_NAME, name);
        values.put(KEY_STUDENT_EMAIL, email);
        values.put(KEY_STUDENT_PASSWORD, password);
        return db.insert(TABLE_STUDENTS, null, values);
    }

    public int getStudentIdByEmail(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_STUDENTS, new String[]{KEY_STUDENT_ID},
                KEY_STUDENT_EMAIL + "=?", new String[]{email}, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            int id = cursor.getInt(0);
            cursor.close();
            return id;
        }
        return -1;
    }



    public boolean hasStudentJoined(int studentId, int eventId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_JOINED_EVENTS, null,
                KEY_JOIN_EVENT_ID + "=? AND " + KEY_JOIN_STUDENT_ID + "=?",
                new String[]{String.valueOf(eventId), String.valueOf(studentId)}, null, null, null);

        boolean joined = cursor.moveToFirst();
        cursor.close();
        return joined;
    }

    public long joinEvent(int studentId, int eventId) {
        if (hasStudentJoined(studentId, eventId)) return -1;

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_JOIN_EVENT_ID, eventId);
        values.put(KEY_JOIN_STUDENT_ID, studentId);
        return db.insert(TABLE_JOINED_EVENTS, null, values);
    }

    public List<String> getStudentNamesByEvent(int eventId) {
        List<String> names = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT s.name FROM " + TABLE_STUDENTS + " s INNER JOIN " +
                TABLE_JOINED_EVENTS + " j ON s.id = j.student_id WHERE j.event_id = ?";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(eventId)});

        if (cursor.moveToFirst()) {
            do {
                names.add(cursor.getString(0));
            } while (cursor.moveToNext());
        }

        cursor.close();
        return names;
    }

    public List<Event> getAllEvents(int studentId) {
        List<Event> events = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT e.*, " +
                "(SELECT COUNT(*) FROM " + TABLE_JOINED_EVENTS + " j " +
                "WHERE j.event_id = e.id AND j.student_id = ?) AS joined " +
                "FROM " + TABLE_EVENTS + " e";

        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(studentId)});

        if (cursor.moveToFirst()) {
            do {
                Event event = new Event();
                event.setId(cursor.getInt(cursor.getColumnIndexOrThrow("id")));
                event.setName(cursor.getString(cursor.getColumnIndexOrThrow("name")));
                event.setDate(cursor.getString(cursor.getColumnIndexOrThrow("date")));
                event.setType(cursor.getString(cursor.getColumnIndexOrThrow("type")));
                event.setDescription(cursor.getString(cursor.getColumnIndexOrThrow("description")));
                event.setLocation(cursor.getString(cursor.getColumnIndexOrThrow("location")));
                event.setJoined(cursor.getInt(cursor.getColumnIndexOrThrow("joined")) > 0);
                int imageUriIndex = cursor.getColumnIndex(KEY_EVENT_IMAGE_URI);
                if (imageUriIndex != -1) {
                    event.setImageUri(cursor.getString(imageUriIndex));
                }
                events.add(event);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return events;
    }

    public List<Student> getAllStudents() {
        List<Student> students = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_STUDENTS, null);

        if (cursor.moveToFirst()) {
            do {
                Student s = new Student();
                s.setId(cursor.getInt(cursor.getColumnIndexOrThrow("id")));
                s.setName(cursor.getString(cursor.getColumnIndexOrThrow("name")));
                s.setEmail(cursor.getString(cursor.getColumnIndexOrThrow("email")));
                students.add(s);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return students;
    }

    public void deleteStudent(int studentId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_STUDENTS, KEY_STUDENT_ID + "=?", new String[]{String.valueOf(studentId)});
        db.close();
    }

    public boolean isValidStudent(String email, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_STUDENTS, new String[]{KEY_STUDENT_ID},
                KEY_STUDENT_EMAIL + "=? AND " + KEY_STUDENT_PASSWORD + "=?",
                new String[]{email, password}, null, null, null);
        boolean valid = cursor.moveToFirst();
        cursor.close();
        return valid;
    }

    public boolean isValidAdmin(String email, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_STUDENTS,
            null,
            KEY_STUDENT_EMAIL + "=? AND " + KEY_STUDENT_PASSWORD + "=?",
            new String[]{email, password},
            null, null, null);

        boolean valid = false;
        if (cursor != null && cursor.moveToFirst()) {
            String name = cursor.getString(cursor.getColumnIndexOrThrow(KEY_STUDENT_NAME));
            if (name.toLowerCase().contains("admin")) {
                valid = true;
            }
            cursor.close();
        }

        return valid;
    }

    public boolean isAdminExists(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_STUDENTS,
            null,
            KEY_STUDENT_EMAIL + "=?",
            new String[]{email},
            null, null, null);

        boolean exists = false;
        if (cursor != null && cursor.moveToFirst()) {
            String name = cursor.getString(cursor.getColumnIndexOrThrow(KEY_STUDENT_NAME));
            if (name.toLowerCase().contains("admin")) {
                exists = true;
            }
            cursor.close();
        }

        return exists;
    }

    public void addAdmin(String name, String email, String password) {
        addStudent(name, email, password);
    }


    public void deleteEventById(int eventId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("events", "id = ?", new String[]{String.valueOf(eventId)});
        db.close();
    }


    public void deleteEventStatusByEventId(int eventId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("joined_events", "event_id = ?", new String[]{String.valueOf(eventId)});
        db.close();
    }

    public List<String> getStudentNamesByEventId(int eventId) {
        List<String> names = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(
            "SELECT s.name FROM students s " +
            "JOIN joined_events je ON s.id = je.student_id " +
            "WHERE je.event_id = ?",
            new String[]{String.valueOf(eventId)}
        );

        if (cursor.moveToFirst()) {
            do {
                names.add(cursor.getString(0));
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return names;
    }
}
