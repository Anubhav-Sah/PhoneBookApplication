package com.example.phonebookapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

// MainActivity.java

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {

    private ListView listViewContacts;
    private FloatingActionButton buttonAddContact;
    private DatabaseHelper dbHelper;
    private List<Contact> contactList;
    private ContactAdapter contactAdapter;
    private EditText editTextSearch; // Add this for search input

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listViewContacts = findViewById(R.id.listViewContacts);
        buttonAddContact = findViewById(R.id.buttonAddContact);
        editTextSearch = findViewById(R.id.editText); // Link the search EditText from XML
        dbHelper = new DatabaseHelper(this);

        buttonAddContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AddEditContactActivity.class);
                startActivity(intent);
            }
        });

        loadContacts();

        listViewContacts.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Contact selectedContact = contactList.get(position);
                Intent intent = new Intent(MainActivity.this, AddEditContactActivity.class);
                intent.putExtra("contact_id", selectedContact.getId());
                intent.putExtra("contact_name", selectedContact.getName());
                intent.putExtra("contact_phone", selectedContact.getPhone());
                startActivityForResult(intent, 2);
            }
        });

        // Set up the search functionality
        editTextSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Perform the search
                searchContacts(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadContacts();
    }

    private void loadContacts() {
        contactList = dbHelper.getAllContacts();
        contactAdapter = new ContactAdapter(this, contactList);
        listViewContacts.setAdapter(contactAdapter);
    }

    private void searchContacts(String query) {
        contactList = dbHelper.searchContacts(query);
        contactAdapter = new ContactAdapter(this, contactList);
        listViewContacts.setAdapter(contactAdapter);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            loadContacts();
        } else {
            Toast.makeText(this, "Failed to save/update contact", Toast.LENGTH_SHORT).show();
        }
    }
}
