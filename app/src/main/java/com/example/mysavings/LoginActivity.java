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

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
public class LoginActivity extends AppCompatActivity {

    private EditText editTextUsername;
    private EditText editTextPassword;
    private Button buttonLogin;
    private TextView textViewClock;

    private ClockTask clockTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        editTextUsername = findViewById(R.id.editTextUsername);
        editTextPassword = findViewById(R.id.editTextPassword);
        buttonLogin = findViewById(R.id.buttonLogin);
        textViewClock = findViewById(R.id.textViewClock);

        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = editTextUsername.getText().toString().trim();
                String password = editTextPassword.getText().toString().trim();

                if (authenticate(username, password)) {
                    // Autenticação bem-sucedida, abra a próxima Activity
                    //Intent Explicita
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish(); // Fecha a LoginActivity para não voltar quando pressionar o botão "Voltar"
                } else {
                    // Autenticação falhou, exibe uma mensagem de erro
                    Toast.makeText(LoginActivity.this, "Usuário ou senha inválidos", Toast.LENGTH_SHORT).show();
                }
            }
        });

        startClock();
    }

    private boolean authenticate(String username, String password) {
        return !username.isEmpty() && !password.isEmpty();
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