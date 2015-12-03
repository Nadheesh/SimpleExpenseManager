package lk.ac.mrt.cse.dbs.simpleexpensemanager.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Nadheesh on 12/3/2015.
 */
public class DBHandler extends SQLiteOpenHelper {

    public DBHandler(Context context){
        super(context, "130257V" , null, 2);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {

        //db = SQLiteDatabase.openOrCreateDatabase(Constants.db_name, null);
        //db = SQLiteDatabase.openDatabase( "/ data / data / com.pack.store / databases / "+ Constants.db_name, null,SQLiteDatabase.CREATE_IF_NECESSARY);
        //db = SQLiteDatabase.openDatabase("asdsa" , null, SQLiteDatabase.CONFLICT_ROLLBACK );

        db.execSQL("CREATE TABLE IF NOT EXISTS account " +
                "( " +
                " account_no CHAR(20), " +
                " bank_name TEXT NOT NULL, " +
                " ac_holder TEXT NOT NULL, " +
                " balance NUMERIC(10,2) DEFAULT 0, " +
                " PRIMARY KEY(account_no) " +
                ");");

        db.execSQL("CREATE TABLE IF NOT EXISTS expense_info " +
                "( " +
                " t_id int AUTOINCREMENT, " +
                " account_no CHAR(20), " +
                " type CHAR(7) NOT NULL,  " +
                " date DATE NOT NULL, " +
                " value NUMERIC(10,2) DEFAULT 0, " +
                " PRIMARY KEY(t_id), " +
                " FOREIGN KEY (account_no) REFERENCES account DELETE ON CASCADE UPDATE ON CASCADE, " +
                " CHECK (type IN ('EXPENSE' , 'INCOME'))" +
                ");");


    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
