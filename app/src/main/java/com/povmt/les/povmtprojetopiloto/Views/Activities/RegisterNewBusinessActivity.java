package com.povmt.les.povmtprojetopiloto.Views.Activities;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.povmt.les.povmtprojetopiloto.Controllers.FirebaseController;
import com.povmt.les.povmtprojetopiloto.Interfaces.ActivityListener;
import com.povmt.les.povmtprojetopiloto.Models.ActivityItem;
import com.povmt.les.povmtprojetopiloto.R;
import com.povmt.les.povmtprojetopiloto.Views.Fragments.RegisterNewTiDialogFragment;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class RegisterNewBusinessActivity extends AppCompatActivity implements ActivityListener{

    @BindView(R.id.iv_photo_activity) ImageView mImageActivity;
    @BindView(R.id.input_name_activity_item) TextInputEditText mInputTitle;
    @BindView(R.id.input_description_activity_item) TextInputEditText mInputDescription;

    private DatabaseReference mDatabase;

    private static final int REQUEST_IMAGE_CAPTURE_CAMERA = 111;
    private static final int REQUEST_IMAGE_CAPTURE_GALLERY = 112;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_new_business);

        ButterKnife.bind(this);

        mDatabase = FirebaseDatabase.getInstance().getReference();

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Nova atividade");
        }

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close_white_24dp);
    }

    @OnClick(R.id.fab_add_photo_activity)
    public void addActivityPhoto(){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[] {
                    Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE },
                    REQUEST_IMAGE_CAPTURE_CAMERA);
        } else {
            selectImage();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_register_activity, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_add_activity){
            progressDialog = ProgressDialog.show(this, null, "Criando nova atividade");
            progressDialog.show();
            createActivity();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_IMAGE_CAPTURE_CAMERA) {
                Bundle extras = data.getExtras();
                Bitmap imageBitmap = (Bitmap) extras.get("data");
                mImageActivity.setImageBitmap(imageBitmap);
            } else if (requestCode == REQUEST_IMAGE_CAPTURE_GALLERY && data != null
                    && data.getData() != null) {

                Uri uri = data.getData();

                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                    mImageActivity.setImageBitmap(bitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_IMAGE_CAPTURE_CAMERA: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED
                        && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    selectImage();
                }
            }
        }
    }

    @Override
    public void receiverActivity(int statusCode, ActivityItem activityItem, String resp) {

    }

    @Override
    public void receiverActivity(int statusCode, List<ActivityItem> activityItems, String resp) {

    }

    @Override
    public void receiverActivity(int statusCode, String resp) {
        progressDialog.dismiss();
        if (statusCode != 200){
            Toast.makeText(this, resp, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, resp, Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void createActivity(){
        mInputTitle.setError(null);
        mInputDescription.setError(null);
        String titleActivity = mInputTitle.getText().toString();
        String descriptionActivity = mInputDescription.getText().toString();

        if(titleActivity.isEmpty()){
            mInputTitle.setError("Insira um título para a atividade");
        } else if (descriptionActivity.isEmpty()){
            mInputDescription.setError("Insira uma descrição para a atividade");
        } else {
            String imageUrl = encodeImageView(mImageActivity);
            Log.d("image", imageUrl);
            ActivityItem activityItem = new ActivityItem(titleActivity, descriptionActivity, imageUrl);

            FirebaseController.getInstance().insertActivity(activityItem, mDatabase, this);
        }
    }

    public void onLaunchCamera() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE_CAMERA);
        }
    }

    private void onLaunchGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);//
        startActivityForResult(Intent.createChooser(intent, "Select File"),REQUEST_IMAGE_CAPTURE_GALLERY);
    }

    public Uri getImageUri() {
        Bitmap inImage = ((BitmapDrawable) mImageActivity.getDrawable()).getBitmap();
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(getContentResolver(), inImage, null, null);
        return Uri.parse(path);
    }

    private String encodeImageView(ImageView imageView) {
        if (imageView.getDrawable() != null) {
            if (((BitmapDrawable) imageView.getDrawable()).getBitmap() != null) {
                Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();

                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                return Base64.encodeToString(stream.toByteArray(), Base64.DEFAULT);
            }
            return null;
        }
        return null;
    }

    private void selectImage() {
        final CharSequence[] items = { "Take Photo", "Choose from Library",
                "Cancel" };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add Photo!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (items[item].equals("Take Photo")) {
                    onLaunchCamera();
                } else if (items[item].equals("Choose from Library")) {
                    onLaunchGallery();
                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }
}
