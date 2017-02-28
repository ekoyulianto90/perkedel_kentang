package com.skripsi.ekoyulianto.biologipedia;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.skripsi.ekoyulianto.biologipedia.Database.DatabaseHelper;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class EditActivity extends AppCompatActivity {

    DatabaseHelper dbHelper;
    protected Cursor cursor;

    EditText editIstilah, editDeskripsi;
    ImageView imgView;
    Button buttonLoad, buttonUpdate;
    byte[] gmbr;

    private static int RESULT_LOAD_IMG = 1;
    final int REQUEST_CODE_GALLERY = 999;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        dbHelper = new DatabaseHelper(this);

        init();
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        cursor = db.rawQuery("SELECT * FROM ensiklopedia WHERE istilah = '" + getIntent().getStringExtra("istilah") + "'", null);
        if (cursor.getCount()>0) {
            cursor.moveToPosition(0);
            editIstilah.setText(cursor.getString(1));
            editDeskripsi.setText(cursor.getString(2));
            gmbr = cursor.getBlob(3);
            ByteArrayInputStream inputStream = new ByteArrayInputStream(gmbr);
            Bitmap theImage = BitmapFactory.decodeStream(inputStream);
            imgView.setImageBitmap(theImage);
        }
        buttonLoad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityCompat.requestPermissions(
                        EditActivity.this,
                        new String[] {Manifest.permission.READ_EXTERNAL_STORAGE},
                        REQUEST_CODE_GALLERY
                );
            }
        });

        buttonUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    updateData(
                            editIstilah.getText().toString().trim(),
                            editDeskripsi.getText().toString().trim(),
                            imageViewToByte(imgView),
                            getIntent().getStringExtra("istilah")
                    );
                    Toast.makeText(getApplicationContext(), "Berhasil", Toast.LENGTH_LONG).show();
                    AdminActivity.maa.RefreshList();
                    finish();
                } catch (Exception e){
                    e.printStackTrace();
                }
            }
        });

    }

    private void updateData(String istilah, String deskripsi, byte[] image, String stringExtra) {

        SQLiteDatabase db = dbHelper.getWritableDatabase();

        String sql = "UPDATE ensiklopedia SET istilah=?, pengertian=?, image=? WHERE istilah=?";
        SQLiteStatement statement = db.compileStatement(sql);
        statement.clearBindings();

        statement.bindString(1, istilah);
        statement.bindString(2, deskripsi);
        statement.bindBlob(3, image);
        statement.bindString(4, stringExtra);

        statement.executeUpdateDelete();
    }

    private byte[] imageViewToByte(ImageView image) {
        Bitmap bitmap = ((BitmapDrawable) image.getDrawable()).getBitmap();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();
        return byteArray;
    }

    public void init() {
        editIstilah = (EditText) findViewById(R.id.edIstilah);
        editDeskripsi = (EditText) findViewById(R.id.edDeskripsi);
        imgView = (ImageView) findViewById(R.id.imgRes);
        buttonLoad = (Button) findViewById(R.id.btnPickImg);
        buttonUpdate = (Button) findViewById(R.id.btnSimpan);
    }

    private void retrieve() {

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CODE_GALLERY){
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, REQUEST_CODE_GALLERY);
            } else {
                Toast.makeText(getApplicationContext(), "Tidak Diijinkan", Toast.LENGTH_LONG).show();
            }
            return;
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == REQUEST_CODE_GALLERY && resultCode == RESULT_OK && data != null){
            Uri uri = data.getData();

            try{
                InputStream inputStream = getContentResolver().openInputStream(uri);
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                imgView.setImageBitmap(bitmap);
            }catch (FileNotFoundException e){
                e.printStackTrace();
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

}
