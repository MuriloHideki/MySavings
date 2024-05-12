package com.example.mysavings;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class EditTransactionActivity extends AppCompatActivity {
    private EditText editTextName;
    private EditText editTextValue;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_transaction);

        editTextName = findViewById(R.id.editTextName);
        editTextValue = findViewById(R.id.editTextValue);
        Button buttonSave = findViewById(R.id.buttonSave);

        String transactionId = getIntent().getStringExtra("transactionId");
        databaseReference = FirebaseDatabase.getInstance().getReference("transactions");

        editTextName.setText(getIntent().getStringExtra("name"));
        editTextValue.setText(String.valueOf(getIntent().getDoubleExtra("value", 0)));

        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveTransaction(transactionId);
            }
        });
    }

    private void saveTransaction(String transactionId) {
        String name = editTextName.getText().toString();
        double value = Double.parseDouble(editTextValue.getText().toString());

        DatabaseReference transactionRef = databaseReference.child(transactionId);
        transactionRef.child("name").setValue(name, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                if (databaseError == null) {
                    Toast.makeText(EditTransactionActivity.this, "Transaction updated successfully!", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(EditTransactionActivity.this, "Failed to update transaction: " + databaseError.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
        transactionRef.child("value").setValue(value);
    }

}
