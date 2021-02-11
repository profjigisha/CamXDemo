package com.jk.camxdemo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

import com.google.common.util.concurrent.ListenableFuture;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

public class CamXActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "CamXActivity";
    private PreviewView previewView;
    private ImageButton btnCameraCapture;
    private ImageCapture imageCapture = null;
    private Uri resultPicUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cam_x);

        this.btnCameraCapture = findViewById(R.id.btnCameraCapture);
        this.btnCameraCapture.setOnClickListener(this);

        this.previewView = findViewById(R.id.preview_view_container);

        this.startCamera();
    }

    @Override
    public void onClick(View view) {
        if (view != null){
            if (view.getId() == R.id.btnCameraCapture){
                this.takePhoto();
            }
        }
    }

    private void startCamera(){
        this.imageCapture = new ImageCapture.Builder().build();

        ListenableFuture<ProcessCameraProvider> cameraProviderFuture = ProcessCameraProvider.getInstance(this);

        cameraProviderFuture.addListener(() -> {
            try{
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
                bindPreview(cameraProvider);
            }catch (InterruptedException | ExecutionException ex){
                //This should never be reached
                Log.e(TAG, ex.getLocalizedMessage());
            }
        }, ContextCompat.getMainExecutor(this));
    }

    private void bindPreview(ProcessCameraProvider cameraProvider){
        Preview preview = new Preview.Builder().build();

        CameraSelector cameraSelector = new CameraSelector.Builder().requireLensFacing(CameraSelector.LENS_FACING_BACK).build();

        preview.setSurfaceProvider(previewView.getSurfaceProvider());

        try {
            cameraProvider.unbindAll();
            cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageCapture);
        }catch (Exception ex){
            Log.e(TAG, "Use-case binding failed " + ex.getLocalizedMessage());
        }
    }

    private void takePhoto(){
        if (imageCapture != null){

            File outputDirectory = this.getOutputDirectory();
            String filename = "CamX_" +
                    new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss-SSS", Locale.CANADA).format(System.currentTimeMillis()) +
                    ".jpeg";
            File pictureFile = new File(outputDirectory, filename);

            ImageCapture.OutputFileOptions outputFileOptions = new ImageCapture.OutputFileOptions.Builder(pictureFile).build();

            imageCapture.takePicture(outputFileOptions,
                    ContextCompat.getMainExecutor(this),
                    new ImageCapture.OnImageSavedCallback() {
                        @Override
                        public void onImageSaved(@NonNull ImageCapture.OutputFileResults outputFileResults) {
                            Log.e(TAG, "Image is saved at " + Uri.fromFile(pictureFile));
                            resultPicUri = Uri.fromFile(pictureFile);

                            Intent resultIntent = new Intent();
                            resultIntent.putExtra("EXTRA_PICTURE_URI", resultPicUri);
                            setResult(RESULT_OK, resultIntent);
                            finish();

//                            file:///storage/emulated/0/Android/media/com.jk.camxdemo/MyCam/CamX_2021-02-10-20-26-23-856.jpeg
                        }

                        @Override
                        public void onError(@NonNull ImageCaptureException exception) {
                            Log.e(TAG, "Image saving failed : " + exception.getLocalizedMessage());

                            Intent resultIntent = new Intent();
                            setResult(RESULT_CANCELED, resultIntent);
                            finish();
                        }
                    });

        }else{
            Log.e(TAG, "ImageCapture use case is unavailable. Cannot click picture");
        }
    }

    private File getOutputDirectory(){
        if (this.getExternalMediaDirs().length > 0){

            //shared storage
            File camDir = new File(this.getExternalMediaDirs()[0], "MyCam");

            if (camDir.exists()){
                return camDir;
            }else if (camDir.mkdir()){
                return camDir;
            }
        }else{
            //app storage
            return this.getFilesDir();
        }

        return null;
    }
}