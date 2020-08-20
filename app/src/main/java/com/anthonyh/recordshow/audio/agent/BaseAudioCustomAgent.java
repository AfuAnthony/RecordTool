package com.anthonyh.recordshow.audio.agent;


import android.text.TextUtils;
import android.util.Log;

import com.anthonyh.recordshow.audio.IAudioCustom;
import com.anthonyh.recordshow.util.DateUtil;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by hf on 2019-01-29.
 */

public abstract class BaseAudioCustomAgent implements IAudioCustom, Runnable {

    private final String TAG = getClass().getSimpleName();

    protected String filePath="/sdcard/recordFile";
    protected String fileName="";

    LinkedBlockingQueue<byte[]> linkedBlockingQueue = new LinkedBlockingQueue<>();

    @Override
    public void addAudioArray(byte[] audio) {
        try {
            linkedBlockingQueue.put(audio);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    @Override
    public void run() {
        if (!ensureRootPath(filePath)) {
            Log.e(TAG, "run: create path failed");
        }

        String fileRealPath=filePath+File.separator;

        if (TextUtils.isEmpty(fileName))
        {
            fileRealPath+=DateUtil.getRandFileName();
        }else {
            fileRealPath+=fileName;
        }

        File fileRecord = new File(fileRealPath);
        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = new FileOutputStream(fileRecord);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        while (!Thread.currentThread().isInterrupted()) {
            try {
                byte[] audio = linkedBlockingQueue.take();
                if (fileOutputStream != null) {
                    fileOutputStream.write(audio);
                }
                decodeAudio(audio);
            } catch (InterruptedException e) {
                e.printStackTrace();
                Thread.currentThread().interrupt();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (fileOutputStream != null) {
            try {
                fileOutputStream.flush();
                fileOutputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    protected abstract void decodeAudio(byte[] audio);

    private boolean ensureRootPath(String audioRootPath) {
        File file = new File(audioRootPath);
        if (!file.exists()) {
            boolean ret = file.mkdirs();
            return ret;
        }
        return true;
    }
}
