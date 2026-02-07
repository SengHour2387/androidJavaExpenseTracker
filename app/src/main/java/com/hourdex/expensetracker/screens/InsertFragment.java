package com.hourdex.expensetracker.screens;

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
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.hourdex.expensetracker.MainActivity;
import com.hourdex.expensetracker.R;
import com.hourdex.expensetracker.controllers.TransactionController;
import com.hourdex.expensetracker.database.tables.CategoryTable;
import com.hourdex.expensetracker.database.tables.TransactionTable;

import androidx.activity.OnBackPressedCallback;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class InsertFragment extends Fragment {

    private TextInputEditText amountInput, titleInput, descriptionInput;
    private MaterialSwitch typeSwitch;
    private TextView typeText;
    private Spinner spinner;
    private Button btnAAddCgr;

    private MainActivity mainActivity;
    private String selectedCategory = null;
    private ArrayAdapter<String> categoryAdapter;
    private List<String> categoryNames = new ArrayList<>();
    private List<CategoryTable> allCategories = new ArrayList<>(); // ← added (small & useful)

    public InsertFragment() {
    }

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

        typeSwitch.setOnCheckedChangeListener((b, checked) -> typeText.setText(checked ? "Income" : "Outcome"));

        btnAAddCgr.setOnClickListener(addCategoryListener());

        saveBtn.setOnClickListener(v -> saveTransaction());

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedCategory = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        categoryNames.add("Select category");
        categoryAdapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_item,
                categoryNames);
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(categoryAdapter);

        getCategories();

        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(),
                new OnBackPressedCallback(true) {
                    @Override
                    public void handleOnBackPressed() {
                        if (hasAnyInput()) {
                            showBackConfirmationDialog();
                        } else {
                            setEnabled(false);
                            requireActivity().getOnBackPressedDispatcher().onBackPressed();
                        }
                    }
                });

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

                    getCategories();

                    requireActivity().runOnUiThread(() -> {
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
        if (mainActivity == null)
            return;

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
        String description = descriptionInput.getText() != null ? descriptionInput.getText().toString().trim() : "";

        int categoryId = getCategoryId(selectedCategory);

        if (categoryId <= 0) {
            Toast.makeText(getContext(), "Invalid category selected", Toast.LENGTH_SHORT).show();
            return;
        }

        MainActivity activity = (MainActivity) getActivity();
        if (activity == null)
            return;

        new Thread(() -> {
            TransactionController controller = new TransactionController(activity);

            TransactionTable transaction = new TransactionTable(
                    title,
                    typeSwitch.isChecked() ? amount : -amount,
                    categoryId,
                    description,
                    new Date());

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

    private boolean hasAnyInput() {
        return (amountInput.getText() != null && !amountInput.getText().toString().trim().isEmpty()) ||
                (titleInput.getText() != null && !titleInput.getText().toString().trim().isEmpty()) ||
                (descriptionInput.getText() != null && !descriptionInput.getText().toString().trim().isEmpty()) ||
                (selectedCategory != null && !selectedCategory.equals("Select category"));
    }

    private boolean isDataValid() {
        if (titleInput.getText() == null || titleInput.getText().toString().trim().isEmpty())
            return false;
        if (amountInput.getText() == null || amountInput.getText().toString().trim().isEmpty())
            return false;
        if (selectedCategory == null || selectedCategory.equals("Select category"))
            return false;

        try {
            Double.parseDouble(amountInput.getText().toString().trim());
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private void showBackConfirmationDialog() {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Please confirm")
                .setMessage("You have unsaved changes. What would you like to do?")
                .setNeutralButton("Cancel", (dialog, which) -> dialog.dismiss())
                .setNegativeButton("Clear", (dialog, which) -> {
                    requireActivity().getSupportFragmentManager().popBackStack();
                });

        if (isDataValid()) {
            builder.setPositiveButton("Save", (dialog, which) -> saveTransaction());
        }
        builder.show();
    }
}