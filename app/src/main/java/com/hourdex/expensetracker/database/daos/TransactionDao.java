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

    @Query("SELECT * FROM transaction_tbl ORDER BY date DESC")
    LiveData<List<TransactionTable>> getAll();

    @Query("SELECT * FROM transaction_tbl WHERE amount > 0")
    LiveData<List<TransactionTable>> getIncome();

    @Query("SELECT * FROM transaction_tbl WHERE amount < 0")
    LiveData<List<TransactionTable>> getOutcome();

    @Query("SELECT COUNT(*) FROM transaction_tbl WHERE category_id = :categoryId ")
    int getTransactionCountByCategory( int categoryId );

    @Query("SELECT SUM(amount) FROM transaction_tbl WHERE category_id = :categoryId AND amount > 0 ")
    Double getTransactionIncomeByCategory(int categoryId );

    @Query("SELECT SUM(amount) FROM transaction_tbl WHERE category_id = :categoryId AND amount < 0 ")
    Double getTransactionOutcomeByCategory(int categoryId );



}
