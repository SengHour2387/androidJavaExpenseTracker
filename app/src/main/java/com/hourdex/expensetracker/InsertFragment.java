package com.hourdex.expensetracker;

import android.os.Bundle;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.materialswitch.MaterialSwitch;
import com.google.android.material.textfield.TextInputEditText;
import com.hourdex.expensetracker.controllers.TransactionController;
import com.hourdex.expensetracker.database.tables.TransactionTable;

import java.util.Date;

public class InsertFragment extends Fragment {

    private TextInputEditText amountInput, titleInput, descriptionInput;
    private MaterialSwitch typeSwitch;
    private TextView typeText;

    private String selectedCategory = null;

    public InsertFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_insert, container, false);

        amountInput = view.findViewById(R.id.amount);
        titleInput = view.findViewById(R.id.title_input);
        descriptionInput = view.findViewById(R.id.description_input);

        typeText = view.findViewById(R.id.transaction_type);
        typeSwitch = view.findViewById(R.id.type_switch);

        Button saveBtn = view.findViewById(R.id.save_btn);

        Button food = view.findViewById(R.id.btn_food);
        Button work = view.findViewById(R.id.btn_work);
        Button study = view.findViewById(R.id.btn_study);
        Button house = view.findViewById(R.id.btn_house);
        Button transport = view.findViewById(R.id.btn_transport);

        typeSwitch.setOnCheckedChangeListener((b, checked) ->
                typeText.setText(checked ? "Income" : "Outcome")
        );

        View.OnClickListener categoryClick = v -> {
            selectedCategory = ((Button) v).getText().toString();
            Toast.makeText(getContext(),
                    "Selected: " + selectedCategory,
                    Toast.LENGTH_SHORT).show();
        };

        food.setOnClickListener(categoryClick);
        work.setOnClickListener(categoryClick);
        study.setOnClickListener(categoryClick);
        house.setOnClickListener(categoryClick);
        transport.setOnClickListener(categoryClick);

        saveBtn.setOnClickListener(v -> saveTransaction());

        return view;
    }

    private void saveTransaction() {

        if (titleInput.getText() == null ||
        titleInput.getText().toString().trim().isEmpty()) {
            Toast.makeText(getContext(), "Enter title", Toast.LENGTH_SHORT).show();
            return;
        }

        if (amountInput.getText() == null ||
        amountInput.getText().toString().trim().isEmpty()) {
            Toast.makeText(getContext(), "Enter amount", Toast.LENGTH_SHORT).show();
            return;
        }

        if (selectedCategory == null) {
            Toast.makeText(getContext(), "Select category", Toast.LENGTH_SHORT).show();
            return;
        }

        double amount;
        try {
            amount = Double.parseDouble(amountInput.getText().toString());
        } catch (NumberFormatException e) {
            Toast.makeText(getContext(), "Invalid amount", Toast.LENGTH_SHORT).show();
            return;
        }

        String title = titleInput.getText().toString().trim();
        String description = descriptionInput.getText() != null
                ? descriptionInput.getText().toString().trim()
                : "";

        int categoryId = getCategoryId(selectedCategory);

        MainActivity mainActivity = (MainActivity) getActivity();
        if (mainActivity == null) return;

        new Thread(() -> {


            TransactionController controller = new TransactionController(mainActivity);

            boolean isSaved = controller.createTransaction(
                    new TransactionTable(
                            title,
                            typeSwitch.isChecked() ? amount : -amount,
                            categoryId,
                            description,
                            new Date()
                    )
            );
            if(isSaved) {
                requireActivity().runOnUiThread(() ->
                        Toast.makeText(getContext(),
                                "Transaction saved",
                                Toast.LENGTH_SHORT).show()
                );
            } else  {
                requireActivity().runOnUiThread(() ->
                        Toast.makeText(getContext(),
                                "Transaction could not be saved",
                                Toast.LENGTH_SHORT).show()
                );
            }
        }).start();
    }

    private int getCategoryId(String category) {
        switch (category) {
            case "Food": return 1;
            case "Work": return 2;
            case "Study": return 3;
            case "House": return 4;
            case "Transport": return 5;
            default: return 0;
        }
    }
}