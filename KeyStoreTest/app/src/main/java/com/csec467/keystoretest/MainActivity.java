package com.csec467.keystoretest;

import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.KeyStore;
import java.security.KeyStore.SecretKeyEntry;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;


public class MainActivity extends AppCompatActivity {
    private static final String KEY_ALIAS = "MySecureKey";
    private static final String FILE_NAME = "data.txt";
    private static final int GCM_TAG_LENGTH = 128;

    private EditText editText;
    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editText = findViewById(R.id.edtData);
        Button saveButton = findViewById(R.id.btnEncryptData);
        Button loadButton = findViewById(R.id.btnDecryptData);

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String inputText = editText.getText().toString();
                try {
                    String encryptedData = encryptData(inputText);
                    Toast.makeText(v.getContext(),encryptedData,Toast.LENGTH_LONG).show();
                    saveToFile(encryptedData);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        loadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    String encryptedData = readFromFile();
                    if (encryptedData != null) {
                        String decryptedText = decryptData(encryptedData);
                        Toast.makeText(getApplicationContext(),decryptedText,Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(getApplicationContext(),"No data",Toast.LENGTH_LONG).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(),"Error decrypting",Toast.LENGTH_LONG).show();
                }
            }
        });

        try {
            generateSecretKey();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Generates an AES encryption key using Android Keystore if it doesn't already exist.
     */
    private void generateSecretKey() throws Exception {
        KeyStore keyStore = KeyStore.getInstance("AndroidKeyStore");
        keyStore.load(null);

        if (!keyStore.containsAlias(KEY_ALIAS)) {
            KeyGenerator keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore");
            keyGenerator.init(
                    new KeyGenParameterSpec.Builder(KEY_ALIAS,
                            KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT)
                            .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                            .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                            .setKeySize(256)
                            .build()
            );
            keyGenerator.generateKey();
        }
    }

    /**
     * Retrieves the stored AES key from Android Keystore.
     */
    private SecretKey getSecretKey() throws Exception {
        KeyStore keyStore = KeyStore.getInstance("AndroidKeyStore");
        keyStore.load(null);
        return ((SecretKeyEntry) keyStore.getEntry(KEY_ALIAS, null)).getSecretKey();
    }

    /**
     * Encrypts the input data using AES-GCM encryption.
     */
    private String encryptData(String plaintext) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        SecretKey secretKey = getSecretKey();

        // Let the system generate the IV automatically
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);

        byte[] iv = cipher.getIV(); // Retrieve the auto-generated IV
        byte[] ciphertext = cipher.doFinal(plaintext.getBytes(StandardCharsets.UTF_8));

        // Combine IV and ciphertext
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        outputStream.write(iv);
        outputStream.write(ciphertext);

        return Base64.encodeToString(outputStream.toByteArray(), Base64.DEFAULT);
    }


    /**
     * Decrypts the input data using AES-GCM encryption.
     */
    private String decryptData(String encryptedData) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        SecretKey secretKey = getSecretKey();

        byte[] combined = Base64.decode(encryptedData, Base64.DEFAULT);

        // Extract IV and ciphertext
        byte[] iv = new byte[12]; // IV size for AES-GCM
        byte[] ciphertext = new byte[combined.length - 12];

        System.arraycopy(combined, 0, iv, 0, 12);
        System.arraycopy(combined, 12, ciphertext, 0, ciphertext.length);

        // Use the extracted IV for decryption
        cipher.init(Cipher.DECRYPT_MODE, secretKey, new GCMParameterSpec(128, iv));

        byte[] plaintext = cipher.doFinal(ciphertext);
        return new String(plaintext, StandardCharsets.UTF_8);
    }
    /**
     * Saves encrypted data to a private file in internal storage.
     */
    private void saveToFile(String data) {
        try (FileOutputStream fos = openFileOutput(FILE_NAME, MODE_PRIVATE);
             OutputStreamWriter writer = new OutputStreamWriter(fos)) {
            writer.write(data);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    /**
     * Reads encrypted data from the private file.
     */
    private String readFromFile() {
        StringBuilder sb = new StringBuilder();
        try (FileInputStream fis = openFileInput(FILE_NAME);
             InputStreamReader isr = new InputStreamReader(fis);
             BufferedReader reader = new BufferedReader(isr)) {
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
        } catch (IOException e) {
            return null; // File not found or empty
        }
        return sb.toString();
    }
}