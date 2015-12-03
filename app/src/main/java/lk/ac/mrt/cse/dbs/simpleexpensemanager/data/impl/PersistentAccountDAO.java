package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.Constants;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.AccountDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.DBHandler;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.exception.InvalidAccountException;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Account;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.content.Context;
import android.database.sqlite.SQLiteStatement;

/**
 * Created by Nadheesh on 12/3/2015.
 */
public class PersistentAccountDAO implements AccountDAO {

    private SQLiteDatabase db;


    public PersistentAccountDAO(DBHandler db_handler){

        db =db_handler.getWritableDatabase();
    }

    @Override
    public List<String> getAccountNumbersList() {
        List <String> ac_no = new ArrayList<>();

        Cursor cursor = db.query("account", new String[]{"account_no"}, null, null , null, null, null);

        while(cursor.moveToNext()){
            ac_no.add(cursor.getString(0));
            System.out.println(cursor.getString(0));
        }

        return ac_no;
    }

    @Override
    public List<Account> getAccountsList() {

        return new ArrayList<Account>();
    }

    @Override
    public Account getAccount(String accountNo) throws InvalidAccountException {
        Account account = null;

        Cursor cursor = db.query("account", new String[]{"bank_name, ac_holder, balance"},"account_no = ?" ,new String[] { accountNo} , null, null, null);

        while(cursor.moveToNext()){
            account = new Account(accountNo,cursor.getString(0),cursor.getString(1),cursor.getDouble(2));
        }
        return account;
    }

    @Override
    public void addAccount(Account account) {

        Cursor cursor = db.query("account", new String[]{"account_no"}, account.getAccountNo(), null , null, null, null);

        if (!cursor.moveToNext()) {
            String sql = "INSERT INTO account (account_no, bank_name, ac_holder, balance) VALUES (?, ?, ?, ?)";
            SQLiteStatement statement = db.compileStatement(sql);

            statement.bindString(1, account.getAccountNo());
            statement.bindString(2, account.getBankName());
            statement.bindString(3, account.getAccountHolderName());
            statement.bindDouble(4, account.getBalance());

            statement.executeInsert();
        }else{
           //implement error condition;
        }
    }

    @Override
    public void removeAccount(String accountNo) throws InvalidAccountException {

    }

    @Override
    public void updateBalance(String accountNo, ExpenseType expenseType, double amount) throws InvalidAccountException {

        Cursor cursor = db.query("account", new String[]{"balance"}, " account_no = ? ", new String[]{accountNo}, null, null, null, null);

        if (!cursor.moveToNext()) {
            String msg = "Account " + accountNo + " is invalid.";
            throw new InvalidAccountException(msg);
        }
        else {

            double balance = cursor.getDouble(0);

            // specific implementation based on the transaction type
            switch (expenseType) {
                case EXPENSE:
                    balance -= amount;
                    break;
                case INCOME:
                    balance += amount;
                    break;
            }

            //starting to update the table

            String sql = "UPDATE account SET balance=? WHERE account_no=?";
            SQLiteStatement statement = db.compileStatement(sql);


            statement.bindDouble(1, balance);
            statement.bindString(2, accountNo);

            statement.executeUpdateDelete();
        }
    }
}
