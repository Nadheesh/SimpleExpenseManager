package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl;


import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.Constants;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.DBHandler;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.TransactionDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Transaction;

/**
 * Created by Nadheesh on 12/3/2015.
 * This is an Persistent-Storage implementation of the TransactionDAO interface. A sqlite database is
 * used to store the account details as a storage.
 */
public class PersistentTransactionDAO implements TransactionDAO{


    private static final SimpleDateFormat dtFormat = new SimpleDateFormat("yyyy-MM-dd");
    private SQLiteDatabase db; //every method will open a readable / writable database according to its needs
    private DBHandler dbHandler ;

    public PersistentTransactionDAO(DBHandler db_handler){

       this.dbHandler = db_handler;
    }



    @Override
    //insert to transaction into the database
    public void logTransaction(Date date, String accountNo, ExpenseType expenseType, double amount) {
        db = dbHandler.getWritableDatabase();


        String sql = "INSERT INTO " + Constants.TRANSACTION_LOG + " ( " + Constants.ACCOUNT_NO + " , " + Constants.DATE + ", " + Constants.EXPENSE_TYPE + " , " + Constants.AMOUNT + " ) VALUES (?, ?, ? , ?)";
        SQLiteStatement statement = db.compileStatement(sql);

        //bind values to the sqlite statement
        statement.bindString(1,accountNo.trim().toUpperCase());
        statement.bindString(2, dtFormat.format(date));
        statement.bindString(3, expenseType.name());
        statement.bindDouble(4, amount);

        statement.executeInsert();
        statement.close();

        db.close();

    }

    @Override
    //get all the transactions in the database
    public List<Transaction> getAllTransactionLogs() {

        db = dbHandler.getReadableDatabase();
        ArrayList<Transaction> transactions = new ArrayList<Transaction>();

        Cursor cursor = db.query(Constants.TRANSACTION_LOG, new String[]{Constants.ACCOUNT_NO, Constants.DATE , Constants.EXPENSE_TYPE  , Constants.AMOUNT } , null , null , null, null, null);

        Transaction transaction;
        while(cursor.moveToNext()){//check if another transaction exists if exists then return its data to a Transaction object
            try {
                transaction = new Transaction(dtFormat.parse(cursor.getString(1)),cursor.getString(0),ExpenseType.valueOf(cursor.getString(2)),cursor.getDouble(3) );
                transactions.add(transaction);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        cursor.close();
        db.close();
        return transactions;

    }

    @Override
    //get limited amount of most recently added transaction logs
    public List<Transaction> getPaginatedTransactionLogs(int limit) {

        db = dbHandler.getReadableDatabase();

        ArrayList<Transaction> transactions = new ArrayList<>();
        //returns a limited number of transactions
        Cursor cursor = db.query( Constants.TRANSACTION_LOG, new String[]{ Constants.ACCOUNT_NO , Constants.DATE , Constants.EXPENSE_TYPE  , Constants.AMOUNT}, null , null , null, null,  " t_id "+ " DESC ", String.valueOf(limit));

        Transaction transaction;
        cursor.moveToLast();

        do{
            try {

                transaction = new Transaction(dtFormat.parse(cursor.getString(1)),cursor.getString(0),ExpenseType.valueOf(cursor.getString(2)),cursor.getDouble(3) );
                transactions.add(transaction);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }while(cursor.moveToPrevious());

        int size = transactions.size();

        //make sure that the returned no of transactions are correct. if not remove part of array list
        if (size <= limit) {
            db.close();
            return transactions;
        }
        cursor.close();
        db.close();
        // return the last <code>limit</code> number of transaction logs
        return transactions.subList(size - limit, size);
    }
}
