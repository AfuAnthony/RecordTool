package com.anthonyh.recordshow.audio.record;

import android.media.AudioRecord;
import android.util.Log;


public class DefaultRecord implements IRecord {
    private static final String TAG = DefaultRecord.class.getSimpleName();
    private AudioRecord audioRecord;

    private int readBufferSize;

    @Override
    public synchronized void init(RecordConfig recordConfig) {
        audioRecord = new AudioRecord(recordConfig.audioSource, recordConfig.sampleRateInHz, recordConfig.channelConfig, recordConfig.audioFormat, recordConfig.bufferSizeInBytes);
        Log.e(TAG, "init: " + (audioRecord.getState() == AudioRecord.STATE_INITIALIZED));
        this.readBufferSize = recordConfig.readBufferSize;
    }

    private byte[] buffer;

    @Override
    public synchronized byte[] read() throws RecordException {
        buffer = new byte[readBufferSize];
        if (readBufferSize <= 0) {
            throw new RecordException(ErrorCodes.ERROR_RECORD_STATE, "record readBufferSize is less than 0");
        }
        if (getState() != 1) {
            throw new RecordException(ErrorCodes.ERROR_RECORD_STATE, "audioRecord is UNINITIALIZED");
        }
//        Log.e(TAG, "read: " + readBufferSize);
        int code = audioRecord.read(buffer, 0, buffer.length);
//        Log.e(TAG, "run: " + code);
        if (code >= 0) {
            return buffer;
        } else {
            throw new RecordException(ErrorCodes.ERROR_RECORD_DATA, "record buffer is null");
        }
    }

    @Override
    public synchronized void start() throws RecordException {
        if (getState() != 1) {
            throw new RecordException(ErrorCodes.ERROR_RECORD_STATE, "audioRecord is UNINITIALIZED");
        }
        audioRecord.startRecording();
    }

    @Override
    public synchronized void stop() throws RecordException {
        if (audioRecord == null || audioRecord.getRecordingState() != AudioRecord.RECORDSTATE_RECORDING) {
            throw new RecordException(ErrorCodes.ERROR_RECORD_STATE, "audioRecord is not running");
        }
        audioRecord.stop();
    }

    @Override
    public synchronized void release() {
        audioRecord.release();
        audioRecord = null;
    }

    @Override
    public synchronized int getState() {
        if (audioRecord == null) {
            return 0;
        }
        return audioRecord.getState();
    }

}
