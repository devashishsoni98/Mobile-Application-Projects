package com.example.sdcard;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.*;

public class MainActivity extends AppCompatActivity {
    EditText e1;
    Button write, read, clear;
    private static final int PERMISSION_REQUEST_CODE = 100;
    File file;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        e1 = findViewById(R.id.editText);
        write = findViewById(R.id.button);
        read = findViewById(R.id.button2);
        clear = findViewById(R.id.button3);

        // Request permissions at runtime
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE},
                    PERMISSION_REQUEST_CODE);
        }

        // Define file location using Scoped Storage (private external storage)
        file = new File(getExternalFilesDir(null), "myfile.txt");

        write.setOnClickListener(v -> writeToFile());
        read.setOnClickListener(v -> readFromFile());
        clear.setOnClickListener(v -> e1.setText(""));
    }

    // Write data to file
    private void writeToFile() {
        String message = e1.getText().toString();
        try {
            FileOutputStream fout = new FileOutputStream(file);
            fout.write(message.getBytes());
            fout.close();
            Toast.makeText(getBaseContext(), "Data Written to: " + file.getAbsolutePath(), Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Toast.makeText(getBaseContext(), e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    // Read data from file
    private void readFromFile() {
        if (!file.exists()) {
            Toast.makeText(getBaseContext(), "File not found!", Toast.LENGTH_LONG).show();
            return;
        }
        StringBuilder buf = new StringBuilder();
        try {
            FileInputStream fin = new FileInputStream(file);
            BufferedReader br = new BufferedReader(new InputStreamReader(fin));
            String message;
            while ((message = br.readLine()) != null) {
                buf.append(message).append("\n");
            }
            e1.setText(buf.toString());
            br.close();
            fin.close();
            Toast.makeText(getBaseContext(), "Data Read Successfully!", Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Toast.makeText(getBaseContext(), e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    // Handle permission request response
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission Granted!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Permission Denied! Cannot write to storage.", Toast.LENGTH_LONG).show();
            }
        }
    }
}
