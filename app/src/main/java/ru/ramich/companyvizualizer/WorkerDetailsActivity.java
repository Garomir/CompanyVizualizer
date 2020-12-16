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

    private static final int REQUEST_ID_READ_WRITE_PERMISSION = 99;

    private int workerId, workerSalary, companyId, departmentId;
    private String workerFN, workerLN, workerPosition, workerBirth;
    Bitmap foto;
    boolean isFotoChanged = false;
    static final int GALLERY_REQUEST = 1;

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
                    if (isFotoChanged){
                        askPermissionAndCaptureFoto(workerId);
                    }
                } else {
                    createWorker(new Worker(
                            etFirstname.getText().toString(),
                            etLastname.getText().toString(),
                            etPosition.getText().toString(),
                            Integer.parseInt(etSalary.getText().toString()),
                            etBirthday.getText().toString()));
                    if (isFotoChanged){
                        askPermissionAndCaptureFoto(workerId);
                    }
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

    private void uploadImg(int imgId) throws IOException {
        Call<Void> img = jsonPlaceHolderApi.uploadImg(imgId, getMultipart(foto));

        img.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                Toast.makeText(getApplicationContext(), "Foto uploaded!", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "Error: " + t, Toast.LENGTH_LONG).show();
            }
        });
    }

    public MultipartBody.Part getMultipart(Bitmap bitmap) throws IOException {
        String file_path = Environment.getDownloadCacheDirectory() +
                "/PhysicsSketchpad";
        File dir = new File(file_path);
        if(!dir.exists())
            dir.mkdirs();
        File file = new File(dir, "sketchpad" + ".jpg");
        FileOutputStream fOut = null;
        fOut = new FileOutputStream(file);
        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, fOut);
        fOut.flush();
        fOut.close();
        RequestBody reqFile = RequestBody.create(MediaType.parse("image/*"), file);
        MultipartBody.Part body = MultipartBody.Part.createFormData("file", file.getName(), reqFile);
        return body;
    }

    public void onClickImg(View view) {
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, GALLERY_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
        switch(requestCode) {
            case GALLERY_REQUEST:
                if(resultCode == RESULT_OK){
                    Uri selectedImage = imageReturnedIntent.getData();
                    try {
                        foto = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImage);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    ivFoto.setImageBitmap(foto);
                    isFotoChanged = true;
                }
        }
    }

    private void askPermissionAndCaptureFoto(int id) {
        if (android.os.Build.VERSION.SDK_INT >= 23) {

            // Check if we have read/write permission
            int readPermission = ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.READ_EXTERNAL_STORAGE);
            int writePermission = ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE);

            if (writePermission != PackageManager.PERMISSION_GRANTED ||
                    readPermission != PackageManager.PERMISSION_GRANTED) {
                // If don't have permission so prompt the user.
                this.requestPermissions(
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                Manifest.permission.READ_EXTERNAL_STORAGE},
                        REQUEST_ID_READ_WRITE_PERMISSION
                );
                return;
            }
        }
        try {
            uploadImg(id);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}