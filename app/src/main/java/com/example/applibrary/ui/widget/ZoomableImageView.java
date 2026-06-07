package com.example.applibrary.ui.widget;

import android.content.Context;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;

public class ZoomableImageView extends AppCompatImageView {

    private final Matrix matrix = new Matrix();
    private final float[] matrixValues = new float[9];
    private final PointF lastTouch = new PointF();

    private float minScale = 1f;
    private float maxScale = 4f;
    private float currentScale = 1f;

    private ScaleGestureDetector scaleDetector;
    private GestureDetector gestureDetector;

    public ZoomableImageView(Context context) {
        super(context);
        init(context);
    }

    public ZoomableImageView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        setScaleType(ScaleType.MATRIX);
        scaleDetector = new ScaleGestureDetector(context, new ScaleListener());
        gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onDoubleTap(MotionEvent e) {
                float target = currentScale > 1.5f ? minScale : 2.5f;
                float factor = target / currentScale;
                matrix.postScale(factor, factor, e.getX(), e.getY());
                currentScale = target;
                clampScale();
                setImageMatrix(matrix);
                return true;
            }
        });
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (changed && getDrawable() != null) {
            resetMatrix();
        }
    }

    public void resetMatrix() {
        matrix.reset();
        if (getDrawable() == null) {
            setImageMatrix(matrix);
            currentScale = minScale;
            return;
        }
        int viewW = getWidth() - getPaddingLeft() - getPaddingRight();
        int viewH = getHeight() - getPaddingTop() - getPaddingBottom();
        int drawableW = getDrawable().getIntrinsicWidth();
        int drawableH = getDrawable().getIntrinsicHeight();
        if (viewW <= 0 || viewH <= 0 || drawableW <= 0 || drawableH <= 0) {
            return;
        }
        float scale = Math.min((float) viewW / drawableW, (float) viewH / drawableH);
        float dx = (viewW - drawableW * scale) / 2f + getPaddingLeft();
        float dy = (viewH - drawableH * scale) / 2f + getPaddingTop();
        matrix.setScale(scale, scale);
        matrix.postTranslate(dx, dy);
        currentScale = minScale = scale;
        maxScale = scale * 4f;
        setImageMatrix(matrix);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        scaleDetector.onTouchEvent(event);
        gestureDetector.onTouchEvent(event);
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                lastTouch.set(event.getX(), event.getY());
                break;
            case MotionEvent.ACTION_MOVE:
                if (!scaleDetector.isInProgress() && currentScale > minScale) {
                    float dx = event.getX() - lastTouch.x;
                    float dy = event.getY() - lastTouch.y;
                    matrix.postTranslate(dx, dy);
                    lastTouch.set(event.getX(), event.getY());
                    setImageMatrix(matrix);
                }
                break;
            default:
                break;
        }
        return true;
    }

    private void clampScale() {
        matrix.getValues(matrixValues);
        float scale = matrixValues[Matrix.MSCALE_X];
        if (scale < minScale) {
            float factor = minScale / scale;
            matrix.postScale(factor, factor, getWidth() / 2f, getHeight() / 2f);
            currentScale = minScale;
        } else if (scale > maxScale) {
            float factor = maxScale / scale;
            matrix.postScale(factor, factor, getWidth() / 2f, getHeight() / 2f);
            currentScale = maxScale;
        } else {
            currentScale = scale;
        }
    }

    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            float factor = detector.getScaleFactor();
            float newScale = currentScale * factor;
            if (newScale < minScale * 0.9f || newScale > maxScale * 1.1f) {
                return false;
            }
            matrix.postScale(factor, factor, detector.getFocusX(), detector.getFocusY());
            currentScale = newScale;
            setImageMatrix(matrix);
            return true;
        }
    }
}
