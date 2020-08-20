package com.anthonyh.recordshow.audio.record;


/**
 * 录音接口
 */
public interface IRecord {

    void init(RecordConfig recordConfig);

    /**
     * @return
     */
    byte[] read() throws RecordException;

    /**
     * 开始录音
     *
     * @throws RecordException
     */
    void start() throws RecordException;

    /**
     * 停止录音
     *
     * @throws RecordException
     */
    void stop() throws RecordException;

    /**
     * 释放资源
     */
    void release();

    /**
     * 返回录音的状态，Android上实际上是AudioRecord的getState()的状态
     *
     * @return
     */
    int getState();

}
