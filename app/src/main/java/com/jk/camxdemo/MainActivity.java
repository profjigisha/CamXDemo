package com.jk.camxdemo;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener  {

    private final String TAG = this.getClass().getCanonicalName();
    private ImageView imgPicture;
    private Button btnGetPicture;

    private final int GALLERY_PICTURE_REQUEST_CODE = 102;
    private final int CAMERA_PERMISSION_REQUEST_CODE = 103;
    private final String[] permissionsArray = new String[]{
            Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.imgPicture = findViewById(R.id.imgProfilePic);

        btnGetPicture = findViewById(R.id.btnGetPicture);
        btnGetPicture.setOnClickListener(this);

        this.checkPermissions();
    }

    @Override
    public void onClick(View view) {
        if (view != null) {

            switch (view.getId()) {
                case R.id.btnGetPicture: {

                    AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);

                    String[] actionItems = new String[]{getString(R.string.take_picture), getString(R.string.choose_from_gallery), getString(R.string.cancel)};
                    alertBuilder.setTitle("Select Profile Picture");

                    alertBuilder.setItems(actionItems, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int index) {
                            switch (index){
                                case 0:
                                    Log.d(TAG, "Taking new picture");
                                    openCamera();
                                    break;
                                case 1:
                                    Log.d(TAG, "Getting Picture from Gallery");
                                    selectFromGallery();
                                    break;
                                case 2:
                                    dialogInterface.dismiss();
                                default:
                                    dialogInterface.dismiss();
                            }
                        }
                    });

                    alertBuilder.show();
                }
                break;
            }
        }
    }

    private void selectFromGallery(){
        Intent pickPhotoIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        startActivityForResult(pickPhotoIntent, GALLERY_PICTURE_REQUEST_CODE);
    }

    private void openCamera(){

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK && requestCode == GALLERY_PICTURE_REQUEST_CODE && data != null){
            this.imgPicture.setImageURI(data.getData());
        }
    }

    private void checkPermissions(){
        if (!(ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) &&
        !(ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)){

            this.btnGetPicture.setEnabled(false);
            ActivityCompat.requestPermissions(this, this.permissionsArray, this.CAMERA_PERMISSION_REQUEST_CODE);
        }else{
            Log.e(TAG, "Camera permission granted");
            Log.e(TAG, "External storage write permission granted");
            this.btnGetPicture.setEnabled(true);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            this.btnGetPicture.setEnabled(true);
        }
    }
}