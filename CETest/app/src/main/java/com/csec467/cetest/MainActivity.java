package com.csec467.cetest;

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
import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKeys;

import java.io.IOException;
import java.security.GeneralSecurityException;

public class MainActivity extends AppCompatActivity {

    private EditText editText;
    private Button saveButton;
    private SharedPreferences securePrefs;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);


        editText = findViewById(R.id.edtData);
        saveButton = findViewById(R.id.btnEncryptFile);

        try {
            securePrefs = getSecurePreferences();
        } catch (GeneralSecurityException | IOException e) {
            Log.e("SecureStorage", "Error initializing EncryptedSharedPreferences", e);
            Toast.makeText(this, "Security Error!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Load saved data
        String savedText = securePrefs.getString("secureData", "");
        editText.setText(savedText);

        saveButton.setOnClickListener(view -> {
            String textToSave = editText.getText().toString();
            saveSecureData(textToSave);
        });
    }
    private SharedPreferences getSecurePreferences() throws GeneralSecurityException, IOException {
        Context credentialStorageContext = createDeviceProtectedStorageContext();
        credentialStorageContext.moveSharedPreferencesFrom(this, "secure_prefs");

        String masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC);
        return EncryptedSharedPreferences.create(
                "secure_prefs",
                masterKeyAlias,
                credentialStorageContext,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        );
    }

    private void saveSecureData(String data) {
        SharedPreferences.Editor editor = securePrefs.edit();
        editor.putString("secureData", data);
        editor.apply();
        Toast.makeText(this, "Data Saved Securely!", Toast.LENGTH_SHORT).show();
    }
}