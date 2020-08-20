package com.anthonyh.recordshow.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewTreeObserver;

import com.anthonyh.recordshow.R;
import com.anthonyh.recordshow.audio.IAudioCustom;
import com.anthonyh.recordshow.util.AreaBuffer;
import com.anthonyh.recordshow.util.DateUtil;

import java.util.concurrent.LinkedBlockingQueue;


public class VadView extends View implements IAudioCustom {
    private static final String TAG = "VadView";
    Paint paint;
    int audioSampleNum = 16000;
    int accuracy = 1;
    int widthPixels;
    int heightPixels;
    Bitmap bitmapCache;
    DrawThread drawThread;
    AreaBuffer areaBuffer = new AreaBuffer(6400);
    GetRecordThread thread;
    LinkedBlockingQueue<byte[]> linkedBlockingQueue = new LinkedBlockingQueue<>();


    public VadView(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.VadView);
        int color = typedArray.getColor(R.styleable.VadView_waveColor, Color.parseColor("#BF1ae8c9"));
        int strokeWidth = (int) typedArray.getDimension(R.styleable.VadView_waveWidth, 1);
        audioSampleNum = typedArray.getInteger(R.styleable.VadView_pointTotal, audioSampleNum);
        accuracy = typedArray.getInteger(R.styleable.VadView_accuracy, accuracy);
        paint = new Paint();
        paint.setColor(color);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStrokeWidth(strokeWidth);
        getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                widthPixels = getWidth();
                heightPixels = getHeight();
                if (bitmapCache == null) {
                    bitmapCache = Bitmap.createBitmap(widthPixels, heightPixels, Bitmap.Config.ARGB_4444);
                }
            }
        });
    }

    boolean isStart = false;

    public void startShow() {
        stopShow();
        isStart = true;
        invalidate();
        if (thread == null) {
            thread = new GetRecordThread();
            thread.start();
        }
        if (drawThread == null) {
            drawThread = new DrawThread();
            drawThread.start();
        }
    }

    public void stopShow() {
        isStart = false;
        if (thread != null) {
            try {
                thread.interrupt();
            } catch (Exception e) {

            } finally {
                thread = null;
            }
        }
        if (drawThread != null) {
            try {
                drawThread.interrupt();
            } catch (Exception e) {

            } finally {
                drawThread = null;
            }
        }
        if (bitmapCache != null) {
            bitmapCache.recycle();
        }
    }

    @Override
    public void addAudioArray(byte[] audio) {
        if (linkedBlockingQueue != null) {
            try {
                linkedBlockingQueue.put(audio);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }


    class GetRecordThread extends Thread {
        @Override
        public void run() {
            while (!isInterrupted()) {
                try {
                    byte[] value = linkedBlockingQueue.take();
                    areaBuffer.put(value);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    class DrawThread extends Thread {
        int readCount = 256;

        @Override
        public void run() {
            while (!isInterrupted()) {
                byte[] data = areaBuffer.get(readCount);
                if (data == null) {
                    continue;
                }

                short[] value = DateUtil.byteArray2ShortArray(data, data.length);
                drawBitmap(value);
            }
        }
    }

    private void drawBitmap(short audio[]) {
        if (widthPixels == 0 || heightPixels == 0) {
            return;
        }
        if (audio == null) {
            return;
        }
        Bitmap bitmap = Bitmap.createBitmap(widthPixels, heightPixels, Bitmap.Config.ARGB_4444);
        Canvas canvas = new Canvas(bitmap);
        float moveDistance = (float) audio.length / audioSampleNum * widthPixels;
        //往左边移动audio长度一样的宽度
        if (this.bitmapCache != null && !this.bitmapCache.isRecycled()) {
            canvas.drawBitmap(this.bitmapCache, -moveDistance, 0, paint);
        }
        //把新的线条画到最右边
        float[] pointAdd = new float[audio.length * 4];
        for (int i = 0; i < audio.length - 1; i += accuracy) {
            pointAdd[4 * i] = (float) i / audioSampleNum * widthPixels + widthPixels - moveDistance;//本来的比例，再加上左边被移动的距离
            pointAdd[4 * i + 1] = heightPixels / 2 + (float) audio[i] / 32768 * heightPixels / 2;
            pointAdd[4 * i + 2] = (float) (i + 1) / audioSampleNum * widthPixels + widthPixels - moveDistance;
            pointAdd[4 * i + 3] = heightPixels / 2 + (float) audio[i + 1] / 32768 * heightPixels / 2;
        }
        canvas.drawLines(pointAdd, paint);
        //保存上一帧的Bitmap用作下一帧的缓存
        this.bitmapCache = bitmap;
        canvas.save();
        canvas.restore();
        postInvalidate();
    }

    protected void onDraw(final Canvas canvas) {
        super.onDraw(canvas);
        if (bitmapCache != null && !bitmapCache.isRecycled()) {
            canvas.drawBitmap(bitmapCache, 0, 0, null);
        }
    }
}