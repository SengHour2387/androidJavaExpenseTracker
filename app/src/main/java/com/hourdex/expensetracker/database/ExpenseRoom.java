package com.hourdex.expensetracker.database;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.hourdex.expensetracker.database.daos.BudgetDao;
import com.hourdex.expensetracker.database.daos.TransactionDao;
import com.hourdex.expensetracker.database.tables.BudgetTable;
import com.hourdex.expensetracker.database.tables.TransactionTable;

@Database(entities = {TransactionTable.class, BudgetTable.class}, version = 1)
public abstract class ExpenseRoom extends RoomDatabase {
    public abstract TransactionDao transactionDao();
    public abstract BudgetDao budgetDao();
}
