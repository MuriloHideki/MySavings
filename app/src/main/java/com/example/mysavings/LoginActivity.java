package com.example.mysavings;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.mysavings.model.Transaction;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
public class LoginActivity extends AppCompatActivity {

    private EditText editTextUsername;
    private EditText editTextPassword;
    private Button buttonLogin;
    private TextView textViewClock;

    private ClockTask clockTask;
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        editTextUsername = findViewById(R.id.editTextUsername);
        editTextPassword = findViewById(R.id.editTextPassword);
        buttonLogin = findViewById(R.id.buttonLogin);
        textViewClock = findViewById(R.id.textViewClock);

        mAuth = FirebaseAuth.getInstance();
        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginUser();
            }
        });

        startClock();
    }

    private void loginUser() {
        String email = editTextUsername.getText().toString();
        String password = editTextPassword.getText().toString();
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(LoginActivity.this, "Email or Password incorrect.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void startClock() {
        Handler handler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                if (msg.what == 1) {
                    String currentTime = (String) msg.obj;
                    textViewClock.setText(currentTime);
                }
                return false;
            }
        });

        clockTask = new ClockTask(handler);
        clockTask.execute();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (clockTask != null) {
            clockTask.cancel(true);
        }
    }

    private static class ClockTask extends AsyncTask<Void, String, Void> {


        private Handler handler;
        private SimpleDateFormat sdf;

        public ClockTask(Handler handler) {
            this.handler = handler;
            this.sdf = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
        }

        @Override
        protected Void doInBackground(Void... voids) {
            while (!isCancelled()) {
                String currentTime = sdf.format(new Date());
                publishProgress(currentTime);

                try {
                    Thread.sleep(1000); // Atualiza a cada segundo
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            String currentTime = values[0];
            Message message = handler.obtainMessage(1, currentTime);
            handler.sendMessage(message);
        }
    }
}