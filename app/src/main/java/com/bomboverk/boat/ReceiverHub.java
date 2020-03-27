package com.bomboverk.boat;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

public class ReceiverHub extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        Uri data = intent.getData();

        if (intent.getType().contains("image/")) {
            Toast.makeText(this, "IMAGE", Toast.LENGTH_LONG).show();
        } else if (intent.getType().contains("text/plain")) {
            Toast.makeText(this, "TEXT", Toast.LENGTH_LONG).show();
        }else{
            Toast.makeText(this, ""+intent.getType(), Toast.LENGTH_LONG).show();
        }
    }
}
