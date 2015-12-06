package lk.ac.mrt.cse.dbs.simpleexpensemanager.control;

import android.content.Context;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.AccountDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.DBHandler;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.TransactionDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl.PersistentAccountDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl.PersistentTransactionDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Account;

/**
 * Created by Nadheesh on 12/3/2015.
 * This is the persistent memory expense manager
 */
public class PersistentExpenseManager  extends ExpenseManager{

    Context context ;

    public PersistentExpenseManager(Context contex) {
        this.context = contex;
        setup();
    }

    @Override
    public void setup() {

        //create the DBhandler for the given context
        DBHandler db_handler = new DBHandler(context);

        //assign DAO objects to the expensemanager
        TransactionDAO persistentTransactionDAO = new PersistentTransactionDAO(db_handler);
        setTransactionsDAO(persistentTransactionDAO);

        AccountDAO persistentAccountDAO = new PersistentAccountDAO(db_handler);
        setAccountsDAO(persistentAccountDAO);



        // add dummy data
        Account dummyAcct1 = new Account("12345A", "Yoda Bank", "Anakin Skywalker", 10000.0);
        Account dummyAcct2 = new Account("78945Z", "Clone BC", "Obi-Wan Kenobi", 80000.0);
        getAccountsDAO().addAccount(dummyAcct1);
        getAccountsDAO().addAccount(dummyAcct2);

    }
}
