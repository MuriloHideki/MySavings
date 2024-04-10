package com.example.mysavings;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mysavings.model.Transaction;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private EditText TransactionEditAmount;
    private Spinner spinnerPriority;
    private Button transactionAddButtonAdd;
    private RecyclerView recyclerViewTransactions;
    private EditText TransactionEditTextDescription;
    private LinearLayout linearLayoutIncomes;
    private TaskAdapter taskAdapter;
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


        recyclerViewTransactions.setLayoutManager(new LinearLayoutManager(this));
        taskAdapter = new TaskAdapter(transactions);
        recyclerViewTransactions.setAdapter(taskAdapter);


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
                    taskAdapter.notifyDataSetChanged();

                    TransactionEditAmount.setText("");
                    TransactionEditTextDescription.setText("");
                } else {
                    Toast.makeText(getApplicationContext(), "Informe um registro", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    // Classe interna para o adaptador do RecyclerView
    private class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {

        private List<Transaction> transactions;

        // Construtor que recebe a lista de tarefas
        public TaskAdapter(List<Transaction> transactions) {
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
        public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
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
            return new TaskViewHolder(view);
        }

        // Método que atualiza o conteúdo das visualizações com base nos dados da tarefa
        @Override
        /*
        Este método é chamado pelo RecyclerView para exibir os dados de uma tarefa em um item da lista
        específico. Ele recebe dois parâmetros: holder, que é a instância do TaskViewHolder que contém
        os elementos de interface do item da lista, e position, que é a posição da tarefa na lista de tarefas.
         */
        public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
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
        public class TaskViewHolder extends RecyclerView.ViewHolder {
            private TextView textViewDescription;
            private TextView textViewPriority;
            private TextView textViewValue;

            // Construtor que recebe a visualização do item de tarefa
            public TaskViewHolder(@NonNull View itemView) {
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