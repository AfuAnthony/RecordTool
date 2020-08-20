package com.anthonyh.recordshow.audio.record;


import android.util.Log;

import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by hf on 2019-01-25.
 */

public class RecordTask implements Runnable {
    private static final String TAG = "RecordTask";
    IRecord record;
    LinkedBlockingQueue<byte[]> linkedBlockingQueue;
    RecordConfig recordConfig;

    public RecordTask(RecordConfig recordConfig, IRecord record, LinkedBlockingQueue<byte[]> linkedBlockingQueue) {
        this.record = record;
        this.linkedBlockingQueue = linkedBlockingQueue;
        this.recordConfig = recordConfig;
    }

    @Override
    public void run() {
        record.init(recordConfig);
        try {
            record.start();
        } catch (RecordException e) {
            e.printStackTrace();
        }

        while (!Thread.currentThread().isInterrupted()) {
            try {
                byte[] content = record.read();
                Log.e(TAG, "run: " + content.length);
                linkedBlockingQueue.put(content);
            } catch (RecordException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
                Thread.currentThread().interrupt();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        try {
            record.stop();
            record.release();
        } catch (RecordException e) {
            e.printStackTrace();
        }
    }
}
