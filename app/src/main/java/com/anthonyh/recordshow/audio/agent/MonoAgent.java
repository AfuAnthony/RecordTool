package com.anthonyh.recordshow.audio.agent;

import com.anthonyh.recordshow.audio.IAudioCustom;

/**
 * Created by hf on 2019-01-29.
 */

public class MonoAgent extends BaseAudioCustomAgent {
    IAudioCustom iAudioCustom;


    public MonoAgent(IAudioCustom iAudioCustom) {
        this.iAudioCustom = iAudioCustom;
    }

    @Override
    protected void decodeAudio(byte[] audio) {

        if (iAudioCustom != null) {
            iAudioCustom.addAudioArray(audio);
        }

    }
}
