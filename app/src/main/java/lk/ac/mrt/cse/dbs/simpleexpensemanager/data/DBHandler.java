package lk.ac.mrt.cse.dbs.simpleexpensemanager.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.Constants;

/**
 * Created by Nadheesh on 12/3/2015.
 */
public class DBHandler extends SQLiteOpenHelper {

    public DBHandler(Context context){
        super(context, Constants.db_name , null, 3);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {

        //db = SQLiteDatabase.openOrCreateDatabase(Constants.db_name, null);
        //db = SQLiteDatabase.openDatabase( "/ data / data / com.pack.store / databases / "+ Constants.db_name, null,SQLiteDatabase.CREATE_IF_NECESSARY);
        //db = SQLiteDatabase.openDatabase("asdsa" , null, SQLiteDatabase.CONFLICT_ROLLBACK );

        //db.execSQL( " DROP TABLE account ;");
        //db.execSQL( " DROP TABLE "+ Constants.TRANSACTION_LOG + " ;");

        db.execSQL("CREATE TABLE IF NOT EXISTS " + Constants.ACCOUNT_TABLE +
                "( " +
                Constants.ACCOUNT_NO + " CHAR(20), " +
                Constants.BANK_NAME+ " TEXT NOT NULL, " +
                Constants.ACCOUNT_HOLDER + " TEXT NOT NULL, " +
                Constants.BALANCE + " NUMERIC(10,2) DEFAULT 0, " +
                " PRIMARY KEY("+ Constants.ACCOUNT_NO +") " +
                ");");

        db.execSQL("CREATE TABLE IF NOT EXISTS " + Constants.TRANSACTION_LOG +
                "( " +
                " t_id int AUTO_INCREMENT, " +
                Constants.ACCOUNT_NO + " CHAR(20), " +
                Constants.EXPENSE_TYPE + " CHAR(7) NOT NULL, " +
                Constants.DATE + " DATE NOT NULL, " +
                Constants.AMOUNT + " NUMERIC(10,2) DEFAULT 0, " +
                " PRIMARY KEY(t_id), " +
                " FOREIGN KEY ( " + Constants.ACCOUNT_NO + ") REFERENCES account, " +
                " CHECK (type IN ('EXPENSE' , 'INCOME'))" +
                ");");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
