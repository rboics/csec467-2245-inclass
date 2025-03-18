package com.csec467.detest;

import android.os.Bundle;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;


public class MainActivity extends AppCompatActivity {
    private EditText editText;
    private Button saveButton, loadButton;
    private SharedPreferences deviceEncryptedPrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        Context deviceProtectedContext = getApplicationContext().createDeviceProtectedStorageContext();
        editText = findViewById(R.id.edtData);
        saveButton = findViewById(R.id.btnEncryptData);

        // Use Device Encrypted Storage (DE)
        deviceEncryptedPrefs = deviceProtectedContext.getSharedPreferences("device_encrypted_prefs", Context.MODE_PRIVATE);

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = editText.getText().toString();
                if (!text.isEmpty()) {
                    deviceEncryptedPrefs.edit().putString("secure_text", text).apply();
                    Toast.makeText(MainActivity.this, "Data saved in device-encrypted storage!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, "Enter some text first!", Toast.LENGTH_SHORT).show();
                }
            }
        });


    }
}