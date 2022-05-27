package com.example.drawingmaker;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.util.Log;
import android.view.View;
import android.content.Context;
import android.util.AttributeSet;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.view.MotionEvent;
import android.util.TypedValue;
import android.widget.Toast;

import androidx.annotation.NonNull;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Arrays;

public class DrawingView extends View {

    private DBManager dbManager;
    private Path drawPath;
    private Paint drawPaint, canvasPaint;
    private int paintColor, lastPaintColor;
    private Canvas drawCanvas;
    private Bitmap canvasBitmap;
    private float brushSize, lastBrushSize;
    private boolean erase = false;
    private final ArrayList<Path> moveList =  new ArrayList<>();
    private final ArrayList<Path> undoList =  new ArrayList<>();
    private final ArrayList<Path> currentMoveList =  new ArrayList<>();
    private final ArrayList<Integer> colorList = new ArrayList<>();
    private final ArrayList<Integer> currentColorList = new ArrayList<>();
    private final ArrayList<Integer> undoColorList = new ArrayList<>();
    private final ArrayList<Float> brushList = new ArrayList<>();
    private final ArrayList<Float> currentBrushList = new ArrayList<>();
    private final ArrayList<Float> undoBrushList = new ArrayList<>();



    public DrawingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setupDrawing();
    }


    private void setupDrawing() {
        drawPath = new Path();
        drawPaint = new Paint();
        paintColor = 0xFF000000;
        drawPaint.setColor(paintColor);
        drawPaint.setAntiAlias(true);
        drawPaint.setStrokeWidth(20);
        drawPaint.setStyle(Paint.Style.STROKE);
        drawPaint.setStrokeJoin(Paint.Join.ROUND);
        drawPaint.setStrokeCap(Paint.Cap.ROUND);
        canvasPaint = new Paint(Paint.DITHER_FLAG);
        brushSize = getResources().getInteger(R.integer.medium_size);
        lastBrushSize = brushSize;
        drawPaint.setStrokeWidth(brushSize);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldW, int oldH) {
        super.onSizeChanged(w, h, oldW, oldH);
        canvasBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        drawCanvas = new Canvas(canvasBitmap);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        for (int i = 0; i < currentMoveList.size(); i++) {
            if (currentColorList.size() != 0)
                drawPaint.setColor(currentColorList.get(i));
            if (currentBrushList.size() != 0)
                drawPaint.setStrokeWidth(currentBrushList.get(i));
            Path path = currentMoveList.get(i);
            canvas.drawPath(path, drawPaint);
        }
            for (int i = 0; i < moveList.size(); i++) {
                if (colorList.size() != 0)
                    drawPaint.setColor(colorList.get(i));
                if (brushList.size() != 0)
                    drawPaint.setStrokeWidth(brushList.get(i));
                Path path = moveList.get(i);
                canvas.drawPath(path, drawPaint);
            }
    }

    public void setLastBrushSize(float lastSize){
        lastBrushSize=lastSize;
    }

    public float getLastBrushSize(){
        return lastBrushSize;
    }

    public void setBrushSize(float newSize) {
        brushSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                newSize, getResources().getDisplayMetrics());
        drawPaint.setStrokeWidth(brushSize);
        drawPath = new Path();
    }


    public void setPaintColor(int color){
        drawPaint.setColor(color);
        paintColor = color;
        lastPaintColor = color;
        drawPath = new Path();
    }

    public void setErase(boolean erase) {
        this.erase = erase;
        if (erase){
            setPaintColor(getResources().getInteger(R.integer.white));
        } else {
            setPaintColor(paintColor);
        }

    }

    public void startNew(){
        drawCanvas.drawColor(0, PorterDuff.Mode.CLEAR);
        moveList.clear();
        colorList.clear();
        brushList.clear();
        invalidate();
    }

    public void undo() {
        if (moveList.size() > 0) {
            undoList.add(moveList.remove(moveList.size() - 1));
            undoColorList.add(colorList.remove(colorList.size() - 1));
            undoBrushList.add(brushList.remove(brushList.size() - 1));
            invalidate();
        }
    }

    public void redo() {
        if (undoList.size() > 0) {
            moveList.add(undoList.remove(undoList.size() - 1));
            colorList.add(undoColorList.remove(undoColorList.size() - 1));
            brushList.add(undoBrushList.remove(undoBrushList.size() - 1));
            invalidate();
        }
    }

    public byte[] getMoveArray(){
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream out = new ObjectOutputStream(baos);
            out.writeObject(moveList);
            return baos.toByteArray();
        } catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public byte[] getColorArray(){
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream out = new ObjectOutputStream(baos);
            out.writeObject(colorList);
            return baos.toByteArray();
        } catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public byte[] getBrushArray(){
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream out = new ObjectOutputStream(baos);
            out.writeObject(brushList);
            return baos.toByteArray();
        } catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    private void readFromByteArray(String title) throws IOException, ClassNotFoundException {
        ByteArrayInputStream baisMove = new ByteArrayInputStream(dbManager.getMoveListFromDB(title));
        ByteArrayInputStream baisColor = new ByteArrayInputStream(dbManager.getMoveListFromDB(title));
        ByteArrayInputStream baisBrush = new ByteArrayInputStream(dbManager.getMoveListFromDB(title));
        ArrayList<Path> moveListFromDB = new ArrayList<>();
        ArrayList<Integer> colorListFromDB = new ArrayList<>();
        ArrayList<Float> brushListFromDb = new ArrayList<>();
        while (baisMove.available() > 0) {
            ObjectInputStream inM = new ObjectInputStream(baisMove);
            ObjectInputStream inC = new ObjectInputStream(baisColor);
            ObjectInputStream inB = new ObjectInputStream(baisBrush);
            Path path = (Path) inM.readObject();
            Integer intgr = (Integer) inC.readObject();
            Float flt = (Float) inB.readObject();
            moveListFromDB.add(path);
            colorListFromDB.add(intgr);
            brushListFromDb.add(flt);
        }
        moveList.addAll(moveListFromDB);
        colorList.addAll(colorListFromDB);
        brushList.addAll(brushListFromDb);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float touchX = event.getX();
        float touchY = event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                drawPath.moveTo(touchX, touchY);
                drawPath.lineTo(touchX, touchY);
                break;
            case MotionEvent.ACTION_MOVE:
                drawPath.lineTo(touchX, touchY);
                currentMoveList.add(drawPath);
                currentColorList.add(paintColor);
                currentBrushList.add(brushSize);
                break;
            case MotionEvent.ACTION_UP:
                drawPath.lineTo(touchX, touchY);
                drawCanvas.drawPath(drawPath, drawPaint);
                moveList.add(drawPath);
                colorList.add(paintColor);
                brushList.add(brushSize);
                drawPath = new Path();
                currentMoveList.clear();
                currentColorList.clear();
                currentBrushList.clear();
                break;
            default:
                return false;
        }
        invalidate();
        return true;
    }

}
