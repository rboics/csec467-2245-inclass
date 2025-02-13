package com.csec467.permissiondemo;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.File;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private String imagePath;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Button btnTakePicture = (Button)findViewById(R.id.btnTakePicture);
        btnTakePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("Debug", "onClick: 1");
                File imageFile = null;
                Intent launchCamera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                Log.d("Debug", "onClick: 2");
                if(launchCamera.resolveActivity(getPackageManager()) != null){
                    Log.d("Debug", "onClick: 3");
                    String filename="testphoto";
                    File photoLocation = getExternalFilesDir(null);
                    try {
                        Log.d("Debug", "onClick: 4");
                        imageFile = File.createTempFile(filename,".jpg", photoLocation);
                        imagePath = imageFile.getAbsolutePath();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
                else{
                    Log.d("Debug", "onClick: 3a");
                }
                if(imageFile!=null){
                    Log.d("Debug", "onClick: 5");
                    Uri photoUri = FileProvider.getUriForFile(getApplicationContext(),
                            "com.csec467.permissiondemo.fileprovider",
                            imageFile);
                    Log.d("Debug", "onClick: 6");
                    launchCamera.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                    Log.d("Debug", "onClick: 7");
                    cameralauncher.launch(launchCamera);
                    Log.d("Debug", "onClick: 8");

                }
            }
        });
    }
    private final ActivityResultLauncher<Intent> cameralauncher = registerForActivityResult(
      new ActivityResultContracts.StartActivityForResult(),
      result -> {
          if(result.getResultCode() == RESULT_OK){
              //Logic to set the image on the interface to be the picture
              ImageView img = (ImageView) findViewById(R.id.imgCapturedPhoto);
              Bitmap b = BitmapFactory.decodeFile(imagePath);
              img.setImageBitmap(b);
          }
          else{
              Toast.makeText(getApplicationContext(),"Taking the picture failed",Toast.LENGTH_LONG).show();
          }
      }
    );

}