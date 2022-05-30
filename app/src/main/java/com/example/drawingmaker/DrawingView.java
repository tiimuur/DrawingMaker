package com.example.drawingmaker;


import android.annotation.SuppressLint;
import android.graphics.PorterDuff;
import android.view.View;
import android.content.Context;
import android.util.AttributeSet;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.view.MotionEvent;
import android.util.TypedValue;

import java.util.ArrayList;

public class DrawingView extends View {

    private Path drawPath;
    private Paint drawPaint;
    private int paintColor, paintAlpha;
    private Canvas drawCanvas;
    private float brushSize;
    private final ArrayList<Path> moveList =  new ArrayList<>();
    private final ArrayList<Path> undoList =  new ArrayList<>();
    private final ArrayList<Path> currentMoveList =  new ArrayList<>();
    private final ArrayList<Integer> colorList = new ArrayList<>();
    private final ArrayList<Integer> currentColorList = new ArrayList<>();
    private final ArrayList<Integer> undoColorList = new ArrayList<>();
    private final ArrayList<Float> brushList = new ArrayList<>();
    private final ArrayList<Float> currentBrushList = new ArrayList<>();
    private final ArrayList<Float> undoBrushList = new ArrayList<>();
    private final ArrayList<Integer> alphaList = new ArrayList<>();
    private final ArrayList<Integer> currentAlphaList = new ArrayList<>();
    private final ArrayList<Integer> undoAlphaList = new ArrayList<>();



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
        brushSize = getResources().getInteger(R.integer.medium_size);
        paintAlpha = 255;
        drawPaint.setStrokeWidth(brushSize);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldW, int oldH) {
        super.onSizeChanged(w, h, oldW, oldH);
        Bitmap canvasBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        drawCanvas = new Canvas(canvasBitmap);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        System.out.println(currentBrushList + "\n" + brushList);
        for (int i = 0; i < currentMoveList.size(); i++) {
            if (currentColorList.size() != 0)
                drawPaint.setColor(currentColorList.get(i));
            if (currentBrushList.size() != 0)
                drawPaint.setStrokeWidth(currentBrushList.get(i));
            if (currentAlphaList.size() != 0)
                drawPaint.setAlpha(currentAlphaList.get(i));
            Path path = currentMoveList.get(i);
            canvas.drawPath(path, drawPaint);
        }
        for (int i = 0; i < moveList.size(); i++) {
            if (colorList.size() != 0)
                drawPaint.setColor(colorList.get(i));
            if (brushList.size() != 0)
                drawPaint.setStrokeWidth(brushList.get(i));
            if (alphaList.size() != 0)
                drawPaint.setAlpha(alphaList.get(i));
            Path path = moveList.get(i);
            canvas.drawPath(path, drawPaint);
        }
    }

    public int getPaintAlpha(){
        return Math.round((float)paintAlpha/255*100);
    }

    public void setPaintAlpha(int newAlpha){
        paintAlpha=Math.round((float)newAlpha/100*255);
        drawPaint.setAlpha(paintAlpha);
    }


    public int getSeekBrushSize(){
        return Math.round((float) brushSize / 275 * 100);
    }

    public void setBrushSize(float newSize) {
        brushSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                newSize, getResources().getDisplayMetrics());
        if (brushList.size() > 0)
            drawPaint.setStrokeWidth(brushList.get(brushList.size() - 1));
        else
            drawPaint.setStrokeWidth(brushSize);
        drawPath = new Path();
    }

    public void setPaintColor(int color){
        drawPaint.setColor(color);
        paintColor = color;
        drawPath = new Path();
    }

    public void setErase(boolean erase) {
        if (erase){
            setPaintColor(getResources().getInteger(R.integer.white));
            setPaintAlpha(100);
        } else {
            setPaintColor(paintColor);
        }
    }

    public void startNew(){
        drawCanvas.drawColor(0, PorterDuff.Mode.CLEAR);
        moveList.clear();
        colorList.clear();
        brushList.clear();
        alphaList.clear();
        invalidate();
    }

    public void undo() {
        if (moveList.size() > 0) {
            undoList.add(moveList.remove(moveList.size() - 1));
            undoColorList.add(colorList.remove(colorList.size() - 1));
            undoBrushList.add(brushList.remove(brushList.size() - 1));
            undoAlphaList.add(alphaList.remove(alphaList.size() - 1));
            invalidate();
        }
    }

    public void redo() {
        if (undoList.size() > 0) {
            moveList.add(undoList.remove(undoList.size() - 1));
            colorList.add(undoColorList.remove(undoColorList.size() - 1));
            brushList.add(undoBrushList.remove(undoBrushList.size() - 1));
            alphaList.add(undoAlphaList.remove(undoAlphaList.size() - 1));
            invalidate();
        }
    }

    private void touchStart (float x, float y) {
        drawPath = new Path();
        moveList.add(drawPath);
        colorList.add(paintColor);
        brushList.add(brushSize);
        alphaList.add(paintAlpha);
        drawPath.reset();
        drawPath.moveTo(x, y);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float touchX = event.getX();
        float touchY = event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                touchStart(touchX, touchY);
                break;
            case MotionEvent.ACTION_MOVE:
                drawPath.lineTo(touchX, touchY);
                currentMoveList.add(drawPath);
                currentColorList.add(paintColor);
                currentBrushList.add(brushSize);
                currentAlphaList.add(paintAlpha);
                break;
            case MotionEvent.ACTION_UP:
                drawPath.lineTo(touchX, touchY);
                drawCanvas.drawPath(drawPath, drawPaint);
                drawPath = new Path();
                currentMoveList.clear();
                currentColorList.clear();
                currentBrushList.clear();
                currentAlphaList.clear();
                break;
            default:
                return false;
        }
        invalidate();
        return true;
    }

}
