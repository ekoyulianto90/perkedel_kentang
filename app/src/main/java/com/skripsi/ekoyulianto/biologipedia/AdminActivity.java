package com.skripsi.ekoyulianto.biologipedia;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.skripsi.ekoyulianto.biologipedia.Adapter.ResultAdapter;
import com.skripsi.ekoyulianto.biologipedia.Database.DatabaseHelper;
import com.skripsi.ekoyulianto.biologipedia.Result.Result;

import java.util.ArrayList;
import java.util.List;

public class AdminActivity extends AppCompatActivity {

    CoordinatorLayout coordinatorLayout;
    DatabaseHelper dbHelper;
    ListView listView;
    public static AdminActivity maa;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(AdminActivity.this, AddActivity.class);
                startActivity(i);
            }
        });

        listView = (ListView) findViewById(R.id.listAdmin);

        maa = this;
        dbHelper = new DatabaseHelper(this);

        RefreshList();
    }

    public void RefreshList(){
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM ensiklopedia ORDER BY istilah", null);
        final String[] daftar = new String[cursor.getCount()];
        cursor.moveToFirst();
        for (int cc=0; cc < cursor.getCount(); cc++)
        {
            cursor.moveToPosition(cc);
            daftar[cc] = cursor.getString(1).toString();
        }

        listView.setAdapter(new ArrayAdapter(this, android.R.layout.simple_list_item_1, daftar));
        listView.setSelected(true);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                final String selection = daftar[position];
                final CharSequence[] dialogitem = {"Lihat", "Edit", "Hapus", "Batal"};
                AlertDialog.Builder builder = new AlertDialog.Builder(AdminActivity.this);
                builder.setTitle("Pilihan");
                builder.setItems(dialogitem, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int item) {
                        switch(item){
                            case 0 :
                                Intent i2 = new Intent(getApplicationContext(), DetailAdmActivity.class);
                                i2.putExtra("istilah", selection);
                                startActivity(i2);
                                break;

                            case 1 :
                                Intent i = new Intent(getApplicationContext(), EditActivity.class);
                                i.putExtra("istilah", selection);
                                startActivity(i);
                                break;
                            case 2 :
                                SQLiteDatabase db = dbHelper.getWritableDatabase();
                                db.execSQL("delete from ensiklopedia where istilah = '"+selection+"'");
                                RefreshList();
                                Toast.makeText(getApplicationContext(), "Berhasil dihapus.", Toast.LENGTH_LONG).show();
                                break;
                            case 3 :
                                dialog.dismiss();
                                break;
                        }
                    }
                });
                builder.create().show();
            }
        });
    }
}
