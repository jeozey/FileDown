package com.jeo.filedown.util;

/**
 * Created by 志文 on 2015/11/13 0013.
 */
public class SpeedUtil {
    public static String getSpeed(long speed) {
        long s = speed / (1024);
        if (s > 1024) {
            return String.format("%d M/s", new Object[]{s / 1024});
        } else {
            return String.format("%d kb/s", new Object[]{s});
        }
    }
}
