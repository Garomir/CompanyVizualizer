package ru.ramich.companyvizualizer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import ru.ramich.companyvizualizer.models.Department;
import ru.ramich.companyvizualizer.models.Worker;
import ru.ramich.companyvizualizer.utils.DepartmentAdapter;
import ru.ramich.companyvizualizer.utils.WorkersAdapter;

public class WorkersActivity extends AppCompatActivity {

    ListView lvWorkers;
    TextView tvCompName, tvDepartmentName;

    Retrofit retrofit = new  Retrofit.Builder()
            .baseUrl("http://ramich.hopto.org")
            .addConverterFactory(GsonConverterFactory.create())
            .build();

    JSONPlaceHolderApi jsonPlaceHolderApi = retrofit.create(JSONPlaceHolderApi.class);
    List<Worker> workers = new ArrayList<>();
    WorkersAdapter myAdapter;
    private int companyId, departmentId;
    private String companyName, departmentName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workers);

        tvCompName = findViewById(R.id.tvCompName);
        tvDepartmentName = findViewById(R.id.tvDepartmentName);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            companyId = extras.getInt("companyId");
            companyName = extras.getString("companyName");
            departmentId = extras.getInt("departmentId");
            departmentName = extras.getString("departmentName");
            tvCompName.setText(companyName);
            tvDepartmentName.setText(departmentName);
        }

        lvWorkers = findViewById(R.id.lvWorkers);
        registerForContextMenu(lvWorkers);
        lvWorkers.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //Возможно тут чтото будет
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        workers.clear();
        getAllWorkers(departmentId);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.itemAdd:
                //Открыть Dialog на добавление новой записи в БД
                showDialog();
        }
        return true;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        getMenuInflater().inflate(R.menu.main_context_menu, menu);
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        AdapterView.AdapterContextMenuInfo acmi = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        switch (item.getItemId()){
            case R.id.itemDelete:
                int someId = (int) myAdapter.getItemId(acmi.position);
                deleteWorker(someId);
        }
        return true;
    }

    public void getAllWorkers(int id){
        Call<List<Worker>> call = jsonPlaceHolderApi.getAllWorkersByDepartment(id);

        call.enqueue(new Callback<List<Worker>>() {
            @Override
            public void onResponse(Call<List<Worker>> call, Response<List<Worker>> response) {
                workers = response.body();
                myAdapter = new WorkersAdapter(workers);
                lvWorkers.setAdapter(myAdapter);
            }

            @Override
            public void onFailure(Call<List<Worker>> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "Error: " + t, Toast.LENGTH_LONG).show();
            }
        });
    }

    public void createWorker(Worker worker){
        Call<Worker> dep = jsonPlaceHolderApi.addWorker(companyId, departmentId, worker);

        dep.enqueue(new Callback<Worker>() {
            @Override
            public void onResponse(Call<Worker> call, Response<Worker> response) {
                workers.clear();
                getAllWorkers(departmentId);
                Toast.makeText(getApplicationContext(), "Worker added!", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(Call<Worker> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "Error: " + t, Toast.LENGTH_LONG).show();
            }
        });
    }

    public void deleteWorker(int id){
        Call<Void> call = jsonPlaceHolderApi.deleteWorker(id);

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                workers.clear();
                getAllWorkers(departmentId);
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "Error: " + t, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void showDialog() {
        LayoutInflater inflater = LayoutInflater.from(this);
        View dialogView = inflater.inflate(R.layout.dialog_worker, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView);

        final EditText etFirstname = (EditText) dialogView.findViewById(R.id.etFirstname);
        final EditText etLastname = (EditText) dialogView.findViewById(R.id.etLastname);
        final EditText etPosition = (EditText) dialogView.findViewById(R.id.etPosition);
        final EditText etSalary = (EditText) dialogView.findViewById(R.id.etSalary);

        builder
                .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //Можно передавать строку в активити по нажатию
                        String first = etFirstname.getText().toString();
                        String last = etLastname.getText().toString();
                        String position = etPosition.getText().toString();
                        String salary = etSalary.getText().toString();
                        int sal = Integer.parseInt(salary);

                        if (first.length() == 0 || last.length() == 0 || position.length() == 0 || salary.length() == 0){
                            Toast.makeText(getApplicationContext(), "Enter the fields!", Toast.LENGTH_SHORT).show();
                        } else {
                            createWorker(new Worker(first, last, position, sal));
                        }
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(getApplicationContext(), "Cancel", Toast.LENGTH_SHORT).show();
                    }
                });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}