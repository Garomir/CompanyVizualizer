package ru.ramich.companyvizualizer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.InputStream;

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
    Bitmap foto;

    ImageView ivFoto;
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
        ivFoto = findViewById(R.id.ivFoto);

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
            getImg(workerId);
        }else if (extras.containsKey("companyId")){
            companyId = extras.getInt("companyId");
            departmentId = extras.getInt("departmentId");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.details_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.itemSave:
                if (workerId > 0){
                    updateWorker(workerId, new Worker(
                            etFirstname.getText().toString(),
                            etLastname.getText().toString(),
                            etPosition.getText().toString(),
                            Integer.parseInt(etSalary.getText().toString()),
                            etBirthday.getText().toString()));
                } else {
                    createWorker(new Worker(
                            etFirstname.getText().toString(),
                            etLastname.getText().toString(),
                            etPosition.getText().toString(),
                            Integer.parseInt(etSalary.getText().toString()),
                            etBirthday.getText().toString()));
                }
        }
        return true;
    }

    public void createWorker(Worker worker){
        Call<Worker> dep = jsonPlaceHolderApi.addWorker(companyId, departmentId, worker);

        dep.enqueue(new Callback<Worker>() {
            @Override
            public void onResponse(Call<Worker> call, Response<Worker> response) {
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
                finish();
            }

            @Override
            public void onFailure(Call<Worker> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "Error: " + t, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void getImg(int imgId) {
        Call<ResponseBody> img = jsonPlaceHolderApi.getImg(imgId);

        img.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                InputStream is = response.body().byteStream();
                foto = BitmapFactory.decodeStream(is);
                ivFoto.setImageBitmap(foto);
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "Error: " + t, Toast.LENGTH_LONG).show();
            }
        });
    }
}