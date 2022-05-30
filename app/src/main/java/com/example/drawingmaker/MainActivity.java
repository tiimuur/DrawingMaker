package com.example.drawingmaker;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.SearchView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;


public class MainActivity extends AppCompatActivity implements OnClickListener{
    @SuppressLint("StaticFieldLeak")
    private DBManager dbManager;
    RecyclerView picList;
    List<ListItem> list;
    PicAdapter picAdapter;
    private static final int REQUEST_EXTERNAL_STORAGE = 1;



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
        FloatingActionButton fab = findViewById(R.id.fab);
        picList = findViewById(R.id.picList);
        dbManager = new DBManager(this);
        dbManager.openDB();
        list = dbManager.getFromDB();
        picAdapter = new PicAdapter(this, list);
        picList.setLayoutManager(new LinearLayoutManager(this));
        picList.setAdapter(picAdapter);
        itemTouchHelper().attachToRecyclerView(picList);
        fab.setOnClickListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search, menu);
        MenuItem menuItem = menu.findItem(R.id.search_view);
        SearchView searchView = (SearchView) menuItem.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                picAdapter.updateAdapter(dbManager.getFromDB(newText));
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onClick(View v) {
        startActivity(new Intent(this, PicActivity.class));
        dbManager.closeDB();
    }

    private ItemTouchHelper itemTouchHelper(){
        return new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                picAdapter.removeItem(viewHolder.getAdapterPosition(), dbManager);
            }
        });
    }


    @Override
    protected void onResume() {
        super.onResume();
        dbManager.openDB();
        picAdapter.updateAdapter(dbManager.getFromDB());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dbManager.closeDB();
    }
}