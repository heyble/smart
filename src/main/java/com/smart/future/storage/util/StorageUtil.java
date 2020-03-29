package com.smart.future.storage.util;

import com.smart.future.common.util.StringUtils;

public class StorageUtil {

    public static long parseStartPointByRange(String range) {
        if (StringUtils.isNullOrEmpty(range) || !range.startsWith("bytes=") || !range.contains("-"))
            return 0;
        String pureRange = range.substring(range.indexOf("=")+1).trim();
        if (pureRange.startsWith("-")) {
            return 0;
        }
        return Long.parseLong(pureRange.split("-")[0]);
    }

    public static long parsEndPointByRange(String range) {
        if (StringUtils.isNullOrEmpty(range) || !range.startsWith("bytes=") || !range.contains("-"))
            return -1;
        String pureRange = range.substring(range.indexOf("=")+1).trim();
        if (pureRange.endsWith("-")) {
            return -1;
        }
        final String[] pureRangeArr = range.split("-");
        return pureRangeArr.length > 1 ? Long.parseLong(pureRangeArr[1]) :  Long.parseLong(pureRangeArr[0]);
    }
}
