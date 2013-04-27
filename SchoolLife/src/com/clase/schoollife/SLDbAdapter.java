package com.clase.schoollife;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class SLDbAdapter {
	//Subjects Table
	public static final String KEY_SUBJECTID = "subjectid";
	public static final String KEY_NAME = "name";
    public static final String KEY_ABBREVIATION = "abbreviation";
    public static final String KEY_PROFESSOR="professor";
    public static final String KEY_CLASSROOM="classroom";
    
    //Tasks table
    public static final String KEY_TASKID= "taskid";
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
    public static final String KEY_TASKSUBJECT="tasksubject";
    
    private static final String TAG = "SLDbAdapter";
    private DatabaseHelper mDbHelper;
    private SQLiteDatabase mDb;
    
    /**
     * Database creation sql statement
     */
    //Subjects table creation statement
    private static final String DATABASE_CREATE_SUBJECT =
    		"CREATE TABLE subject(subjectid integer primary key autoincrement, name text not null, abbreviation text not null, professor text not null, classroom text not null);";
    
    //Tasks table creation statement
    private static final String DATABASE_CREATE_TASK =
    		"CREATE TABLE task(taskid integer not null, type integer not null, title text not null, explanation text not null, date date not null, mark real, revision integer not null, revisiondate date, completed integer not null, feelingsstars integer, feelings text, tasksubject integer not null, CONSTRAINT PK_task_taskid PRIMARY KEY(taskid),FOREIGN KEY(tasksubject) REFERENCES subject(subjectid));";

    private static final String DATABASE_NAME = "data";
    private static final String DATABASE_TABLE_SUBJECT = "subject";
    private static final String DATABASE_TABLE_TASK = "task";
    private static final int DATABASE_VERSION = 3;
    
    private final Context mCtx;
    
    private static class DatabaseHelper extends SQLiteOpenHelper {

        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
        	db.execSQL("PRAGMA foreign_keys=ON;");
            db.execSQL(DATABASE_CREATE_SUBJECT);
            db.execSQL(DATABASE_CREATE_TASK);
            db.execSQL("CREATE INDEX taskindex ON task(tasksubject);");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
                    + newVersion + ", which will destroy all old data");
            db.execSQL("DROP TABLE IF EXISTS subject");
            db.execSQL("DROP TABLE IF EXISTS task");
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
     * successfully created return the new subjectId for that subject, otherwise return
     * a -1 to indicate failure.
     * 
     * @param name the name of the subject
     * @param abbreviation the abbreviation of the subject
     * @param professor the professor of the subject
     * @param classroom the classroom of the subject
     * @return subjectId or -1 if failed
     */
    public long createSubject(String name, String abbreviation, String professor, String classroom) {
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_NAME, name);
        initialValues.put(KEY_ABBREVIATION, abbreviation);
        initialValues.put(KEY_PROFESSOR, professor);
        initialValues.put(KEY_CLASSROOM, classroom);

        return mDb.insert(DATABASE_TABLE_SUBJECT, null, initialValues);
    }
    
    /**
     * Create a new task using the details provided. If the task is
     * successfully created return the new taskId for that task, otherwise return
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
     * @param taskSubject	the subject related to the task
     * @return taskId or -1 if failed
     */
    public long createTask(int type, String title, String explanation, String date, double mark, boolean rev, String revisionDate, boolean comp, int feelingsStars, String feelings, long taskSubject) {
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
        if(mark==-1){
        	initialValues.putNull(KEY_MARK);
    	} else{
    		initialValues.put(KEY_MARK, mark);
    	}
        initialValues.put(KEY_REVISION, revision);
        initialValues.put(KEY_REVISIONDATE, revisionDate);
        initialValues.put(KEY_COMPLETED, completed);
        if(feelingsStars==-1){
        	initialValues.putNull(KEY_FEELINGSSTARS);
        } else{
        	initialValues.put(KEY_FEELINGSSTARS, feelingsStars);
        }
        initialValues.put(KEY_FEELINGS, feelings);
        initialValues.put(KEY_TASKSUBJECT, taskSubject);

        return mDb.insert(DATABASE_TABLE_TASK, null, initialValues);
    }
    
    /**
     * Delete the subject with the given subjectId
     * 
     * @param subjectId id of subject to delete
     * @return true if deleted, false otherwise
     */
    public boolean deleteSubject(long subjectId) {
    	boolean task= deleteAllTasksOfSubject(subjectId);
    	return (mDb.delete(DATABASE_TABLE_SUBJECT, KEY_SUBJECTID+"="+subjectId, null) >0) && task;
    }
    
    /**
     * Delete the task with the given idTask
     * 
     * @param taskId id of task to delete
     * @return true if deleted, false otherwise
     */
    public boolean deleteTask(long taskId) {
        return mDb.delete(DATABASE_TABLE_TASK, KEY_TASKID + "=" + taskId, null) > 0;
    }
    
    public boolean deleteAllSubjects(){
    	mDb.execSQL("DELETE FROM "+DATABASE_TABLE_TASK +";");
    	mDb.execSQL("DELETE FROM "+DATABASE_TABLE_SUBJECT +";");
    	return true;
    }
    
    public boolean deleteAllTasksOfSubject(long subjectId){
    	return mDb.delete(DATABASE_TABLE_TASK, KEY_TASKSUBJECT + "=" + subjectId,null)>0;
    }
    
    /**
     * Return a Cursor over the list of all subjects in the database
     * 
     * @return Cursor over all subjects
     */
    public Cursor fetchAllSubjects() {
    	return mDb.query(DATABASE_TABLE_SUBJECT, new String[] {KEY_SUBJECTID+" _id", KEY_NAME, KEY_ABBREVIATION, KEY_PROFESSOR, KEY_CLASSROOM}, null, null, null, null, null);
    }
    
    /**
     * Return a Cursor over the list of all tasks in the database
     * 
     * @return Cursor over all tasks
     */
    public Cursor fetchAllTasks() {
        return mDb.query(DATABASE_TABLE_TASK, new String[] {KEY_TASKID+" _id", KEY_TYPE, KEY_TITLE, KEY_EXPLANATION, KEY_DATE, KEY_MARK, KEY_REVISION, KEY_REVISIONDATE, KEY_COMPLETED, KEY_FEELINGSSTARS, KEY_FEELINGS, KEY_TASKSUBJECT}, null, null, null, null, null);
    }
    
    /**
     * Return a Cursor positioned at the subject that matches the given subjectId
     * 
     * @param subjectId id of subject to retrieve
     * @return Cursor positioned to matching subject, if found
     * @throws SQLException if note could not be found/retrieved
     */
    public Cursor fetchSubject(long subjectId) throws SQLException {
        Cursor mCursor = mDb.query(true, DATABASE_TABLE_SUBJECT, new String[] {KEY_SUBJECTID+" _id", KEY_NAME, KEY_ABBREVIATION, KEY_PROFESSOR, KEY_CLASSROOM}, KEY_SUBJECTID + "=" + subjectId, null, null, null, null, null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;
    }
    
    /**
     * Return a Cursor positioned at the task that matches the given taskId
     * 
     * @param taskId id of task to retrieve
     * @return Cursor positioned to matching task, if found
     * @throws SQLException if note could not be found/retrieved
     */
    public Cursor fetchTask(long taskId) throws SQLException {
        Cursor mCursor = mDb.query(true, DATABASE_TABLE_TASK, new String[] {KEY_TASKID+" _id", KEY_TYPE, KEY_TITLE, KEY_EXPLANATION, KEY_DATE, KEY_MARK, KEY_REVISION, KEY_REVISIONDATE, KEY_COMPLETED, KEY_FEELINGSSTARS, KEY_FEELINGS, KEY_TASKSUBJECT}, KEY_TASKID + "=" + taskId, null, null, null, null, null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;
    }
    
    /**
     * Return a Cursor over the list of all tasks of a subject
     * 
     * @param subjectId id of subject
     * @return Cursor positioned to matching tasks, if found
     * @throws SQLException if note could not be found/retrieved
     */
    public Cursor fetchAllTasksOfSubject(long subjectId) throws SQLException {
        Cursor mCursor = mDb.query(true, DATABASE_TABLE_TASK, new String[] {KEY_TASKID+ " _id", KEY_TYPE, KEY_TITLE, KEY_EXPLANATION, KEY_DATE, KEY_MARK, KEY_REVISION, KEY_REVISIONDATE, KEY_COMPLETED, KEY_FEELINGSSTARS, KEY_FEELINGS, KEY_TASKSUBJECT}, KEY_TASKSUBJECT + "=" + subjectId, null, null, null, null, null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;
    }
    
    /**
     * Update the subject using the details provided. The subject to be updated is
     * specified using the subjectId, and it is altered to use the values passed in
     * 
     * @param subjectId id of subject to update
     * @param name the name of the subject
     * @param abbreviation the abbreviation of the subject
     * @param professor the professor of the subject
     * @param classroom the classroom of the subject
     * @return true if the subject was successfully updated, false otherwise
     */
    public boolean updateSubject(long subjectId, String name, String abbreviation, String professor, String classroom) {
        ContentValues args = new ContentValues();
        args.put(KEY_NAME, name);
        args.put(KEY_ABBREVIATION, abbreviation);
        args.put(KEY_PROFESSOR, professor);
        args.put(KEY_CLASSROOM, classroom);

        return mDb.update(DATABASE_TABLE_SUBJECT, args, KEY_SUBJECTID + "=" + subjectId, null) > 0;
    }
    
    /**
     * Update the task using the details provided. The task to be updated is
     * specified using the taskId, and it is altered to use the values passed in
     * 
     * @param taskId id of task to update
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
     * @param taskSubject	the subject related to the task
     * @return true if the task was successfully updated, false otherwise
     */
    public boolean updateTask(long taskId, int type, String title, String explanation, String date, double mark, boolean rev, String revisionDate, boolean comp, int feelingsStars, String feelings, long taskSubject) {
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
        if(mark==-1){
        	args.putNull(KEY_MARK);
    	} else{
    		args.put(KEY_MARK, mark);
    	}
        args.put(KEY_REVISION, revision);
        args.put(KEY_REVISIONDATE, revisionDate);
        args.put(KEY_COMPLETED, completed);
        if(feelingsStars==-1){
        	args.putNull(KEY_FEELINGSSTARS);
        } else{
        	args.put(KEY_FEELINGSSTARS, feelingsStars);
        }
        args.put(KEY_FEELINGS, feelings);
        args.put(KEY_TASKSUBJECT, taskSubject);

        return mDb.update(DATABASE_TABLE_TASK, args, KEY_TASKID + "=" + taskId, null) > 0;
    }
}
