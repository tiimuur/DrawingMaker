package com.example.drawingmaker;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.TypefaceSpan;
import android.view.View;
import android.view.View.OnClickListener;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;
import java.util.Objects;

public class MainActivity extends AppCompatActivity implements OnClickListener{
    @SuppressLint("StaticFieldLeak")
    private DBManager dbManager;
    private FloatingActionButton fab;
    RecyclerView picList;
    List<String> list;
    PicAdapter picAdapter;
    private static final int REQUEST_EXTERNAL_STORAGE = 1;


    @SuppressLint("NotifyDataSetChanged")
    ActivityResultLauncher<Intent> activityResultLauncherForNew = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
        list.clear();
        dbManager.openDB();
        list.addAll(dbManager.getTitlesFromDB());
        picAdapter.notifyDataSetChanged();
        setResult(12);
        /*if (result.getResultCode() == 78){
            Intent intent = result.getData();
            if (intent != null){
                String data = intent.getStringExtra("title");

            }
        }*/
    });
    @SuppressLint("NotifyDataSetChanged")
    ActivityResultLauncher<Intent> activityResultLauncherForOpen = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result ->  {
            list.clear();
            dbManager.openDB();
            list.addAll(dbManager.getTitlesFromDB());
            picAdapter.notifyDataSetChanged();
            setResult(45);
            /*Intent intent = new Intent();
            intent.putExtra("pic", list.get(0));*/
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        init();
    }

    private void init(){
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) !=
                PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE },
                    REQUEST_EXTERNAL_STORAGE);
        }
        fab = findViewById(R.id.fab);
        picList = findViewById(R.id.picList);
        dbManager = new DBManager(this);
        dbManager.openDB();
        list = dbManager.getTitlesFromDB();
        picAdapter = new PicAdapter(this, list, item -> activityResultLauncherForOpen.launch(new Intent(this, PicActivity.class)));
        picList.setLayoutManager(new LinearLayoutManager(this));
        picList.setAdapter(picAdapter);
        fab.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        activityResultLauncherForNew.launch(new Intent(this, PicActivity.class));
        dbManager.closeDB();
    }


    @Override
    protected void onResume() {
        super.onResume();
        dbManager.openDB();
    }
}