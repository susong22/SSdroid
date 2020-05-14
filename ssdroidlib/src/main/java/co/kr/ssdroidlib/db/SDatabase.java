package co.kr.ssdroidlib.db;

import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

/**
 * Created By hhsong 2020.05.12
 */
public class SDatabase {
    private SQLiteDatabase mDB;
    public SDatabase()
    {
    }

    @Override
    protected void finalize() throws Throwable {
        Close();
        super.finalize();
    }

    public  boolean IsOpened()
    {
        if(mDB != null) return true;
        return false;
    }

    public void Close()
    {
        if(mDB != null)
        {
            try {
                mDB.close();
                mDB = null;
            }
            catch (SQLException e)
            {
                e.printStackTrace();
                Log.e("JavaSong","DB Close " + e.getLocalizedMessage());
            }
        }
    }

    public void BeginTransaction()
    {
        try {
            mDB.beginTransaction();;
        }
        catch (SQLException e)
        {
            e.printStackTrace();
            Log.e("JavaSong","DB BeginTransaction " + e.getLocalizedMessage());
        }
    }

    public void Commit()
    {
        try {
            mDB.setTransactionSuccessful();
        }
        catch (SQLException e)
        {
            e.printStackTrace();
            Log.e("JavaSong","DB Commit " + e.getLocalizedMessage());
        }
    }

    public void Rollback() {
        try {
            mDB.endTransaction();
        } catch (SQLException e) {
            e.printStackTrace();
            Log.e("JavaSong","DB Rollback " + e.getLocalizedMessage());
        }
    }

    public boolean Open(String stPathToDB)
    {
        try
        {
            mDB = SQLiteDatabase.openOrCreateDatabase(stPathToDB,null);
        }
        catch (SQLException e)
        {

            e.printStackTrace();
            Log.e("JavaSong","DB Open " + e.getLocalizedMessage());
            return false;
        }
        return true;
    }

    public Cursor Query(String sSQL)
    {
        Cursor rc = null;
        try
        {
            rc = mDB.rawQuery(sSQL,null);
        }
        catch (SQLException e)
        {
            e.printStackTrace();
            Log.e("JavaSong","DB Query " + e.getLocalizedMessage());
        }
        return rc;
    }



    public long QueryCount(String sSQL){

        long count = 0;
        try
        {
            SQLiteStatement s = mDB.compileStatement( sSQL);
            count = s.simpleQueryForLong();

        }
        catch (SQLException e)
        {
            e.printStackTrace();
            Log.e("JavaSong","DB QueryCount " + e.getLocalizedMessage());
        }
        return count;
    }

    //Create Table을 하려면  create table if not exists TableNam ()
    public boolean Execute(String sSQL)
    {
        try
        {
            mDB.execSQL(sSQL);
        }
        catch (SQLException e)
        {
            e.printStackTrace();
            Log.e("JavaSong","DB Execute " + e.getLocalizedMessage());
            return false;
        }
        return true;
    }
}
