package com.povmt.les.povmtprojetopiloto.Views.Activities;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.kosalgeek.android.photoutil.CameraPhoto;
import com.kosalgeek.android.photoutil.GalleryPhoto;
import com.kosalgeek.android.photoutil.ImageLoader;
import com.povmt.les.povmtprojetopiloto.Controllers.FirebaseController;
import com.povmt.les.povmtprojetopiloto.Interfaces.ActivityListener;
import com.povmt.les.povmtprojetopiloto.Models.ActivityItem;
import com.povmt.les.povmtprojetopiloto.R;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class RegisterNewBusinessActivity extends AppCompatActivity implements ActivityListener{

    @BindView(R.id.iv_photo_activity) ImageView mImageActivity;
    @BindView(R.id.input_name_activity_item) TextInputEditText mInputTitle;
    @BindView(R.id.input_description_activity_item) TextInputEditText mInputDescription;

    private static final int REQUEST_PERMISSION = 111;
    private static final int REQUEST_IMAGE_CAPTURE_GALLERY = 112;
    private static final int REQUEST_IMAGE_CAPTURE_CAMERA = 113;
    private ProgressDialog progressDialog;
    private CameraPhoto cameraPhoto;
    private GalleryPhoto galleryPhoto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_new_business);

        ButterKnife.bind(this);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Nova atividade");
        }

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close_white_24dp);

        cameraPhoto = new CameraPhoto(getApplicationContext());
        galleryPhoto = new GalleryPhoto(getApplicationContext());

    }

    @OnClick(R.id.fab_add_photo_activity)
    public void addActivityPhoto(){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[] {
                    Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE }, REQUEST_PERMISSION);
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
            if(requestCode == REQUEST_IMAGE_CAPTURE_CAMERA){
                String photoPath = cameraPhoto.getPhotoPath();
                try {
                    Bitmap bitmap = ImageLoader.init().from(photoPath).requestSize(512, 512).getBitmap();
                    mImageActivity.setImageBitmap(bitmap);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            } else if (requestCode == REQUEST_IMAGE_CAPTURE_GALLERY) {
                galleryPhoto.setPhotoUri(data.getData());
                String photoPath = galleryPhoto.getPath();
                try {
                    Bitmap bitmap = ImageLoader.init().from(photoPath).requestSize(512, 512).getBitmap();
                    mImageActivity.setImageBitmap(bitmap);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_PERMISSION: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED
                        && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    selectImage();
                }
            }
        }
    }

    @Override
    public void receiverActivity(int statusCode, ActivityItem activityItem, String resp) {
        if (statusCode != 200){
            Toast.makeText(this, resp, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, resp, Toast.LENGTH_SHORT).show();
            FirebaseController.getInstance().saveImageOfActivityItem(activityItem,
                    encodeImageView(mImageActivity));
            finish();
        }
    }

    @Override
    public void receiverActivity(int statusCode, List<ActivityItem> activityItems, String resp) {

    }

    @Override
    public void receiverImageUri(Uri uri) {

    }

    @Override
    public void receiverActivity(int code, String s) {

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
            ActivityItem activityItem = new ActivityItem(titleActivity, descriptionActivity);

            FirebaseController.getInstance().insertActivity(activityItem, this);
        }
    }

    private void onLaunchCamera() {
        try {
            startActivityForResult(cameraPhoto.takePhotoIntent(), REQUEST_IMAGE_CAPTURE_CAMERA);
        } catch (IOException e) {
            e.printStackTrace();
        }
        cameraPhoto.addToGallery();
    }

    private void onLaunchGallery() {
        startActivityForResult(galleryPhoto.openGalleryIntent(), REQUEST_IMAGE_CAPTURE_GALLERY);
    }

    private byte[] encodeImageView(ImageView imageView) {
        imageView.setDrawingCacheEnabled(true);
        imageView.buildDrawingCache();
        Bitmap bitmap = imageView.getDrawingCache();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        return baos.toByteArray();
    }

    private void selectImage() {
        final CharSequence[] items = { "Tirar foto", "Escolher da galeria"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Adicionar foto");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (item == 0) {
                    onLaunchCamera();
                } else if (item == 1) {
                    onLaunchGallery();
                }
            }
        });

        builder.show();
    }
}
