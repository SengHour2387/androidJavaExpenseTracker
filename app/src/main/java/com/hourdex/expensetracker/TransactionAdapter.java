package com.hourdex.expensetracker;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.hourdex.expensetracker.database.tables.TransactionTable;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class TransactionAdapter extends ArrayAdapter<TransactionTable> {

    public TransactionAdapter(@NonNull Context context, @NonNull List<TransactionTable> transactions) {
        super(context, 0, transactions);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_transaction, parent, false);
        }

        TransactionTable transaction = getItem(position);

        if (transaction != null) {
            TextView titleView = convertView.findViewById(R.id.transaction_title);
            TextView amountView = convertView.findViewById(R.id.transaction_amount);
            TextView categoryView = convertView.findViewById(R.id.transaction_category);
            TextView dateView = convertView.findViewById(R.id.transaction_date);

            titleView.setText(transaction.label);
            amountView.setText(String.format(Locale.getDefault(), "%.1f$", transaction.amount));
            categoryView.setText(getCategoryName(transaction.category_id));

            // Format the date (you'll need to add a timestamp field to your TransactionTable)
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            dateView.setText("Date: " + dateFormat.format(new Date()));
        }

        return convertView;
    }

    private String getCategoryName(int categoryId) {
        switch (categoryId) {
            case 1: return "Food";
            case 2: return "Work";
            case 3: return "Study";
            case 4: return "House";
            case 5: return "Transport";
            default: return "Other";
        }
    }
}