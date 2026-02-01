package com.hourdex.expensetracker;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.hourdex.expensetracker.database.daos.CategoryDao;
import com.hourdex.expensetracker.database.tables.CategoryTable;
import com.hourdex.expensetracker.database.tables.TransactionTable;

import java.text.SimpleDateFormat;
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

            if(transaction.amount < 0) {
                amountView.setText(String.format(Locale.getDefault(), "%.1f$", transaction.amount).toString());
                amountView.setTextColor(getContext().getResources().getColor(com.google.android.material.R.color.design_default_color_error));
            } else {
                amountView.setText(String.format(Locale.getDefault(), "%.1f$", transaction.amount));
            }

            new Thread(()->{
                String catName = getCategoryName(transaction.category_id);
                ((MainActivity) getContext()).runOnUiThread(() -> {
                    categoryView.setText(catName);
                });
            }).start();
            // Format the date (you'll need to add a timestamp field to your TransactionTable)
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            dateView.setText("Date: " + dateFormat.format(transaction.date) + " at:" + transaction.date.getHours() + ":" + transaction.date.getMinutes());
        }

        return convertView;
    }

    private String getCategoryName(int categoryId) {
        if (categoryId <= 0) return "Uncategorized";

        CategoryDao dao = ((MainActivity) getContext()).getCategoryDao();

        CategoryTable cat = dao.getCategory(categoryId);
        return (cat != null) ? cat.name : "Unknown";
    }
}