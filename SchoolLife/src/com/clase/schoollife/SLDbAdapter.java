package com.clase.schoollife;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import java.util.ArrayList;

public class SLDbAdapter {
	//Subjects Table
	public static final String KEY_IDSUBJECT = "_idsubject";
	public static final String KEY_NAME = "name";
    public static final String KEY_ABBREVIATION = "abbreviation";
    public static final String KEY_PROFESSOR="professor";
    public static final String KEY_CLASSROOM="classroom";
    
    //Tasks table
    public static final String KEY_IDTASK = "_idtask";
	public static final String KEY_TYPE = "type";
    public static final String KEY_TITLE = "title";
    public static final String KEY_EXPLANATION="explanation";
    public static final String KEY_DATE="date";
    public static final String KEY_MARK="mark";
    public static final String KEY_REVISION="revision";
    public static final String KEY_REVISIONDATE="revisiondate";
    public static final String KEY_COMPLETED="completed";
    public static final String KEY_FEELINGSSTARS="feelingsstars";
    public static final String KEY_FEELINGS= "feelings";
    public static final String KEY_SUBJECT="subject";
    
    private static final String TAG = "SLDbAdapter";
    private DatabaseHelper mDbHelper;
    private SQLiteDatabase mDb;
    
    /**
     * Database creation sql statement
     */
    //Subjects table creation statement
    private static final String DATABASE_CREATE1 =
    		"create table subjects (_idsubject integer primary key autoincrement, name text not null, abbreviation text not null, professor text not null, classroom text not null);";
    
    //Tasks table creation statement
    private static final String DATABASE_CREATE2 =
    		"create table tasks (_idtask integer primary key autoincrement, type text not null, title text not null, explanation text not null, date date not null, mark real, revision integer not null, revisiondate date, completed integer not null, feelingsstars integer, feeling text, subject integer not null);";

    private static final String DATABASE_NAME = "data";
    private static final String DATABASE_TABLE1 = "subjects";
    private static final String DATABASE_TABLE2 = "tasks";
    private static final int DATABASE_VERSION = 1;
    
    private final Context mCtx;
    
    private static class DatabaseHelper extends SQLiteOpenHelper {

        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(DATABASE_CREATE1);
            db.execSQL(DATABASE_CREATE2);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
                    + newVersion + ", which will destroy all old data");
            db.execSQL("DROP TABLE IF EXISTS notes");
            onCreate(db);
        }
    }
    
    /**
     * Constructor - takes the context to allow the database to be
     * opened/created
     * 
     * @param ctx the Context within which to work
     */
    public SLDbAdapter(Context ctx) {
        this.mCtx = ctx;
    }
    
    /**
     * Open the database. If it cannot be opened, try to create a new
     * instance of the database. If it cannot be created, throw an exception to
     * signal the failure
     * 
     * @return this (self reference, allowing this to be chained in an
     *         initialization call)
     * @throws SQLException if the database could be neither opened or created
     */
    public SLDbAdapter open() throws SQLException {
        mDbHelper = new DatabaseHelper(mCtx);
        mDb = mDbHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        mDbHelper.close();
    }
    
    /**
     * Create a new subject using the details provided. If the task is
     * successfully created return the new idSubject for that subject, otherwise return
     * a -1 to indicate failure.
     * 
     * @param name the name of the subject
     * @param abbreviation the abbreviation of the subject
     * @param professor the professor of the subject
     * @param classroom the classroom of the subject
     * @return idSubject or -1 if failed
     */
    public long createSubject(String name, String abbreviation, String professor, String classroom) {
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_NAME, name);
        initialValues.put(KEY_ABBREVIATION, abbreviation);
        initialValues.put(KEY_PROFESSOR, professor);
        initialValues.put(KEY_CLASSROOM, classroom);

        return mDb.insert(DATABASE_TABLE1, null, initialValues);
    }
    
    /**
     * Create a new task using the details provided. If the task is
     * successfully created return the new idTask for that task, otherwise return
     * a -1 to indicate failure.
     * 
     * @param type exam or assignment
     * @param title the title of the task
     * @param explanation the explanation of the task
     * @param date the date of the task
     * @param mark the mark of the task
     * @param rev revision (yes or not)
     * @param revisionDate	the date of the revision
     * @param comp	task completed
     * @param feelingsStars from 0 to 10
     * @param feelings	the feelings of the task
     * @param subject	the subject related to the task
     * @return idTask or -1 if failed
     */
    public long createTask(String type, String title, String explanation, String date, double mark, boolean rev, String revisionDate, boolean comp, int feelingsStars, String feelings, long subject) {
    	int revision=1;
        int completed=1;
    	if(!rev){
        	revision=0;
        }
        if(!comp){
        	completed=0;
        }
        
    	ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_TYPE, type);
        initialValues.put(KEY_TITLE, title);
        initialValues.put(KEY_EXPLANATION, explanation);
        initialValues.put(KEY_DATE, date);
        initialValues.put(KEY_MARK, mark);
        initialValues.put(KEY_REVISION, revision);
        initialValues.put(KEY_REVISIONDATE, revisionDate);
        initialValues.put(KEY_COMPLETED, completed);
        initialValues.put(KEY_FEELINGSSTARS, feelingsStars);
        initialValues.put(KEY_FEELINGS, feelings);
        initialValues.put(KEY_SUBJECT, subject);

        return mDb.insert(DATABASE_TABLE2, null, initialValues);
    }
    
    /**
     * Delete the subject with the given idSubject
     * 
     * @param idSubject id of subject to delete
     * @return true if deleted, false otherwise
     */
    public boolean deleteSubject(long idSubject) {
    	/*
    	Cursor mCursor= fetchAllTasks();
    	boolean tasks= true;
    	if(mCursor.getLong(mCursor.getColumnIndexOrThrow(SLDbAdapter.KEY_SUBJECT))==idSubject){
    		tasks= (tasks && deleteTask(mCursor.getLong(mCursor.getColumnIndexOrThrow(SLDbAdapter.KEY_IDTASK))));
    	}
        return (tasks && (mDb.delete(DATABASE_TABLE1, KEY_IDSUBJECT + "=" + idSubject, null) > 0));*/
    	ArrayList<Long> tasks= taskOfSubject(idSubject);
    	boolean task= true;
    	for(int i=0; i<tasks.size();i++){
    		task=(task && deleteTask(tasks.get(i)));
    	}
    	return (task && (mDb.delete(DATABASE_TABLE1, KEY_IDSUBJECT + "=" + idSubject, null) > 0));
    }
    
    /**
     * Delete the task with the given idTask
     * 
     * @param idTask id of task to delete
     * @return true if deleted, false otherwise
     */
    public boolean deleteTask(long idTask) {
        return mDb.delete(DATABASE_TABLE2, KEY_IDTASK + "=" + idTask, null) > 0;
    }
    
    /**
     * Return a Cursor over the list of all subjects in the database
     * 
     * @return Cursor over all subjects
     */
    public Cursor fetchAllSubjects() {
        return mDb.query(DATABASE_TABLE1, new String[] {KEY_IDSUBJECT, KEY_NAME, KEY_ABBREVIATION, KEY_PROFESSOR, KEY_CLASSROOM}, null, null, null, null, null);
    }
    
    /**
     * Return a Cursor over the list of all tasks in the database
     * 
     * @return Cursor over all tasks
     */
    public Cursor fetchAllTasks() {
        return mDb.query(DATABASE_TABLE2, new String[] {KEY_IDTASK, KEY_TYPE, KEY_EXPLANATION, KEY_DATE, KEY_MARK, KEY_REVISION, KEY_REVISIONDATE, KEY_COMPLETED, KEY_FEELINGSSTARS, KEY_FEELINGS, KEY_SUBJECT}, null, null, null, null, null);
    }
    
    /**
     * Return a Cursor positioned at the subject that matches the given idSubject
     * 
     * @param idSubject id of subject to retrieve
     * @return Cursor positioned to matching subject, if found
     * @throws SQLException if note could not be found/retrieved
     */
    public Cursor fetchSubjects(long idSubject) throws SQLException {
        Cursor mCursor = mDb.query(true, DATABASE_TABLE1, new String[] {KEY_IDSUBJECT, KEY_NAME, KEY_ABBREVIATION, KEY_PROFESSOR, KEY_CLASSROOM}, KEY_IDSUBJECT + "=" + idSubject, null, null, null, null, null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;
    }
    
    /**
     * Return a Cursor positioned at the task that matches the given idTask
     * 
     * @param idTask id of task to retrieve
     * @return Cursor positioned to matching task, if found
     * @throws SQLException if note could not be found/retrieved
     */
    public Cursor fetchTasks(long idTask) throws SQLException {
        Cursor mCursor = mDb.query(true, DATABASE_TABLE2, new String[] {KEY_IDTASK, KEY_TYPE, KEY_EXPLANATION, KEY_DATE, KEY_MARK, KEY_REVISION, KEY_REVISIONDATE, KEY_COMPLETED, KEY_FEELINGSSTARS, KEY_FEELINGS, KEY_SUBJECT}, KEY_IDTASK + "=" + idTask, null, null, null, null, null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;
    }
    
    /**
     * Update the subject using the details provided. The subject to be updated is
     * specified using the idSubject, and it is altered to use the values passed in
     * 
     * @param idSubject id of subject to update
     * @param name the name of the subject
     * @param abbreviation the abbreviation of the subject
     * @param professor the professor of the subject
     * @param classroom the classroom of the subject
     * @return true if the subject was successfully updated, false otherwise
     */
    public boolean updateSubject(long idSubject, String name, String abbreviation, String professor, String classroom) {
        ContentValues args = new ContentValues();
        args.put(KEY_NAME, name);
        args.put(KEY_ABBREVIATION, abbreviation);
        args.put(KEY_PROFESSOR, professor);
        args.put(KEY_CLASSROOM, classroom);

        return mDb.update(DATABASE_TABLE1, args, KEY_IDSUBJECT + "=" + idSubject, null) > 0;
    }
    
    /**
     * Update the task using the details provided. The task to be updated is
     * specified using the idTask, and it is altered to use the values passed in
     * 
     * @param idTask id of task to update
     * @param type exam or assignment
     * @param title the title of the task
     * @param explanation the explanation of the task
     * @param date the date of the task
     * @param mark the mark of the task
     * @param rev revision (yes or not)
     * @param revisionDate	the date of the revision
     * @param comp	task completed
     * @param feelingsStars from 0 to 10
     * @param feelings	the feelings of the task
     * @param subject	the subject related to the task
     * @return true if the task was successfully updated, false otherwise
     */
    public boolean updateTask(long idTask, String type, String title, String explanation, String date, double mark, boolean rev, String revisionDate, boolean comp, int feelingsStars, String feelings, long subject) {
        int revision=1;
        int completed=1;
    	if(!rev){
        	revision=0;
        }
        if(!comp){
        	completed=0;
        }
        
    	ContentValues args = new ContentValues();
        args.put(KEY_TYPE, type);
        args.put(KEY_TITLE, title);
        args.put(KEY_EXPLANATION, explanation);
        args.put(KEY_DATE, date);
        args.put(KEY_MARK, mark);
        args.put(KEY_REVISION, revision);
        args.put(KEY_REVISIONDATE, revisionDate);
        args.put(KEY_COMPLETED, completed);
        args.put(KEY_FEELINGSSTARS, feelingsStars);
        args.put(KEY_FEELINGS, feelings);
        args.put(KEY_SUBJECT, subject);

        return mDb.update(DATABASE_TABLE2, args, KEY_IDTASK + "=" + idTask, null) > 0;
    }
    
    public ArrayList<Long> taskOfSubject(long idSubject){
    	ArrayList<Long> idTasks = new ArrayList<Long>();
    	Cursor mCursor = fetchAllTasks();
    	idTasks.add(mCursor.getLong(mCursor.getColumnIndexOrThrow(SLDbAdapter.KEY_SUBJECT)));
    	return idTasks;
    }
}
