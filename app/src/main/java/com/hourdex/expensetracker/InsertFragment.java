package com.hourdex.expensetracker;

import android.os.Bundle;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.materialswitch.MaterialSwitch;
import com.google.android.material.textfield.TextInputEditText;
import com.hourdex.expensetracker.controllers.TransactionController;
import com.hourdex.expensetracker.database.daos.CategoryDao;
import com.hourdex.expensetracker.database.tables.CategoryTable;
import com.hourdex.expensetracker.database.tables.TransactionTable;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class InsertFragment extends Fragment {

    private TextInputEditText amountInput, titleInput, descriptionInput;
    private MaterialSwitch typeSwitch;
    private TextView typeText;
    private Spinner spinner;
    private ImageButton btnAAddCgr;

    private MainActivity mainActivity;
    private String selectedCategory = null;
    private ArrayAdapter<String> categoryAdapter;
    private List<String> categoryNames = new ArrayList<>();
    private List<CategoryTable> allCategories = new ArrayList<>();  // ← added (small & useful)

    public InsertFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_insert, container, false);

        mainActivity = (MainActivity) getActivity();

        amountInput = view.findViewById(R.id.amount);
        titleInput = view.findViewById(R.id.title_input);
        descriptionInput = view.findViewById(R.id.description_input);
        spinner = view.findViewById(R.id.category_spinner);

        typeText = view.findViewById(R.id.transaction_type);
        typeSwitch = view.findViewById(R.id.type_switch);
        btnAAddCgr = view.findViewById(R.id.btnAddCat);

        Button saveBtn = view.findViewById(R.id.save_btn);

        typeSwitch.setOnCheckedChangeListener((b, checked) ->
                typeText.setText(checked ? "Income" : "Outcome")
        );

        btnAAddCgr.setOnClickListener(addCategoryListener());

        saveBtn.setOnClickListener(v -> saveTransaction());

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedCategory = parent.getItemAtPosition(position).toString();
            }

            @Override public void onNothingSelected(AdapterView<?> adapterView) {}
        });

        // Initial setup
        categoryNames.add("Select category");
        categoryAdapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_item,
                categoryNames
        );
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(categoryAdapter);

        getCategories();  // load once on start

        return view;
    }

    private View.OnClickListener addCategoryListener() {
        return v -> {
            BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(requireContext());
            bottomSheetDialog.setContentView(R.layout.add_category_btm_modal);

            Button btnCreate = bottomSheetDialog.findViewById(R.id.btn_create);
            TextInputEditText categoryNameInput = bottomSheetDialog.findViewById(R.id.category_name_input);

            btnCreate.setOnClickListener(btn -> {
                String name = categoryNameInput.getText().toString().trim();
                if (name.isEmpty()) {
                    Toast.makeText(requireContext(), "Enter category name", Toast.LENGTH_SHORT).show();
                    return;
                }

                new Thread(() -> {
                    CategoryTable category = new CategoryTable(name);
                    long newId = mainActivity.getCategoryDao().insertCategory(category);

                    // Reload categories (this also updates allCategories)
                    getCategories();

                    requireActivity().runOnUiThread(() -> {
                        // Try to auto-select the newly added category
                        int position = categoryNames.indexOf(name);
                        if (position >= 0) {
                            spinner.setSelection(position);
                            selectedCategory = name;
                        }
                        Toast.makeText(requireContext(), "Category added", Toast.LENGTH_SHORT).show();
                    });
                }).start();

                bottomSheetDialog.dismiss();
            });

            bottomSheetDialog.show();
        };
    }

    private void getCategories() {
        if (mainActivity == null) return;

        new Thread(() -> {
            List<CategoryTable> categories = mainActivity.getCategoryDao().getAll();

            allCategories.clear();
            allCategories.addAll(categories);

            categoryNames.clear();
            categoryNames.add("Select category");

            for (CategoryTable c : categories) {
                categoryNames.add(c.name);
            }

            requireActivity().runOnUiThread(() -> {
                categoryAdapter.notifyDataSetChanged();
                // Optional: keep "Select category" selected after refresh
                spinner.setSelection(0);
            });
        }).start();
    }

    private void saveTransaction() {
        if (titleInput.getText() == null || titleInput.getText().toString().trim().isEmpty()) {
            Toast.makeText(getContext(), "Enter title", Toast.LENGTH_SHORT).show();
            return;
        }

        if (amountInput.getText() == null || amountInput.getText().toString().trim().isEmpty()) {
            Toast.makeText(getContext(), "Enter amount", Toast.LENGTH_SHORT).show();
            return;
        }

        if (selectedCategory == null || selectedCategory.equals("Select category")) {
            Toast.makeText(getContext(), "Select category", Toast.LENGTH_SHORT).show();
            return;
        }

        double amount;
        try {
            amount = Double.parseDouble(amountInput.getText().toString().trim());
        } catch (NumberFormatException e) {
            Toast.makeText(getContext(), "Invalid amount", Toast.LENGTH_SHORT).show();
            return;
        }

        String title = titleInput.getText().toString().trim();
        String description = descriptionInput.getText() != null ?
                descriptionInput.getText().toString().trim() : "";

        int categoryId = getCategoryId(selectedCategory);

        if (categoryId <= 0) {
            Toast.makeText(getContext(), "Invalid category selected", Toast.LENGTH_SHORT).show();
            return;
        }

        MainActivity activity = (MainActivity) getActivity();
        if (activity == null) return;

        new Thread(() -> {
            TransactionController controller = new TransactionController(activity);

            TransactionTable transaction = new TransactionTable(
                    title,
                    typeSwitch.isChecked() ? amount : -amount,
                    categoryId,
                    description,
                    new Date()
            );

            boolean isSaved = controller.createTransaction(transaction);

            requireActivity().runOnUiThread(() -> {
                if (isSaved) {
                    Toast.makeText(getContext(), "Transaction saved", Toast.LENGTH_SHORT).show();
                    requireActivity().getSupportFragmentManager().popBackStack();
                } else {
                    Toast.makeText(getContext(), "Failed to save transaction", Toast.LENGTH_SHORT).show();
                }
            });
        }).start();
    }

    private int getCategoryId(String categoryName) {
        if (categoryName == null || categoryName.equals("Select category")) {
            return 0;
        }

        for (CategoryTable cat : allCategories) {
            if (cat.name.equals(categoryName)) {
                return cat.id;
            }
        }
        return 0;
    }
}