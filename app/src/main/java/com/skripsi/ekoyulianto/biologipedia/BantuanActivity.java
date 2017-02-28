package com.skripsi.ekoyulianto.biologipedia;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class BantuanActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bantuan);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
}
