package com.hourdex.expensetracker.repos;

import androidx.lifecycle.LiveData;

import com.hourdex.expensetracker.database.ExpenseRoom;
import com.hourdex.expensetracker.database.tables.TransactionTable;

import java.util.List;

public class transactionRepo {

    final ExpenseRoom room;

    public transactionRepo( ExpenseRoom room ) {
        this.room = room;
    }

    public LiveData<List<TransactionTable>> transactionTableList() {
        return room.transactionDao().getAll();
    }
    public LiveData<List<TransactionTable>> incomeList() {
        return room.transactionDao().getIncome();
    }

    public LiveData<List<TransactionTable>> outcomeList() {
        return room.transactionDao().getOutcome();
    }

}
