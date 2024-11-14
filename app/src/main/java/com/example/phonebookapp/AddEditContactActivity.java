package com.example.phonebookapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class AddEditContactActivity extends AppCompatActivity {

    private EditText editTextName, editTextPhone;
    private Button buttonSave, buttonDelete;
    private DatabaseHelper dbHelper;
    private int contactId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_contact);

        // Initialize TextInputLayout and TextInputEditText views
//        TextInputLayout nameInputLayout = findViewById(R.id.text_input_layout);
//        TextInputLayout phoneInputLayout = findViewById(R.id.text_input_layout2);

        editTextName = findViewById(R.id.editTextName);
        editTextPhone = findViewById(R.id.editTextPhone);

        buttonSave = findViewById(R.id.buttonSave);
        buttonDelete = findViewById(R.id.buttonDelete);
        dbHelper = new DatabaseHelper(this);





        Intent intent = getIntent();
        if (intent.hasExtra("contact_id")) {
            contactId = intent.getIntExtra("contact_id", -1);
            String contactName = intent.getStringExtra("contact_name");
            String contactPhone = intent.getStringExtra("contact_phone");

            // Pre-populate the fields with the contact details
            editTextName.setText(contactName);
            editTextPhone.setText(contactPhone);

            // Show the delete button if editing an existing contact
            buttonDelete.setVisibility(View.VISIBLE);
        }

        // Save Button Click Listener
        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (contactId == -1) {
                    saveContact();
                } else {
                    updateContact();
                }
            }
        });

        // Delete Button Click Listener
        buttonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteContact();
            }
        });
    }

    // Method to save a new contact
    private void saveContact() {
        String name = editTextName.getText().toString().trim();
        String phone = editTextPhone.getText().toString().trim();

        if (name.isEmpty() || phone.isEmpty()) {
            Toast.makeText(this, "Please enter both name and phone number", Toast.LENGTH_SHORT).show();
            return;
        }

        // Check if the phone number already starts with +91, else add it
        if (!phone.startsWith("+91")) {
            phone = "+91 " + phone;
        }

        // Check if the contact already exists
        if (dbHelper.isContactExists(this, name)) {
            Toast.makeText(this, "Contact already exists!", Toast.LENGTH_SHORT).show();
            return;
        }

        dbHelper.addContact(this, name, phone); // Save contact with prefixed number

        Toast.makeText(this, "Contact saved", Toast.LENGTH_SHORT).show();
        setResult(RESULT_OK);
        finish();
    }

    private void updateContact() {
        String name = editTextName.getText().toString().trim();
        String phone = editTextPhone.getText().toString().trim();

        if (name.isEmpty() || phone.isEmpty()) {
            Toast.makeText(this, "Please enter both name and phone number", Toast.LENGTH_SHORT).show();
            return;
        }

        // Check if the phone number already starts with +91, else add it
        if (!phone.startsWith("+91")) {
            phone = "+91 " + phone;
        }

        dbHelper.updateContact(contactId, name, phone); // Update contact with prefixed number

        Toast.makeText(this, "Contact updated", Toast.LENGTH_SHORT).show();
        setResult(RESULT_OK);
        finish();
    }



    // Method to delete a contact
    private void deleteContact() {
        dbHelper.deleteContact(contactId);
        Toast.makeText(this, "Contact deleted", Toast.LENGTH_SHORT).show();

        setResult(RESULT_OK);
        finish();
    }
}
