package com.skripsi.ekoyulianto.biologipedia;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

import com.skripsi.ekoyulianto.biologipedia.CustomFont.CustomTextView;
import com.skripsi.ekoyulianto.biologipedia.Database.DatabaseHelper;

import java.io.ByteArrayInputStream;

public class DetailActivity extends AppCompatActivity {

    protected Cursor cursor;
    DatabaseHelper dbHelper;

    CustomTextView txIstilah;
    CustomTextView txPengertian;
    ImageView img;
    byte[] gmbr;
    String sel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        dbHelper = new DatabaseHelper(this);

        txIstilah = (CustomTextView) findViewById(R.id.textIstilah);
        txPengertian = (CustomTextView) findViewById(R.id.textArti);
        img = (ImageView) findViewById(R.id.imageView);
        sel = getIntent().getStringExtra("istilah");

        SQLiteDatabase db = dbHelper.getReadableDatabase();

        cursor = db.rawQuery("SELECT * FROM ensiklopedia WHERE istilah = '"
                + getIntent().getStringExtra("istilah") + "'", null);

        cursor.moveToFirst();
        if (cursor.getCount() > 0) {
            cursor.moveToPosition(0);
            txIstilah.setText(cursor.getString(1));
            txPengertian.setText(cursor.getString(2));
            gmbr = cursor.getBlob(3);
            ByteArrayInputStream inputStream = new ByteArrayInputStream(gmbr);
            Bitmap theImage = BitmapFactory.decodeStream(inputStream);
            img.setImageBitmap(theImage);
        }
    }
}
