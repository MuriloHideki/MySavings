package com.example.mysavings;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
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
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mysavings.model.Transaction;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private EditText TransactionEditAmount;
    private EditText TransactionEditTextDescription;
    private Spinner spinnerPriority;
    private Button transactionAddButtonAdd;
    private Button buttonSendTransactions;
    private RecyclerView recyclerViewTransactions;
    private LinearLayout linearLayoutIncomes;
    private Button buttonFoto;
    private TransactionAdapter transactionAdapter;
    private ImageView imageView;
    private List<Transaction> transactions = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        spinnerPriority = findViewById(R.id.spinnerPriority);
        TransactionEditAmount = findViewById(R.id.editAmountTransaction);
        TransactionEditTextDescription = findViewById(R.id.editTextIncomeDescription);
        transactionAddButtonAdd = findViewById(R.id.buttonAddIncome);
        linearLayoutIncomes = findViewById(R.id.linearLayoutIncomes);
        recyclerViewTransactions = findViewById(R.id.recyclerViewIncomes);
        buttonSendTransactions = findViewById(R.id.buttonSendTransactions);
        imageView = findViewById(R.id.imageView);
        buttonFoto = findViewById(R.id.foto);

        recyclerViewTransactions.setLayoutManager(new LinearLayoutManager(this));
        transactionAdapter = new TransactionAdapter(transactions);
        recyclerViewTransactions.setAdapter(transactionAdapter);


        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.register_types, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerPriority.setAdapter(adapter);

        
        transactionAddButtonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String taskDescription = TransactionEditTextDescription.getText().toString().trim();
                String taskPriority = spinnerPriority.getSelectedItem().toString();
                double taskValue = Double.parseDouble(TransactionEditAmount.getText().toString().trim());

                if (!taskDescription.isEmpty() && !taskPriority.isEmpty()) {
                    Transaction transaction = new Transaction();
                    transaction.setDescription(taskDescription);
                    transaction.setType(taskPriority);
                    transaction.setValue(taskValue);

                    transactions.add(transaction);
                    transactionAdapter.notifyDataSetChanged();

                    TransactionEditAmount.setText("");
                    TransactionEditTextDescription.setText("");
                } else {
                    Toast.makeText(getApplicationContext(), "Informe um registro", Toast.LENGTH_SHORT).show();
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

        buttonSendTransactions.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                sendTasksByEmail();
            }
        });
    }

    ActivityResultLauncher<Intent> fotografarLauncher = registerForActivityResult(
            /*Aqui, você está criando um novo contrato de resultado de atividade usando
            ActivityResultContracts.StartActivityForResult(). Esse contrato é usado para iniciar uma atividade e receber um resultado.*/
            new ActivityResultContracts.StartActivityForResult(),

            /*
            Aqui, você está definindo um callback para lidar com o resultado da atividade. Este callback é chamado quando a atividade é concluída e retorna um resultado.
             */
            new ActivityResultCallback<ActivityResult>() {
                /*
                Aqui, você está implementando o método onActivityResult do ActivityResultCallback, que é chamado
                quando a atividade é concluída. O parâmetro result contém o resultado da atividade.
                 */
                @Override
                public void onActivityResult(ActivityResult result) {
                    /*
                    Aqui, você está verificando se a atividade foi concluída com sucesso. O código Activity.RESULT_OK indica que a atividade foi concluída com sucesso.
                     */
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        //Aqui, você está obtendo os dados da intent da atividade concluída.
                        Intent data = result.getData();
                        //Aqui, você está obtendo os extras (dados adicionais) da intent, que podem incluir a imagem capturada pela câmera.
                        Bundle extras = data.getExtras();
                        //Aqui, você está obtendo a imagem capturada da intent usando a chave "data" e convertendo-a em um objeto Bitmap.
                        Bitmap imageBitmap = (Bitmap) extras.get("data");
                        imageView.setImageBitmap(imageBitmap);
                    }
                }
            });

    // Este é o método tirarFoto que é chamado quando o usuário clica em um botão para tirar uma foto.
    public void tirarFoto(View view) {
        //Aqui, você está criando uma intent para capturar uma imagem usando a câmera do dispositivo.
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        //Aqui, você está verificando se há uma atividade de câmera disponível para lidar com a intent. Se houver, o código dentro do bloco if será executado.
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            //Aqui, você está iniciando a atividade de captura de imagem usando o FotografarLauncher, que está configurado para lidar com o resultado da atividade.
            fotografarLauncher.launch(takePictureIntent);
        }
    }
    private void sendTasksByEmail() {
        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        /*
        Outros tipos possiveis são, por exemplo:
        message/rfc822: Para enviar um email com formato MIME, que pode incluir anexos e formatação avançada.
        text/html: Para enviar um email com conteúdo HTML, permitindo formatação avançada e elementos interativos.
        image/jpeg, image/png, etc.: Para enviar imagens como anexos no email.
        application/pdf, application/msword, etc.: Para enviar arquivos PDF, documentos do Word, e
        outros tipos de arquivos como anexos.
         */
        emailIntent.setType("text/plain");
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Transaction List");
        emailIntent.putExtra(Intent.EXTRA_TEXT, generateTasksText());
        startActivity(Intent.createChooser(emailIntent, "Send Transaction List"));
    }

    private String generateTasksText() {
        StringBuilder sb = new StringBuilder();
        for (Transaction transaction : transactions) {
            sb.append("Description: ").append(transaction.getDescription()).append("\n");
            sb.append("Value: ").append(transaction.getValue()).append("\n");
            sb.append("Type: ").append(transaction.getType()).append("\n");
            //sb.append("Concluída: ").append(transaction.isCompleted() ? "Sim" : "Não").append("\n\n");
        }
        return sb.toString();
    }

    // Classe interna para o adaptador do RecyclerView
    private class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder> {

        private List<Transaction> transactions;

        // Construtor que recebe a lista de tarefas
        public TransactionAdapter(List<Transaction> transactions) {
            this.transactions = transactions;
        }

        // Método que cria novas visualizações (layout) para os itens da lista
        @NonNull
        @Override
        /*
         Este método é chamado quando o RecyclerView precisa criar uma nova visualização (item de lista)
         para exibir. Ele recebe dois parâmetros: parent, que é o ViewGroup no qual a nova visualização
         será inserida após a criação, e viewType, que é o tipo de visualização, caso o RecyclerView
         tenha vários tipos de itens de lista.
         */
        public TransactionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            /*
            Aqui, estamos inflando o layout task_item.xml para criar a visualização do item da lista.
            O LayoutInflater é usado para inflar layouts XML em objetos de visualização reais. parent.getContext()
            retorna o contexto do ViewGroup pai, que é necessário para o LayoutInflater. O método inflate cria uma
            nova instância de View a partir do arquivo de layout XML task_item.xml. O terceiro argumento false
            indica que a visualização recém-criada não deve ser anexada ao ViewGroup pai automaticamente,
            pois o RecyclerView cuidará disso.
             */
            /*
            O processo de inflar um layout envolve a interpretação do arquivo XML para criar os objetos de visualização
            correspondentes, como TextView, Button, LinearLayout, entre outros, conforme definido no arquivo XML.
            Isso permite que você defina a estrutura e a aparência de suas interfaces de usuário de forma declarativa,
            sem precisar criar manualmente cada elemento de interface no código Java.
             */
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.transaction_item, parent, false);
            return new TransactionViewHolder(view);
        }

        // Método que atualiza o conteúdo das visualizações com base nos dados da tarefa
        @Override
        /*
        Este método é chamado pelo RecyclerView para exibir os dados de uma tarefa em um item da lista
        específico. Ele recebe dois parâmetros: holder, que é a instância do TaskViewHolder que contém
        os elementos de interface do item da lista, e position, que é a posição da tarefa na lista de tarefas.
         */
        public void onBindViewHolder(@NonNull TransactionViewHolder holder, int position) {
            /*
            Aqui, estamos obtendo a tarefa na posição especificada da lista de tarefas. A lista de
            tarefas (tasks) é a fonte de dados que contém todas as tarefas a serem exibidas na lista.
             */
            Transaction transaction = transactions.get(position);
            /*
             Em seguida, estamos chamando o método bind do TaskViewHolder para associar os dados da tarefa
             aos elementos de interface do item da lista. O método bind é responsável por atualizar os elementos
             de interface (como TextView para título, descrição e prioridade, e CheckBox para concluído)
             com os dados da tarefa específica.
             */
            holder.bind(transaction);
        }

        // Método que retorna o número total de itens na lista de tarefas
        @Override
        public int getItemCount() {
            return transactions.size();
        }

        // Classe interna que representa cada item de tarefa na lista
        public class TransactionViewHolder extends RecyclerView.ViewHolder {
            private TextView textViewDescription;
            private TextView textViewPriority;
            private TextView textViewValue;

            // Construtor que recebe a visualização do item de tarefa
            public TransactionViewHolder(@NonNull View itemView) {
                super(itemView);
                // Inicializa os elementos de interface do item de tarefa
                textViewPriority = itemView.findViewById(R.id.textViewPriority);
                textViewDescription = itemView.findViewById(R.id.textViewDescription);
                textViewValue = itemView.findViewById(R.id.textViewValue);
//                checkBoxCompleted = itemView.findViewById(R.id.checkBoxCompleted);
            }

            // Método que associa os dados da tarefa aos elementos de interface do item de tarefa
            public void bind(Transaction transaction) {
                textViewPriority.setText("Type: " + transaction.getType());
                textViewValue.setText("Value: " + transaction.getValue());
                textViewDescription.setText("Description: " + transaction.getDescription());
            }
        }
    }
}