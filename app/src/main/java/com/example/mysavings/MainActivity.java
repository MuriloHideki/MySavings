//Murilo Hideki Sakomura
//190044
package com.example.mysavings;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mysavings.helper.TransactionDbHelper;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import com.example.mysavings.model.Transaction;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private EditText TransactionEditAmount;
    private EditText TransactionEditTextName;
    private Spinner spinnerPriority;
    private Button transactionAddButtonAdd;
    private Button buttonSendTransactions;
    private RecyclerView recyclerViewTransactions;
    private LinearLayout linearLayoutIncomes;
    private Button buttonFoto;
    private TransactionAdapter transactionAdapter;
    private ImageView imageView;
    private List<Transaction> transactions = new ArrayList<>();
    private DatabaseReference databaseReference;
    private TextView balance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        spinnerPriority = findViewById(R.id.spinnerPriority);
        TransactionEditAmount = findViewById(R.id.editAmountTransaction);
        TransactionEditTextName = findViewById(R.id.editTextIncomeName);
        transactionAddButtonAdd = findViewById(R.id.buttonAddIncome);
        linearLayoutIncomes = findViewById(R.id.linearLayoutIncomes);
        recyclerViewTransactions = findViewById(R.id.recyclerViewIncomes);
        buttonSendTransactions = findViewById(R.id.buttonSendTransactions);
        imageView = findViewById(R.id.imageView);
        buttonFoto = findViewById(R.id.foto);
        balance = findViewById(R.id.textBalance);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference("transactions");

        recyclerViewTransactions.setLayoutManager(new LinearLayoutManager(this));

        OnTransactionClickListener listener = new OnTransactionClickListener() {
            @Override
            public void onTransactionClick(Transaction transaction) {
                onEditTransactionClicked(transaction);
            }
        };

        recyclerViewTransactions.setLayoutManager(new LinearLayoutManager(this));
        transactionAdapter = new TransactionAdapter(transactions, listener);
        recyclerViewTransactions.setAdapter(transactionAdapter);


        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.register_types, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerPriority.setAdapter(adapter);


        transactionAddButtonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String transactionName = TransactionEditTextName.getText().toString().trim();
                final String transactionPriority = spinnerPriority.getSelectedItem().toString();
                final String dateString = ((EditText)findViewById(R.id.editTransactionDate)).getText().toString();
                final double transactionValue;
                try {
                    transactionValue = Double.parseDouble(TransactionEditAmount.getText().toString().trim());
                } catch (NumberFormatException e) {
                    Toast.makeText(getApplicationContext(), "Por favor, insira um valor válido.", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!transactionName.isEmpty() && !transactionPriority.isEmpty()) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setTitle("Confirm Transaction");
                    builder.setMessage("Do you realy want to add this transaction?\n\n" +
                            "Name: " + transactionName +
                            "\nValue: " + String.format("%.2f", transactionValue) +
                            "\nType: " + transactionPriority +
                            "\nDate: " + dateString);
                    builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            Transaction transaction = new Transaction();
                            transaction.setName(transactionName);
                            transaction.setType(transactionPriority);
                            transaction.setValue(transactionValue);

                            try {
                                Date date = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).parse(dateString);
                                transaction.setDate(date);
                            } catch (ParseException e) {
                                Toast.makeText(MainActivity.this, "Invalid date format", Toast.LENGTH_SHORT).show();
                                return;
                            }

                            transactions.add(transaction);
                            transactionAdapter.notifyDataSetChanged();

                            String transactionId = databaseReference.push().getKey();
                            transaction.setId(transactionId);
                            databaseReference.child(transactionId).setValue(transaction);

                            TransactionDbHelper dbHelper = new TransactionDbHelper(MainActivity.this);
                            SQLiteDatabase db = dbHelper.getWritableDatabase();

                            ContentValues values = new ContentValues();
                            values.put("description", transaction.getName());
                            values.put("type", transaction.getType());
                            values.put("value", transaction.getValue());
                            values.put("date", transaction.getDate().getTime());
                            values.put("id", transaction.getId());

                            db.insert("transactions", null, values);

                            TransactionEditAmount.setText("");
                            TransactionEditTextName.setText("");
                            ((EditText)findViewById(R.id.editTransactionDate)).setText("");

                            Toast.makeText(getApplicationContext(), "Transaction added successfully!", Toast.LENGTH_SHORT).show();
                        }
                    });
                    builder.setNegativeButton("Cancel",null);

                    AlertDialog dialog = builder.create();
                    dialog.show();
                } else {
                    Toast.makeText(getApplicationContext(), "All fields are required", Toast.LENGTH_SHORT).show();
                }
            }
        });

        buttonSendTransactions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendTasksByEmail();
            }
        });

        buttonFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tirarFoto(view);
            }
        });
    }

    protected void onStart() {
        super.onStart();
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                transactions.clear();
                double balanceAux = 0;
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Transaction transaction = postSnapshot.getValue(Transaction.class);

                    if(transaction.getType().equals("Income")){
                        balanceAux += transaction.getValue();
                    }else {
                        balanceAux -= transaction.getValue();
                    }

                    balance.setText("Balance: " + String.format("%.2f", balanceAux));

                    transactions.add(transaction);
                }
                transactionAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    public void onEditTransactionClicked(Transaction transaction) {
        Intent intent = new Intent(MainActivity.this, EditTransactionActivity.class);
        intent.putExtra("transactionId", transaction.getId());
        intent.putExtra("name", transaction.getName());
        intent.putExtra("value", transaction.getValue());
        intent.putExtra("date", transaction.getDate().getTime());
        startActivity(intent);
    }

    ActivityResultLauncher<Intent> fotografarLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),


            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        Bundle extras = data.getExtras();
                        Bitmap imageBitmap = (Bitmap) extras.get("data");
                        imageView.setImageBitmap(imageBitmap);
                    }
                }
            });

    public void tirarFoto(View view) {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            fotografarLauncher.launch(takePictureIntent);
        }
    }
    private void sendTasksByEmail() {
        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.setType("text/plain");
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Transaction List");
        emailIntent.putExtra(Intent.EXTRA_TEXT, generateTransactionsText());
        startActivity(Intent.createChooser(emailIntent, "Send Transaction List"));
    }

    private String generateTransactionsText() {
        StringBuilder sb = new StringBuilder();
        for (Transaction transaction : transactions) {
            sb.append("Description: ").append(transaction.getName()).append("\n");
            sb.append("Value: ").append(transaction.getValue()).append("\n");
            sb.append("Type: ").append(transaction.getType()).append("\n");
            sb.append("Date: ").append(transaction.getDate()).append("\n");
        }
        return sb.toString();
    }
    public interface OnTransactionClickListener {
        void onTransactionClick(Transaction transaction);
    }
    private class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder> {

        private List<Transaction> transactions;
        private OnTransactionClickListener listener;

        public TransactionAdapter(List<Transaction> transactions, OnTransactionClickListener listener) {
            this.transactions = transactions;
            this.listener = listener;
        }

        @NonNull
        @Override
        public TransactionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.transaction_item, parent, false);
            return new TransactionViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull TransactionViewHolder holder, int position) {
            Transaction transaction = transactions.get(position);
            holder.bind(transaction);
        }

        @Override
        public int getItemCount() {
            return transactions.size();
        }

        public class TransactionViewHolder extends RecyclerView.ViewHolder {
            private TextView textViewName;
            private TextView textViewPriority;
            private TextView textViewValue;
            private TextView textViewDate;
            public TransactionViewHolder(@NonNull View itemView) {
                super(itemView);
                textViewPriority = itemView.findViewById(R.id.textViewPriority);
                textViewName = itemView.findViewById(R.id.textViewName);
                textViewValue = itemView.findViewById(R.id.textViewValue);
                textViewDate = itemView.findViewById(R.id.textViewDate);
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION && listener != null) {
                            Transaction transaction = transactions.get(position);
                            MainActivity.this.onEditTransactionClicked(transaction);
                        }
                    }
                });
            }

            public void bind(Transaction transaction) {
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                String formattedDate = sdf.format(transaction.getDate());

                textViewPriority.setText("Type: " + transaction.getType());
                textViewValue.setText("Value: " + String.format("%.2f", transaction.getValue()));
                textViewName.setText("Name: " + transaction.getName());
                textViewDate.setText("Date: " + formattedDate);
            }
        }

    }
    public void showDatePickerDialog(View v) {
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(MainActivity.this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        Calendar cal = Calendar.getInstance();
                        cal.set(Calendar.YEAR, year);
                        cal.set(Calendar.MONTH, month);
                        cal.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        Date date = cal.getTime();
                        EditText editTransactionDate = findViewById(R.id.editTransactionDate);
                        editTransactionDate.setText(new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(date));
                    }
                }, year, month, day);
        datePickerDialog.show();
    }
}