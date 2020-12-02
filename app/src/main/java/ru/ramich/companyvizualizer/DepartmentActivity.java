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
import ru.ramich.companyvizualizer.models.Company;
import ru.ramich.companyvizualizer.models.Department;
import ru.ramich.companyvizualizer.utils.CustomAdapter;
import ru.ramich.companyvizualizer.utils.DepartmentAdapter;

public class DepartmentActivity extends AppCompatActivity {

    ListView lvDepartments;
    TextView tvCompanyName;

    Retrofit retrofit = new  Retrofit.Builder()
            .baseUrl("http://ramich.hopto.org")
            .addConverterFactory(GsonConverterFactory.create())
            .build();

    JSONPlaceHolderApi jsonPlaceHolderApi = retrofit.create(JSONPlaceHolderApi.class);
    List<Department> departments = new ArrayList<>();
    DepartmentAdapter myAdapter;
    private int companyId;
    private String companyName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_department);

        tvCompanyName = findViewById(R.id.tvCompanyName);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            companyId = extras.getInt("companyId");
            companyName = extras.getString("companyName");
            tvCompanyName.setText(companyName);
        }

        lvDepartments = findViewById(R.id.lvDepartments);
        registerForContextMenu(lvDepartments);
        lvDepartments.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //Запустить WorkersActivity
                Department d = (Department) myAdapter.getItem(position);
                Intent intent = new Intent(getApplicationContext(), WorkersActivity.class);
                intent.putExtra("departmentId", d.getId());
                intent.putExtra("departmentName", d.getName());
                intent.putExtra("companyId", companyId);
                intent.putExtra("companyName", companyName);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        departments.clear();
        getAllDepartments(companyId);
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
                deleteDepartment(someId);
        }
        return true;
    }

    public void getAllDepartments(int id){
        Call<List<Department>> call = jsonPlaceHolderApi.getAllDepartmentsByCompany(id);

        call.enqueue(new Callback<List<Department>>() {
            @Override
            public void onResponse(Call<List<Department>> call, Response<List<Department>> response) {
                departments = response.body();
                myAdapter = new DepartmentAdapter(departments);
                lvDepartments.setAdapter(myAdapter);
            }

            @Override
            public void onFailure(Call<List<Department>> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "Error: " + t, Toast.LENGTH_LONG).show();
            }
        });
    }

    public void createDepartment(Department department){
        Call<Department> dep = jsonPlaceHolderApi.addDepartment(companyId, department);

        dep.enqueue(new Callback<Department>() {
            @Override
            public void onResponse(Call<Department> call, Response<Department> response) {
                departments.clear();
                getAllDepartments(companyId);
                Toast.makeText(getApplicationContext(), "Department added!", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(Call<Department> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "Error: " + t, Toast.LENGTH_LONG).show();
            }
        });
    }

    public void deleteDepartment(int id){
        Call<Void> call = jsonPlaceHolderApi.deleteDepartment(id);

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                departments.clear();
                getAllDepartments(companyId);
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "Error: " + t, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void showDialog() {
        LayoutInflater inflater = LayoutInflater.from(this);
        View dialogView = inflater.inflate(R.layout.dialog, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView);

        final EditText etName = (EditText) dialogView.findViewById(R.id.etName);

        builder
                .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //Можно передавать строку в активити по нажатию
                        String someName = etName.getText().toString();
                        if (someName.length() == 0){
                            Toast.makeText(getApplicationContext(), "Enter the field!", Toast.LENGTH_SHORT).show();
                        } else {
                            createDepartment(new Department(someName));
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