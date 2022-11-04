package com.syntepro.sueldazo.database;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.syntepro.sueldazo.entity.app.Departamento;
import com.syntepro.sueldazo.entity.app.Pais;
import com.syntepro.sueldazo.entity.app.Provincia;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DataBaseAdapter {

    private static final String TAG = "DataAdapter";

    private SQLiteDatabase mDb;
    private DatabaseHelper mDbHelper;

    public DataBaseAdapter(Context context) {
        mDbHelper = new DatabaseHelper(context);
    }

    public void createDatabase() throws SQLException {
        try {
            mDbHelper.createDataBase();
        } catch (IOException mIOException) {
            Log.e(TAG, mIOException.toString() + "  UnableToCreateDatabase");
            throw new Error("UnableToCreateDatabase");
        }
    }

    public void open() throws SQLException {
        try {
            mDbHelper.openDataBase();
            mDbHelper.close();
            mDb = mDbHelper.getReadableDatabase();
        } catch (SQLException mSQLException) {
            Log.e(TAG, "open >>" + mSQLException.toString());
            throw mSQLException;
        }
    }

    public void close() {
        mDbHelper.close();
    }

    // Get Country Info
    public List getInfoCountry() {
        List<Pais> list = null;

        String sql = "SELECT *  FROM pais;";
        try {
            Cursor c = mDb.rawQuery(sql, null);
            list = new ArrayList<>();
            if (c != null) {
                if (c.moveToFirst()) {
                    do {
                        Pais pais = new Pais(c.getInt(0), c.getString(1), c.getString(2), c.getString(3), c.getString(4), c.getString(5), c.getString(6), c.getString(7), c.getString(8));
                        list.add(pais);
                    } while (c.moveToNext());
                }
            }

        } catch (SQLException mSQLException) {
            Log.e(TAG, "getInfoUser >>" + mSQLException.toString());
            throw mSQLException;
        } finally {
            return list;
        }
    }

    // Get Country
    public Pais getCountrySelected(int idPais) {
        Pais pais = null;

        String sql = "SELECT *  FROM pais WHERE idPais ='" + idPais + "';";
        try {
            Cursor c = mDb.rawQuery(sql, null);
            pais = new Pais();
            if (c != null) {
                if (c.moveToFirst()) {
                    do {
                        pais = new Pais(c.getInt(0), c.getString(1), c.getString(2), c.getString(3), c.getString(4), c.getString(5), c.getString(6), c.getString(7), c.getString(8));
                    } while (c.moveToNext());
                }
            }
        } catch (SQLException mSQLException) {
            Log.e(TAG, "getCountry >>" + mSQLException.toString());
            throw mSQLException;
        } finally {
            return pais;
        }
    }

    // Get Country Configuration
    public Pais getCountryConfiguration(String nombre) {
        Pais pais = null;

        String sql = "SELECT *  FROM pais WHERE nombre ='" + nombre + "';";
        try {
            Cursor c = mDb.rawQuery(sql, null);
            pais = new Pais();
            if (c != null) {
                if (c.moveToFirst()) {
                    do {
                        pais = new Pais(c.getInt(0), c.getString(1), c.getString(2), c.getString(3), c.getString(4), c.getString(5), c.getString(6), c.getString(7), c.getString(8));
                    } while (c.moveToNext());
                }
            }
        } catch (SQLException mSQLException) {
            Log.e(TAG, "getCountry >>" + mSQLException.toString());
            throw mSQLException;
        } finally {
            return pais;
        }
    }

    // Get Country by ABR
    public Pais getCountryInfoAbr(String abr) {
        Pais pais = null;

        String sql = "SELECT *  FROM pais WHERE abreviacion ='" + abr + "';";
        try {
            Cursor c = mDb.rawQuery(sql, null);
            pais = new Pais();
            if (c != null) {
                if (c.moveToFirst()) {
                    do {
                        pais = new Pais(c.getInt(0), c.getString(1), c.getString(2), c.getString(3), c.getString(4), c.getString(5), c.getString(6), c.getString(7), c.getString(8));
                    } while (c.moveToNext());
                }
            }
        } catch (SQLException mSQLException) {
            Log.e(TAG, "getCountry >>" + mSQLException.toString());
            throw mSQLException;
        } finally {
            return pais;
        }
    }


    // Get Department Info
    public List getInfoDepto(int idPais) {
        List<Departamento> list = null;

        String sql = "SELECT *  FROM departamento WHERE idPais ='" + idPais + "';";
        try {
            Cursor c = mDb.rawQuery(sql, null);
            list = new ArrayList<>();
            if (c != null) {
                if (c.moveToFirst()) {
                    do {
                        Departamento depto = new Departamento(c.getInt(0), c.getInt(1), c.getString(2), "");
                        list.add(depto);
                    } while (c.moveToNext());
                }
            }

        } catch (SQLException mSQLException) {
            Log.e(TAG, "getInfoUser >>" + mSQLException.toString());
            throw mSQLException;
        } finally {
            return list;
        }
    }

    // Get Province Info
    public List getInfoPrv(int idDepto) {
        List<Provincia> list = null;

        String sql = "SELECT *  FROM provincia WHERE idDepto ='" + idDepto + "';";
        try {
            Cursor c = mDb.rawQuery(sql, null);
            list = new ArrayList<>();
            if (c != null) {
                if (c.moveToFirst()) {
                    do {
                        Provincia prv = new Provincia(c.getInt(0), c.getInt(1), c.getString(2));
                        list.add(prv);
                    } while (c.moveToNext());
                }
            }

        } catch (SQLException mSQLException) {
            Log.e(TAG, "getInfoUser >>" + mSQLException.toString());
            throw mSQLException;
        } finally {
            return list;
        }
    }

}
