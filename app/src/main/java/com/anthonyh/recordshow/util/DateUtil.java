package com.anthonyh.recordshow.util;

/**
 * Created by hf on 2019-01-25.
 */

public class DateUtil {
    public static String getRandFileName() {
        return System.currentTimeMillis() + "";
    }


    /////
    public static short[] byteArray2ShortArray(byte[] data, int items) {
        short[] retVal = new short[items / 2];
        try {
            for (int e = 0; e < retVal.length; ++e) {
                retVal[e] = (short) (data[e * 2] & 255 | (data[e * 2 + 1] & 255) << 8);
            }
            return retVal;
        } catch (Exception var4) {
            return retVal;
        }
    }

}
