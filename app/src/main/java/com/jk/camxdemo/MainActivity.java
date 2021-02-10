package com.jk.camxdemo;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener  {

    private final String TAG = this.getClass().getCanonicalName();
    private ImageView imgPicture;
    private Button btnGetPicture;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.imgPicture = findViewById(R.id.imgProfilePic);

        btnGetPicture = findViewById(R.id.btnGetPicture);
        btnGetPicture.setOnClickListener(this);
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

                                    break;
                                case 1:
                                    Log.d(TAG, "Getting Picture drom Gallery");
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

}