package com.platform.lib.utils;

public class TTNumberUtil {

    /**
     * object转double
     *
     * @param price
     * @return
     */
    public static double getValue(Object price) {
        if (price instanceof Integer) {
            int result = (int) price;
            return (double) result;
        }

        if (price instanceof Float) {
            return (double) price;
        }

        if (price instanceof Double) {
            return (double) price;
        }

        try {
            if (price instanceof String) {
                return Double.valueOf((String) price);
            }
        } catch (Exception e) {

        }

        return 0;
    }
}
