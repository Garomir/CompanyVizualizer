package ru.ramich.companyvizualizer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import ru.ramich.companyvizualizer.models.Worker;

public class WorkerDetailsActivity extends AppCompatActivity {

    private int workerId, workerSalary, companyId, departmentId;
    private String workerFN, workerLN, workerPosition, workerBirth;
    EditText etFirstname, etLastname, etPosition, etSalary, etBirthday;

    Retrofit retrofit = new  Retrofit.Builder()
            .baseUrl("http://ramich.hopto.org")
            .addConverterFactory(GsonConverterFactory.create())
            .build();

    JSONPlaceHolderApi jsonPlaceHolderApi = retrofit.create(JSONPlaceHolderApi.class);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_worker_details);

        etFirstname = findViewById(R.id.etFirstname);
        etLastname = findViewById(R.id.etLastname);
        etPosition = findViewById(R.id.etPosition);
        etSalary = findViewById(R.id.etSalary);
        etBirthday = findViewById(R.id.etBirthday);

        Bundle extras = getIntent().getExtras();
        if (extras.containsKey("workerId")) {
            workerId = extras.getInt("workerId");
            workerSalary = extras.getInt("workerSalary");
            workerFN = extras.getString("workerFN");
            workerLN = extras.getString("workerLN");
            workerPosition = extras.getString("workerPosition");
            workerBirth = extras.getString("workerBirth");
            etFirstname.setText(workerFN);
            etLastname.setText(workerLN);
            etPosition.setText(workerPosition);
            etSalary.setText(String.valueOf(workerSalary));
            etBirthday.setText(workerBirth);
        }else if (extras.containsKey("companyId")){
            companyId = extras.getInt("companyId");
            departmentId = extras.getInt("departmentId");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.details_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        Worker workerToSave = new Worker(
                etFirstname.getText().toString(),
                etLastname.getText().toString(),
                etPosition.getText().toString(),
                Integer.parseInt(etSalary.getText().toString()),
                etBirthday.getText().toString());
        switch (item.getItemId()){
            case R.id.itemSave:
                if (workerId > 0){
                    updateWorker(workerId, workerToSave);
                } else {
                    createWorker(workerToSave);
                }
        }
        return true;
    }

    public void createWorker(Worker worker){
        Call<Worker> dep = jsonPlaceHolderApi.addWorker(companyId, departmentId, worker);

        dep.enqueue(new Callback<Worker>() {
            @Override
            public void onResponse(Call<Worker> call, Response<Worker> response) {
                Toast.makeText(getApplicationContext(), "New worker created!", Toast.LENGTH_LONG).show();
                finish();
            }

            @Override
            public void onFailure(Call<Worker> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "Error: " + t, Toast.LENGTH_LONG).show();
            }
        });
    }

    public void updateWorker(int id, Worker worker){
        Call<Worker> wor = jsonPlaceHolderApi.updateWorker(worker, id);

        wor.enqueue(new Callback<Worker>() {
            @Override
            public void onResponse(Call<Worker> call, Response<Worker> response) {
                Toast.makeText(getApplicationContext(), "Worker updated!", Toast.LENGTH_LONG).show();
                finish();
            }

            @Override
            public void onFailure(Call<Worker> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "Error: " + t, Toast.LENGTH_LONG).show();
            }
        });
    }
}