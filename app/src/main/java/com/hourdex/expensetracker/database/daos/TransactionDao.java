package com.hourdex.expensetracker.database.daos;


import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.hourdex.expensetracker.database.tables.TransactionTable;

import java.util.List;

@Dao
public interface TransactionDao {
    @Insert
    void newTransaction( TransactionTable transactionTable );

    @Query("SELECT * FROM transaction_tbl")
    LiveData<List<TransactionTable>> getAll();

    @Query("SELECT * FROM transaction_tbl WHERE amount > 0")
    LiveData<List<TransactionTable>> getIncome();

    @Query("SELECT * FROM transaction_tbl WHERE amount < 0")
    LiveData<List<TransactionTable>> getOutcome();

}
