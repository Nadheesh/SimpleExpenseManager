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
 * Created by Nadheesh on 12/3/2015./**
 * This is an Persistent-Storage implementation of the AccountDAO interface. A sqlite database is
 * used to store the account details as a storage.
 */
public class PersistentAccountDAO implements AccountDAO {

    private SQLiteDatabase db;
    private DBHandler dbHandler ;


    public PersistentAccountDAO(DBHandler db_handler){

        this.dbHandler = db_handler;
    }

    @Override
    public List<String> getAccountNumbersList() {
        db =dbHandler.getReadableDatabase();
        List <String> ac_no = new ArrayList<>();

        Cursor cursor = db.query(Constants.ACCOUNT_TABLE, new String[]{Constants.ACCOUNT_NO}, null, null , null, null, null);

        while(cursor.moveToNext()){
            ac_no.add(cursor.getString(0));
        }
        cursor.close();
        db.close();
        return ac_no;

    }

    @Override
    public List<Account> getAccountsList() {
        ArrayList<Account> accounts = new ArrayList<>();

        db =dbHandler.getReadableDatabase();

        Account account = null;

        Cursor cursor = db.query(Constants.ACCOUNT_TABLE, new String[]{Constants.BANK_NAME, Constants.ACCOUNT_HOLDER, Constants.BALANCE , Constants.ACCOUNT_NO}, null ,null , null, null, null);

        while(cursor.moveToNext()){
            account = new Account(cursor.getString(3),cursor.getString(0),cursor.getString(1),cursor.getDouble(2));
            accounts.add(account);
        }

        cursor.close();
        db.close();
        return accounts;
    }

    @Override
    public Account getAccount(String accountNo) throws InvalidAccountException {

        db =dbHandler.getReadableDatabase();

        Account account = null;

        Cursor cursor = db.query(Constants.ACCOUNT_TABLE, new String[]{Constants.BANK_NAME, Constants.ACCOUNT_HOLDER, Constants.BALANCE},Constants.ACCOUNT_NO+" = ?" ,new String[] { accountNo} , null, null, null);

        if(cursor.moveToNext()){
            account = new Account(accountNo,cursor.getString(0),cursor.getString(1),cursor.getDouble(2));
        }
        cursor.close();
        db.close();
        return account;
    }

    @Override
    public void addAccount(Account account) {

        db =dbHandler.getWritableDatabase();

        Cursor cursor = db.query(Constants.ACCOUNT_TABLE, new String[]{Constants.ACCOUNT_NO}, Constants.ACCOUNT_NO+" = ?" ,new String[] { account.getAccountNo().toUpperCase()}, null , null, null, null);

        if (!cursor.moveToNext()) {
            String sql = "INSERT INTO " +Constants.ACCOUNT_TABLE + " ( "+ Constants.ACCOUNT_NO +", "+ Constants.BANK_NAME + ", "+ Constants.ACCOUNT_HOLDER +", "+ Constants.BALANCE +") VALUES (?, ?, ?, ?)";
            SQLiteStatement statement = db.compileStatement(sql);

            statement.bindString(1, account.getAccountNo().toUpperCase());
            statement.bindString(2, account.getBankName());
            statement.bindString(3, account.getAccountHolderName());
            statement.bindDouble(4, account.getBalance());

            statement.executeInsert();
            statement.close();
        }else{
           //implement error condition;
        }
        cursor.close();
        db.close();
    }

    @Override
    public void removeAccount(String accountNo) throws InvalidAccountException {

        db =dbHandler.getWritableDatabase();

        String sql = "DELETE FROM account WHERE account_no = ?";

        SQLiteStatement statement = db.compileStatement(sql);

        statement.bindString(1, accountNo);

        statement.executeUpdateDelete();
        statement.close();

        db.close();
    }

    @Override
    public void updateBalance(String accountNo, ExpenseType expenseType, double amount) throws InvalidAccountException {

        db =dbHandler.getWritableDatabase();

        Cursor cursor = db.query(Constants.ACCOUNT_TABLE , new String[]{ Constants.BALANCE }, Constants.ACCOUNT_NO +" = ? ", new String[]{accountNo}, null, null, null, null);

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

            String sql = "UPDATE "+ Constants.ACCOUNT_TABLE +" SET "+ Constants.BALANCE +"=? WHERE "+Constants.ACCOUNT_NO+"=?";
            SQLiteStatement statement = db.compileStatement(sql);


            statement.bindDouble(1, balance);
            statement.bindString(2, accountNo);

            statement.executeUpdateDelete();
            statement.close();

        }
        cursor.close();
        db.close();
    }
}
