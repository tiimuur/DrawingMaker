package com.example.drawingmaker;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AlertDialog;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.app.Dialog;
import android.view.View.OnClickListener;
import android.os.Bundle;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.io.InputStream;

public class PicActivity extends AppCompatActivity implements OnClickListener {

    private DBManager dbManager;
    private int[] colors;
    private DrawingView drawView;
    private String tempPic = "empty";
    private String tempTitle;
    private static final int MENU_LAST = 2131230974;
    private static final int MENU_NEXT = 2131231052;
    private Drawable savedPic;

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
        int startBrush = getResources().getInteger(R.integer.medium_size);
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
        drawView.setBrushSize(startBrush);
        resumePainting();
    }

    public void resumePainting(){
        Intent intent = getIntent();
        if (intent != null){
            ListItem item = (ListItem) intent.getSerializableExtra(Constants.LIST_ITEM_KEY);
            boolean isNew = intent.getBooleanExtra(Constants.NEW_KEY, true);
            if(!isNew){
                try {
                    Uri uri = Uri.parse(item.getPic());
                    InputStream inputStream = getContentResolver().openInputStream(uri);
                    savedPic = Drawable.createFromStream(inputStream, uri.toString());
                } catch (FileNotFoundException e) {
                    Log.d("someTAG", e.getMessage());
                }
                drawView.setBackground(savedPic);
                System.out.println(item.getPic());
            }
        }
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


    @SuppressLint({"SetTextI18n", "ResourceAsColor"})
    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.draw_btn) {
            final Dialog brushDialog = new Dialog(this);
            brushDialog.setTitle(getString(R.string.brush_size));
            brushDialog.setContentView(R.layout.brush_chooser);
            final TextView text_brush_size = brushDialog.findViewById(R.id.brush_size);
            final TextView text_alpha = brushDialog.findViewById(R.id.brush_opacity);
            final SeekBar seek_brush_size = brushDialog.findViewById(R.id.brush_size_seek);
            final SeekBar seek_alpha = brushDialog.findViewById(R.id.brush_opacity_seek);
            int currBrush = drawView.getSeekBrushSize();
            seek_brush_size.setMax(50);
            text_brush_size.setText(currBrush + getString(R.string.dp));
            seek_brush_size.setProgress(currBrush);
            seek_brush_size.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @SuppressLint("SetTextI18n")
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    text_brush_size.setText(progress + getString(R.string.dp));
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                }
            });
            int currAlpha = drawView.getPaintAlpha();
            seek_alpha.setMax(100);
            text_alpha.setText(currAlpha + "%");
            seek_alpha.setProgress(currAlpha);
            seek_alpha.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    text_alpha.setText(progress + "%");
                    drawView.setErase(false);
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                }
            });
            Button brushButtonChooser = brushDialog.findViewById(R.id.ok);
            brushButtonChooser.setOnClickListener(v1 -> {
                drawView.setBrushSize(seek_brush_size.getProgress());
                drawView.setPaintAlpha(seek_alpha.getProgress());
                brushDialog.dismiss();
            });
            brushDialog.show();
        } else if (v.getId() == R.id.colorButton) {
            final Dialog colorDialog = new Dialog(this);
            colorDialog.setTitle(R.string.color_choosing);
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
            drawView.setErase(true);
        } else if (v.getId() == R.id.new_btn) {
            AlertDialog.Builder newDialog = new AlertDialog.Builder(this);
            newDialog.setTitle(getString(R.string.new_pic));
            newDialog.setMessage(getString(R.string.deleting));
            newDialog.setPositiveButton(R.string.yes, (dialog, which) -> {
                drawView.setBackgroundColor(0);
                drawView.startNew();
                dialog.dismiss();
            });
            newDialog.setNegativeButton(R.string.cancel, (dialog, which) -> dialog.cancel());
            newDialog.show();
        } else if (v.getId() == R.id.save_btn) {
            final EditText editableTitle = new EditText(this);
            AlertDialog.Builder saveDialog = new AlertDialog.Builder(this);
            saveDialog.setTitle(getString(R.string.saving_pic));
            saveDialog.setView(editableTitle);
            editableTitle.setHint(getString(R.string.hint));
            saveDialog.setPositiveButton(getString(R.string.ok), (dialog, which) -> {
                tempTitle = editableTitle.getText().toString();
                drawView.setDrawingCacheEnabled(true);
                tempPic = MediaStore.Images.Media.insertImage(
                      getContentResolver(), drawView.getDrawingCache(),
                      tempTitle + ".png", "drawing");
                if (tempPic != null) {
                    dbManager.openDB();
                  try {
                      dbManager.insertToDB(tempTitle, tempPic);
                      Toast.makeText(this, getString(R.string.saved), Toast.LENGTH_LONG).show();

                  } catch (Exception e){
                      Toast.makeText(this, getString(R.string.error) + e.getMessage(), Toast.LENGTH_SHORT).show();
                  }

                } else {
                    Toast.makeText(this, getString(R.string.error), Toast.LENGTH_SHORT).show();
                }
            }).setNegativeButton(getString(R.string.cancel), (dialog, which) -> dialog.cancel());
            saveDialog.show();
        }
    }
}