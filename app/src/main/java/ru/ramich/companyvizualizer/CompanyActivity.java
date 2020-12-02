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
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import ru.ramich.companyvizualizer.models.Company;
import ru.ramich.companyvizualizer.utils.CustomAdapter;

public class CompanyActivity extends AppCompatActivity {

    ListView lvCompanies;

    Retrofit retrofit = new  Retrofit.Builder()
            .baseUrl("http://ramich.hopto.org")
            .addConverterFactory(GsonConverterFactory.create())
            .build();

    JSONPlaceHolderApi jsonPlaceHolderApi = retrofit.create(JSONPlaceHolderApi.class);
    List<Company> companies = new ArrayList<>();
    CustomAdapter myAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_company);

        lvCompanies = findViewById(R.id.lvCompanies);
        registerForContextMenu(lvCompanies);
        lvCompanies.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //Запустить DepartmentActivity
                Company c = (Company) myAdapter.getItem(position);
                Intent intent = new Intent(getApplicationContext(), DepartmentActivity.class);
                intent.putExtra("companyId", c.getId());
                intent.putExtra("companyName", c.getName());
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        companies.clear();
        getAllCompanies();
    }

    public void getAllCompanies(){
        Call<List<Company>> call = jsonPlaceHolderApi.getAllCompanies();

        call.enqueue(new Callback<List<Company>>() {
            @Override
            public void onResponse(Call<List<Company>> call, Response<List<Company>> response) {
                companies = response.body();
                myAdapter = new CustomAdapter(companies);
                lvCompanies.setAdapter(myAdapter);
            }

            @Override
            public void onFailure(Call<List<Company>> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "Error: " + t, Toast.LENGTH_LONG).show();
            }
        });
    }

    public void deleteCompany(int id){
        Call<Void> call = jsonPlaceHolderApi.deleteCompany(id);

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                companies.clear();
                getAllCompanies();
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "Error: " + t, Toast.LENGTH_LONG).show();
            }
        });
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
                deleteCompany(someId);
        }
        return true;
    }

    public void createCompany(Company company){
        Call<Company> note = jsonPlaceHolderApi.addCompany(company);

        note.enqueue(new Callback<Company>() {
            @Override
            public void onResponse(Call<Company> call, Response<Company> response) {
                companies.clear();
                getAllCompanies();
                Toast.makeText(getApplicationContext(), "Company added!", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(Call<Company> call, Throwable t) {
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
                            createCompany(new Company(someName));
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