package com.csec467.sharedid2;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {

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

        Button btnShowFile = (Button) findViewById(R.id.btnShowData);

        btnShowFile.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ///data/data/com.csec467.sharedid1/files/example.txt
                        try {
                            FileInputStream in = new FileInputStream("/data/data/com.csec467.sharedid1/files/example.txt");
                            StringBuilder b = new StringBuilder();
                            int ch;
                            while((ch = in.read()) !=-1 ){
                                b.append((char)ch);
                            }
                            in.close();
                            Toast.makeText(getApplicationContext(), b.toString(), Toast.LENGTH_LONG).show();

                        } catch (FileNotFoundException e) {
                            throw new RuntimeException(e);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }


                    }
                }
        );
    }
}