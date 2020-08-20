package com.anthonyh.recordshow.audio.record;

import android.os.Handler;
import android.os.Looper;

import com.anthonyh.recordshow.audio.IAudioCustom;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by hf on 2019-01-25.
 */

public class DistriTask implements Runnable {
    LinkedBlockingQueue<byte[]> linkedBlockingQueue;
    Map<String, IAudioCustom> customMap;
    Handler handler = new Handler(Looper.getMainLooper());

    public DistriTask(LinkedBlockingQueue<byte[]> linkedBlockingQueue, Map<String, IAudioCustom> customMap) {
        this.linkedBlockingQueue = linkedBlockingQueue;
        this.customMap = customMap;
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                byte[] content = linkedBlockingQueue.take();
                distriContent(content);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void distriContent(final byte[] value) {
        Collection<IAudioCustom> customCollection = customMap.values();
        Iterator<IAudioCustom> it = customCollection.iterator();
        while (it.hasNext()) {
            final IAudioCustom audioCustom = it.next();
            handler.post(new Runnable() {
                @Override
                public void run() {
                    audioCustom.addAudioArray(value);
                }
            });
        }
    }
}
