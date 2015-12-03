package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl;


import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.DBHandler;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.TransactionDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Account;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Transaction;

/**
 * Created by Nadheesh on 12/3/2015.
 */
public class PersistentTransactionDAO implements TransactionDAO{

    private static final SimpleDateFormat dtFormat = new SimpleDateFormat("yyyy-MM-dd");
    private SQLiteDatabase db;

    public PersistentTransactionDAO(DBHandler db_handler){
        db =db_handler.getWritableDatabase();
    }

    @Override
    public void logTransaction(Date date, String accountNo, ExpenseType expenseType, double amount) {

        String sql = "INSERT INTO expense_info (account_no , date, type , value ) VALUES (?, ?, ? , ?)";
        SQLiteStatement statement = db.compileStatement(sql);

        statement.bindString(1,accountNo);
        statement.bindString(2, dtFormat.format(date));
        statement.bindString(3, expenseType.name());
        statement.bindDouble(4, amount);

        statement.executeInsert();

    }

    @Override
    public List<Transaction> getAllTransactionLogs() {
        ArrayList<Transaction> transactions = new ArrayList<Transaction>();

        Cursor cursor = db.query("expense_info", new String[]{"account_no , date, type , value"}, null , null , null, null, null);

        Transaction transaction;
        while(cursor.moveToNext()){
            try {
                transaction = new Transaction(dtFormat.parse(cursor.getString(1)),cursor.getString(0),ExpenseType.valueOf(cursor.getString(2)),cursor.getDouble(3) );
                transactions.add(transaction);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        return transactions;

    }

    @Override
    public List<Transaction> getPaginatedTransactionLogs(int limit) {

        ArrayList<Transaction> transactions = new ArrayList<Transaction>();

        Cursor cursor = db.query("expense_info", new String[]{"account_no , date, type ,value"}, null , null , null, null, null);

        Transaction transaction;
        while(cursor.moveToNext()){
            try {
                transaction = new Transaction(dtFormat.parse(cursor.getString(1)),cursor.getString(0),ExpenseType.valueOf(cursor.getString(2)),cursor.getDouble(3) );
                transactions.add(transaction);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        int size = transactions.size();

        if (size <= limit) {
            return transactions;
        }

        // return the last <code>limit</code> number of transaction logs
        return transactions.subList(size - limit, size);
    }
}
