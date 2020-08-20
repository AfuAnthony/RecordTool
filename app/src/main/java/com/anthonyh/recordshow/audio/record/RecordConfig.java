package com.anthonyh.recordshow.audio.record;

/**
 * Created by hf on 2019-01-29.
 */

public class RecordConfig {


    int audioSource;
    int sampleRateInHz;
    int channelConfig;
    int audioFormat;
    int bufferSizeInBytes;
    int readBufferSize;

    public int getAudioSource() {
        return audioSource;
    }

    public void setAudioSource(int audioSource) {
        this.audioSource = audioSource;
    }

    public int getSampleRateInHz() {
        return sampleRateInHz;
    }

    public void setSampleRateInHz(int sampleRateInHz) {
        this.sampleRateInHz = sampleRateInHz;
    }

    public int getChannelConfig() {
        return channelConfig;
    }

    public void setChannelConfig(int channelConfig) {
        this.channelConfig = channelConfig;
    }

    public int getAudioFormat() {
        return audioFormat;
    }

    public void setAudioFormat(int audioFormat) {
        this.audioFormat = audioFormat;
    }

    public int getBufferSizeInBytes() {
        return bufferSizeInBytes;
    }

    public void setBufferSizeInBytes(int bufferSizeInBytes) {
        this.bufferSizeInBytes = bufferSizeInBytes;
    }

    public int getReadBufferSize() {
        return readBufferSize;
    }

    public void setReadBufferSize(int readBufferSize) {
        this.readBufferSize = readBufferSize;
    }

    private RecordConfig(Builder builder) {
        audioSource = builder.audioSource;
        sampleRateInHz = builder.sampleRateInHz;
        channelConfig = builder.channelConfig;
        audioFormat = builder.audioFormat;
        bufferSizeInBytes = builder.bufferSizeInBytes;
        readBufferSize = builder.readBufferSize;
    }


    public static final class Builder {
        private int audioSource;
        private int sampleRateInHz;
        private int channelConfig;
        private int audioFormat;
        private int bufferSizeInBytes;
        private int readBufferSize;

        public Builder() {
        }

        public Builder audioSource(int val) {
            audioSource = val;
            return this;
        }

        public Builder sampleRateInHz(int val) {
            sampleRateInHz = val;
            return this;
        }

        public Builder channelConfig(int val) {
            channelConfig = val;
            return this;
        }

        public Builder audioFormat(int val) {
            audioFormat = val;
            return this;
        }

        public Builder bufferSizeInBytes(int val) {
            bufferSizeInBytes = val;
            return this;
        }

        public Builder readBufferSize(int val) {
            readBufferSize = val;
            return this;
        }

        public RecordConfig build() {
            return new RecordConfig(this);
        }
    }
}
