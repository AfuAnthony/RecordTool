package com.anthonyh.recordshow.audio.record;

/**
 * 录音异常
 */
public class RecordException extends Exception {

    private static final long serialVersionUID = -6235972944353069086L;

    private int code;
    private String message;

    public RecordException(int code, String message) {
        super(message);
        this.code = code;
        this.message = message;
    }

    public RecordException(String message) {
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return "code:" + code + ",message:" + message;
    }

}
