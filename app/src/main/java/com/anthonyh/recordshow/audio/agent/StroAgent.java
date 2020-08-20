package com.anthonyh.recordshow.audio.agent;

import android.util.Log;

import com.anthonyh.recordshow.audio.IAudioCustom;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;

/**
 * Created by hf on 2019-01-29.
 */

public class StroAgent extends BaseAudioCustomAgent {
    IAudioCustom left, right;
    FileOutputStream fileOutputStream;

    public StroAgent(IAudioCustom left, IAudioCustom right) {
        this.left = left;
        this.right = right;
        try {
            fileOutputStream = new FileOutputStream("/sdcard/left.pcm");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private static final String TAG = "StroAgent";

    boolean leftV = true;

    @Override
    protected void decodeAudio(byte[] audio) {
        Log.e(TAG, "decodeAudio: " + audio.length);
        byte[] leftAudio = new byte[audio.length / 2];
        byte[] rightAudio = new byte[audio.length / 2];
        int index = 0;
        int leftIndex = 0;
        int rightIndex = 0;

        for (int i = 0; i < audio.length; i++) {
            index++;
            if (leftV) {
                leftAudio[leftIndex] = audio[i];
                leftIndex++;
            } else {
                rightAudio[rightIndex] = audio[i];
                rightIndex++;
            }

            if (index == 2) {
                leftV = !leftV;
                index = 0;
            }
    }
        left.addAudioArray(leftAudio);
        right.addAudioArray(rightAudio);
    }


}
