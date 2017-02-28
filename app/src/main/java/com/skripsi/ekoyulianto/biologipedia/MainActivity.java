package com.skripsi.ekoyulianto.biologipedia;

import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.skripsi.ekoyulianto.biologipedia.Adapter.ResultAdapter;
import com.skripsi.ekoyulianto.biologipedia.Database.DatabaseHelper;
import com.skripsi.ekoyulianto.biologipedia.Result.Result;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {


    public static MainActivity ma;
    DatabaseHelper dbHelper;
    ListView listView;
    ImageButton imageButtonSpeech, imageButtonSearch;
    EditText editTextCari;
    List<Result> resultList = new ArrayList<>();
    List<Result> resultList2 = new ArrayList<>();
    Result resultArray;
    int jumB = 0;
    DrawerLayout drawer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        toggle.setDrawerIndicatorEnabled(false);

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        editTextCari = (EditText) findViewById(R.id.edt_tool_search);
        imageButtonSearch = (ImageButton) findViewById(R.id.imgBtnSrc);
        imageButtonSpeech = (ImageButton) findViewById(R.id.imgBtnSpch);
        listView = (ListView) findViewById(R.id.listView1);

        ma = this;
        dbHelper = new DatabaseHelper(this);
        dbHelper.openDataBase();
        RefreshList();


        imageButtonSpeech.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(
                        RecognizerIntent.ACTION_RECOGNIZE_SPEECH);

                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, "id-ID");

                try {
                    startActivityForResult(intent, 1);

                    EditText editText1 = (EditText) findViewById(R.id.edt_tool_search);
                    editText1.setText("");
                } catch (ActivityNotFoundException a) {
                    Toast t = Toast.makeText(getApplicationContext(),
                            "Perangkat Tidak Mendukung", Toast.LENGTH_SHORT);
                    t.show();
                }
            }
        });

        imageButtonSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resultList2.clear();
                String pat = editTextCari.getText().toString().toLowerCase();
                double time_start = System.currentTimeMillis();
                startSearch(pat);

                double time_end = System.currentTimeMillis();
                double total_time = time_end - time_start;
                String temp = String.valueOf(total_time);
                if (jumB > 0) {
                    Snackbar.make(v, jumB + " istilah ditemukan dalam waktu " + temp + " ms.", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                } else {
                    Snackbar.make(v, "Data tidak ditemukan.", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
            }
        });
    }

    public void startSearch(String pattern) {

        String txt, txtIstlh, txtArti;
        jumB = 0;
        dbHelper.openDataBase();
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM ensiklopedia ORDER BY istilah", null);
        if (cursor.moveToFirst()) {
            try {
                do {
                    txt = txtIstlh = cursor.getString(1);
                    txtArti = cursor.getString(2);
                    findPattern(txt, pattern, txtIstlh, txtArti);
                } while (cursor.moveToNext());
            } catch (Exception e) {
                Log.e("Print", "catch startSearch");
            }
        }

        listView.setAdapter(new ResultAdapter(this, R.layout.list_item, resultList2));
        listView.setSelected(true);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final String selection = resultList2.get(position).getIstilah();
                Intent i = new Intent(getApplicationContext(), DetailActivity.class);
                i.putExtra("istilah", selection);
                startActivity(i);
            }
        });
        ((ResultAdapter) listView.getAdapter()).notifyDataSetInvalidated();
        db.close();
    }

    static String txtB;
    static String patB;

    static String IstilahB;
    static String ArtiB;

    public void findPattern(String t, String p, String txI, String txA) {
        txtB = t;
        patB = p;
        IstilahB = txI;
        ArtiB = txA;

        char[] text = t.toCharArray();
        char[] pattern = p.toCharArray();
        int pos = indexOf(text, pattern);
        if (pos != -1) {
            Result resultArr2 = new Result();
            resultArr2.setIstilah(IstilahB);
            resultArr2.setDeskripsi(ArtiB);
            resultList2.add(resultArr2);
            jumB++;
        }
    }

    public int indexOf(char[] text, char[] pattern) {
        if (pattern.length == 0)
            return 0;
        int charTable[] = makeCharTable(pattern);
        int offsetTable[] = makeOffsetTable(pattern);
        for (int i = pattern.length - 1, j; i < text.length; ) {
            for (j = pattern.length - 1; pattern[j] == text[i]; --i, --j)
                if (j == 0)
                    return i;
            i += Math.max(offsetTable[pattern.length - 1 - j],
                    charTable[text[i]]);
        }
        return -1;
    }

    /**
     * bad character shift
     **/

    private int[] makeCharTable(char[] pattern) {
        final int ALPHABET_SIZE = 256;
        int[] table = new int[ALPHABET_SIZE];
        for (int i = 0; i < table.length; ++i)
            table[i] = pattern.length;
        for (int i = 0; i < pattern.length - 1; ++i)
            table[pattern[i]] = pattern.length - 1 - i;
        return table;
    }

    /**
     * good-suffix shift
     **/

    private static int[] makeOffsetTable(char[] pattern) {
        int[] table = new int[pattern.length];
        int lastPrefixPosition = pattern.length;
        for (int i = pattern.length - 1; i >= 0; --i) {
            if (isPrefix(pattern, i + 1))
                lastPrefixPosition = i + 1;
            table[pattern.length - 1 - i] = lastPrefixPosition - i
                    + pattern.length - 1;
        }
        for (int i = 0; i < pattern.length - 1; ++i) {
            int slen = suffixLength(pattern, i);
            table[slen] = pattern.length - 1 - i + slen;
        }
        return table;
    }


    private static boolean isPrefix(char[] pattern, int p) {
        for (int i = p, j = 0; i < pattern.length; ++i, ++j)
            if (pattern[i] != pattern[j])
                return false;
        return true;
    }


    private static int suffixLength(char[] pattern, int p) {
        int len = 0;
        for (int i = p, j = pattern.length - 1; i >= 0
                && pattern[i] == pattern[j]; --i, --j)
            len += 1;
        return len;
    }

    public void RefreshList() {

        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM ensiklopedia ORDER BY istilah", null);
        if (cursor.moveToFirst()) {
            try {
                do {
                    resultArray = new Result();
                    resultArray.setIstilah(cursor.getString(1));
                    resultArray.setDeskripsi(cursor.getString(2));
                    resultList.add(resultArray);
                } while (cursor.moveToNext());
            } catch (Exception e) {
                Log.e("Print", "catch startSearch");
            }
        }

        listView.setAdapter(new ResultAdapter(this, R.layout.list_item, resultList));
        listView.setSelected(true);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final String selection = resultList.get(position).getIstilah();
                Intent i = new Intent(getApplicationContext(), DetailActivity.class);
                i.putExtra("istilah", selection);
                startActivity(i);
            }
        });
        ((ResultAdapter) listView.getAdapter()).notifyDataSetInvalidated();
        db.close();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case 1: {
                if (resultCode == RESULT_OK && null != data) {

                    ArrayList<String> text = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);

                    EditText editText1 = (EditText) findViewById(R.id.edt_tool_search);
                    editText1.setText(text.get(0));
                    resultList2.clear();
                    String pat = editText1.getText().toString().toLowerCase();
                    double time_start = System.currentTimeMillis();
                    startSearch(pat);

                    double time_end = System.currentTimeMillis();
                    double total_time = time_end - time_start;
                    String temp = String.valueOf(total_time);
                    if (jumB > 0) {
                        Toast.makeText(getApplicationContext(), jumB + " istilah ditemukan dalam waktu " + temp + " ms.", Toast.LENGTH_LONG).show();

                    } else {
                        Toast.makeText(getApplicationContext(), "Data tidak ditemukan", Toast.LENGTH_LONG).show();
                        
                    }
                }
                break;
            }
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.END)) {
            drawer.closeDrawer(GravityCompat.END);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.menuKanan) {
            if (drawer.isDrawerOpen(Gravity.RIGHT)) {
                drawer.closeDrawer(Gravity.RIGHT);
            } else {
                drawer.openDrawer(Gravity.RIGHT);
            }
            return true;
        } else if (id == R.id.refreshDB) {
            editTextCari.setText("");
            resultList.clear();
            RefreshList();
            resultList2.clear();
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_bantuan) {
            Intent i = new Intent(MainActivity.this, BantuanActivity.class);
            startActivity(i);
        } else if (id == R.id.nav_tentang) {
            Intent i = new Intent(MainActivity.this, TentangActivity.class);
            startActivity(i);
        } else if (id == R.id.nav_admin) {
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            final LayoutInflater inflater = MainActivity.this.getLayoutInflater();
            final View view = inflater.inflate(R.layout.admin_login, null);

            builder.setTitle("Administrator");
            builder.setView(view)
                    .setPositiveButton("Masuk", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            // sign in the user ...
                            EditText pass = (EditText) view.findViewById(R.id.password);
                            String pwd = pass.getText().toString();

                            if (pwd.equals("asd")) {
                                Intent i = new Intent(MainActivity.this, AdminActivity.class);
                                startActivity(i);
                            } else if (pwd.equals("")){
                                Toast.makeText(getApplicationContext(), "Password tidak boleh kosong!", Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(getApplicationContext(), "Password salah!", Toast.LENGTH_LONG).show();
                            }
                        }
                    })
                    .setNegativeButton("Kembali", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.dismiss();
                        }
                    });
            builder.create().show();
        } else if (id == R.id.nav_keluar) {
            finish();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.END);
        return true;
    }
}
