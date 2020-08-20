package com.anthonyh.recordshow.audio.record;

import android.text.TextUtils;
import android.util.Log;

import com.anthonyh.recordshow.audio.IAudioCustom;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by hf on 2019-01-25.
 */

public class RecordManger {

    private static final RecordManger ourInstance = new RecordManger();

    Future recordFuture;
    DistriTask distriTask;
    LinkedBlockingQueue<byte[]> linkedBlockingQueue = new LinkedBlockingQueue<>();
    ConcurrentHashMap<String, IAudioCustom> customMap;
    ExecutorService executorService;
    RecordState recordState = RecordState.NONE;

    public enum RecordState {
        RECORDING,
        NONE
    }


    public static RecordManger getInstance() {
        return ourInstance;
    }

    private RecordManger() {
        customMap = new ConcurrentHashMap<>();
        executorService = Executors.newFixedThreadPool(2);
        startDistr();
    }

    private void startDistr() {
        distriTask = new DistriTask(linkedBlockingQueue, customMap);
        executorService.submit(distriTask);
    }


    public RecordState getRecordState() {
        return recordState;
    }

    public void registAudioCustom(String key, IAudioCustom value) {
        if (customMap != null) {
            if (!TextUtils.isEmpty(key) && value != null)
                customMap.put(key, value);
        }
    }

    public void unRegistAudioCustom(String key) {
        if (!TextUtils.isEmpty(key)) {
            if (customMap.containsKey(key)) {
                customMap.remove(key);
            }
        }
    }

    private static final String TAG = "RecordManger";

    public synchronized void startRecord(RecordConfig recordConfig, IRecord record) throws RecordException {
        Log.e(TAG, "startRecord: ");
        recordState = RecordState.RECORDING;
        RecordTask recordTask = new RecordTask(recordConfig, record, linkedBlockingQueue);
        recordFuture = executorService.submit(recordTask);
    }

    public synchronized void stopRecord() {
        recordState = RecordState.NONE;
        if (recordFuture != null) {
            recordFuture.cancel(true);
        }
    }




}
