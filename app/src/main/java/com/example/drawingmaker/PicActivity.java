package com.example.drawingmaker;


import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AlertDialog;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.media.Image;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.app.Dialog;
import android.view.View.OnClickListener;
import android.os.Bundle;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.File;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Objects;
import java.util.UUID;

public class PicActivity extends AppCompatActivity implements OnClickListener {

    private DBManager dbManager;
    private int[] colors;
    private float smallBrush, mediumBrush, largeBrush;
    private DrawingView drawView;
    private String tempPic = "empty";
    private String tempTitle = "empty";
    private static final int MENU_LAST = 2131230972;
    private static final int MENU_NEXT = 2131231052;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pic_activity);
        init();
    }

    private void init(){
        dbManager = new DBManager(this);
        drawView = findViewById(R.id.drawing);
        colors = new int[]{getResources().getInteger(R.integer.brown), getResources().getInteger(R.integer.red), getResources().getInteger(R.integer.orange),
                getResources().getInteger(R.integer.yellow), getResources().getInteger(R.integer.green), getResources().getInteger(R.integer.turquoise),
                getResources().getInteger(R.integer.blue), getResources().getInteger(R.integer.purple), getResources().getInteger(R.integer.coral),
                getResources().getInteger(R.integer.white), getResources().getInteger(R.integer.grey), getResources().getInteger(R.integer.black)};
        smallBrush = getResources().getInteger(R.integer.small_size);
        mediumBrush = getResources().getInteger(R.integer.medium_size);
        largeBrush = getResources().getInteger(R.integer.large_size);
        ImageButton brushButton = findViewById(R.id.draw_btn);
        brushButton.setOnClickListener(this);
        ImageButton colorButton = findViewById(R.id.colorButton);
        colorButton.setOnClickListener(this);
        ImageButton eraseButton = findViewById(R.id.erase_btn);
        eraseButton.setOnClickListener(this);
        ImageButton newButton = findViewById(R.id.new_btn);
        newButton.setOnClickListener(this);
        ImageButton saveButton = findViewById(R.id.save_btn);
        saveButton.setOnClickListener(this);
        drawView.setBrushSize(mediumBrush);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == MENU_LAST){
            drawView.undo();
        } else if (item.getItemId() == MENU_NEXT){
            drawView.redo();
        }
            return super.onOptionsItemSelected(item);

    }

    /*@Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 45) {
            if (data != null) {
                tempTitle = data.getStringExtra("pic");
                String select = "SELECT pic FROM Pictures WHERE title = " + tempTitle;
                drawView.setCanvasBitmap(openBitmap(select));
                Toast.makeText(this, select, Toast.LENGTH_LONG).show();
            }
        }
    }*/

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.draw_btn) {
            final Dialog brushDialog = new Dialog(this);
            brushDialog.setTitle("Размер кисти:");
            brushDialog.setContentView(R.layout.brush_chooser);
            ImageButton smallBtn = brushDialog.findViewById(R.id.small_brush);
            ImageButton mediumBtn = brushDialog.findViewById(R.id.medium_brush);
            ImageButton largeBtn = brushDialog.findViewById(R.id.large_brush);
            drawView.setErase(false);
            @SuppressLint("NonConstantResourceId") View.OnClickListener onClickListener = v1 -> {
                switch (v1.getId()) {
                    case R.id.small_brush:
                        drawView.setBrushSize(smallBrush);
                        drawView.setLastBrushSize(smallBrush);
                        break;
                    case R.id.medium_brush:
                        drawView.setBrushSize(mediumBrush);
                        drawView.setLastBrushSize(mediumBrush);
                        break;
                    case R.id.large_brush:
                        drawView.setBrushSize(largeBrush);
                        drawView.setLastBrushSize(largeBrush);
                        break;
                }
                brushDialog.dismiss();
            };


            smallBtn.setOnClickListener(onClickListener);
            mediumBtn.setOnClickListener(onClickListener);
            largeBtn.setOnClickListener(onClickListener);
            brushDialog.show();
        } else if (v.getId() == R.id.colorButton) {
            final Dialog colorDialog = new Dialog(this);
            colorDialog.setTitle("Выбор цвета:");
            colorDialog.setContentView(R.layout.color_chooser);
            ImageButton brownButton = colorDialog.findViewById(R.id.brownButton);
            ImageButton redButton = colorDialog.findViewById(R.id.redButton);
            ImageButton orangeButton = colorDialog.findViewById(R.id.orangeButton);
            ImageButton yellowButton = colorDialog.findViewById(R.id.yellowButton);
            ImageButton greenButton = colorDialog.findViewById(R.id.greenButton);
            ImageButton turquoiseButton = colorDialog.findViewById(R.id.turquoiseButton);
            ImageButton blueButton = colorDialog.findViewById(R.id.blueButton);
            ImageButton purpleButton = colorDialog.findViewById(R.id.purpleButton);
            ImageButton coralButton = colorDialog.findViewById(R.id.coralButton);
            ImageButton whiteButton = colorDialog.findViewById(R.id.whiteButton);
            ImageButton greyButton = colorDialog.findViewById(R.id.greyButton);
            ImageButton blackButton = colorDialog.findViewById(R.id.blackButton);

            @SuppressLint("NonConstantResourceId") View.OnClickListener onClickListener = v12 -> {
                drawView.setBrushSize(drawView.getLastBrushSize());
                drawView.setErase(false);
                switch (v12.getId()) {
                    case R.id.brownButton:
                        drawView.setPaintColor(colors[0]);
                        break;
                    case R.id.redButton:
                        drawView.setPaintColor(colors[1]);
                        break;
                    case R.id.orangeButton:
                        drawView.setPaintColor(colors[2]);
                        break;
                    case R.id.yellowButton:
                        drawView.setPaintColor(colors[3]);
                        break;
                    case R.id.greenButton:
                        drawView.setPaintColor(colors[4]);
                        break;
                    case R.id.turquoiseButton:
                        drawView.setPaintColor(colors[5]);
                        break;
                    case R.id.blueButton:
                        drawView.setPaintColor(colors[6]);
                        break;
                    case R.id.purpleButton:
                        drawView.setPaintColor(colors[7]);
                        break;
                    case R.id.coralButton:
                        drawView.setPaintColor(colors[8]);
                        break;
                    case R.id.whiteButton:
                        drawView.setPaintColor(colors[9]);
                        break;
                    case R.id.greyButton:
                        drawView.setPaintColor(colors[10]);
                        break;
                    case R.id.blackButton:
                        drawView.setPaintColor(colors[11]);
                        break;
                }
                colorDialog.dismiss();
            };
            brownButton.setOnClickListener(onClickListener);
            redButton.setOnClickListener(onClickListener);
            orangeButton.setOnClickListener(onClickListener);
            yellowButton.setOnClickListener(onClickListener);
            greenButton.setOnClickListener(onClickListener);
            turquoiseButton.setOnClickListener(onClickListener);
            blueButton.setOnClickListener(onClickListener);
            purpleButton.setOnClickListener(onClickListener);
            coralButton.setOnClickListener(onClickListener);
            whiteButton.setOnClickListener(onClickListener);
            greyButton.setOnClickListener(onClickListener);
            blackButton.setOnClickListener(onClickListener);
            colorDialog.show();
        } else if (v.getId() == R.id.erase_btn) {
            final Dialog eraseDialog = new Dialog(this);
            eraseDialog.setTitle("Размер ластика:");
            eraseDialog.setContentView(R.layout.brush_chooser);
            ImageButton smallBtn = eraseDialog.findViewById(R.id.small_brush);
            ImageButton mediumBtn = eraseDialog.findViewById(R.id.medium_brush);
            ImageButton largeBtn = eraseDialog.findViewById(R.id.large_brush);
            @SuppressLint("NonConstantResourceId") View.OnClickListener onClickListener = v1 -> {
                drawView.setErase(true);
                switch (v1.getId()) {
                    case R.id.small_brush:
                        drawView.setBrushSize(smallBrush);
                        break;
                    case R.id.medium_brush:
                        drawView.setBrushSize(mediumBrush);
                        break;
                    case R.id.large_brush:
                        drawView.setBrushSize(largeBrush);
                        break;
                }
                eraseDialog.dismiss();
            };
            smallBtn.setOnClickListener(onClickListener);
            mediumBtn.setOnClickListener(onClickListener);
            largeBtn.setOnClickListener(onClickListener);
            eraseDialog.show();
        } else if (v.getId() == R.id.new_btn) {
            AlertDialog.Builder newDialog = new AlertDialog.Builder(this);
            newDialog.setTitle(R.string.new_pic);
            newDialog.setMessage("Создать новый рисунок?\n(ты потеряешь текущий рисунок)");
            newDialog.setPositiveButton("Да", (dialog, which) -> {
                drawView.startNew();
                dialog.dismiss();
            });
            newDialog.setNegativeButton("Отмена", (dialog, which) -> dialog.cancel());
            newDialog.show();
        } else if (v.getId() == R.id.save_btn) {
            final EditText title = new EditText(this);
            AlertDialog.Builder saveDialog = new AlertDialog.Builder(this);
            saveDialog.setTitle("Сохранение рисунка");
            saveDialog.setMessage("Сохранить рисунок?");
            saveDialog.setView(title);
            saveDialog.setPositiveButton("Да", (dialog, which) -> {
                drawView.setDrawingCacheEnabled(true);
                String temp = title.getText().toString();
                tempPic = MediaStore.Images.Media.insertImage(
                        getContentResolver(), drawView.getDrawingCache(),
                        temp + ".png", "drawing");
                if (tempPic != null) {
                    dbManager.openDB();
                    dbManager.insertToDB(temp, drawView.getMoveArray(), drawView.getColorArray(), drawView.getBrushArray());
                    Toast.makeText(this, "Сохранено", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(this, "Упс.. Ошибка", Toast.LENGTH_SHORT).show();
                }
            }).setNegativeButton("Отмена", (dialog, which) -> dialog.cancel());
            saveDialog.show();
        }
    }


}