package com.appbenefy.sueldazo.database;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 4;
    private static String DB_PATH = "";
    private static String DB_NAME = "benefy.db";
    private final Context mContext;
    private SQLiteDatabase mDataBase;

    DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DATABASE_VERSION);
        this.mContext = context;
        DB_PATH = context.getApplicationInfo().dataDir + "/databases/";
    }

    void createDataBase() throws IOException {
        //If the database does not exist, copy it from the assets.
        boolean mDataBaseExist = checkDataBase();
        if(!mDataBaseExist) {
            this.getReadableDatabase();
            this.close();
            try {
                copyDataBase();
                Log.e("DataBaseHelper Class", "createDatabase database created");
            } catch (IOException mIOException) {
                mIOException.printStackTrace();
                throw new Error("ErrorCopyingDataBase");
            }
        }
    }

    // Check that the database exists here: /data/data/your package/databases/Da Name
    private boolean checkDataBase() {
        File dbFile = new File(DB_PATH + DB_NAME);
        Log.e("dbFile", dbFile + "   " + dbFile.exists());
        return dbFile.exists();
    }

    // Copy the database from assets
    private void copyDataBase() throws IOException {
        InputStream mInput = mContext.getAssets().open(DB_NAME);
        String outFileName = DB_PATH + DB_NAME;
        OutputStream mOutput = new FileOutputStream(outFileName);
        byte[] mBuffer = new byte[1024];
        int mLength;
        while ((mLength = mInput.read(mBuffer)) > 0) {
            mOutput.write(mBuffer, 0, mLength);
        }
        mOutput.flush();
        mOutput.close();
        mInput.close();
    }

    // Open the database, so we can query it
    boolean openDataBase() throws SQLException {
        String mPath = DB_PATH + DB_NAME;
        mDataBase = SQLiteDatabase.openDatabase(mPath, null, SQLiteDatabase.CREATE_IF_NECESSARY);
        //mDataBase = SQLiteDatabase.openDatabase(mPath, null, SQLiteDatabase.NO_LOCALIZED_COLLATORS);
        return mDataBase != null;
    }

    @Override
    public synchronized void close() {
        if(mDataBase != null)
            mDataBase.close();
        super.close();
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.e("DataBaseHelper", "Updating table from " + oldVersion + " to " + newVersion);
        if (oldVersion == 1) {
            File myDb = mContext.getDatabasePath("beneficios.db");
            boolean isDelete = myDb.delete();
            if (isDelete) { Log.e("Database", "Ok Delete"); } else { Log.e("Database", "Error Delete"); }
        } else if (oldVersion == 2) {
            File myDb = mContext.getDatabasePath("beneficios.db");
            boolean isDelete = myDb.delete();
            if (isDelete) { Log.e("Database", "Ok Delete"); } else { Log.e("Database", "Error Delete"); }
        }

//        Log.e("DataBase", "New: " + newVersion + "Old: " + oldVersion);
//        if(newVersion > oldVersion) {
//            try {
//                File myDb = mContext.getDatabasePath(DB_NAME);
//                boolean isDelete = myDb.delete();
//                if (isDelete) {
//                    this.getReadableDatabase();
//                    this.close();
//                    copyDataBase();
//                }
//            } catch (IOException e) {
//                Log.e("Exception", "OnUpgrade Version");
//                e.printStackTrace();
//            }
//        }
    }

}

