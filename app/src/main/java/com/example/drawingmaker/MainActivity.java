package com.example.drawingmaker;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.view.View.OnClickListener;

import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class MainActivity extends AppCompatActivity implements OnClickListener{
    public DBManager dbManager;
    private FloatingActionButton fab;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        fab = findViewById(R.id.fab);
        RecyclerView picList = findViewById(R.id.picList);
        dbManager = new DBManager(this);
        dbManager.openDB();
        List<String> list = dbManager.getFromDB();
        PicAdapter picAdapter= new PicAdapter(this, list, new PicAdapter.OnItemClick() {
            @Override
            public void onClick(String item) {
                startActivity(new Intent(MainActivity.this, PicActivity.class));
            }
        });
        picList.setLayoutManager(new LinearLayoutManager(this));
        picList.setAdapter(picAdapter);
        fab.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        final EditText newTitle = new EditText(this);
        AlertDialog.Builder newPicDialog = new AlertDialog.Builder(this);
        newPicDialog.setTitle(R.string.new_pic);
        newPicDialog.setMessage(R.string.new_title);
        newPicDialog.setView(newTitle);
        newPicDialog.setPositiveButton("Создать", new DialogInterface.OnClickListener(){
            public void onClick(DialogInterface dialog, int which){
                String sTitle = String.valueOf(newTitle.getText());
                dbManager.insertToDB(sTitle, null);
                dbManager.closeDB();
                startActivity(new Intent(MainActivity.this, PicActivity.class));
            }
        });
        newPicDialog.setNegativeButton("Отмена", new DialogInterface.OnClickListener(){
            public void onClick(DialogInterface dialog, int which){
                dialog.cancel();
            }
        });
        newPicDialog.show();
    }


    @Override
    protected void onResume() {
        super.onResume();
        dbManager.openDB();
    }

}