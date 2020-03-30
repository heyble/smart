package com.smart.future.nostart;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;

public class NoStartTest {

    @Test
    public void currTimeTest(){
        System.out.println(System.currentTimeMillis());
    }

    @Test
    public void md5Test() throws IOException, NoSuchAlgorithmException {
        // final File file = new File("D:\\upload\\real\\Blood.Diamond.2006.血钻.双语字幕.HR-HDTV.AC3.960X40.x264.avi");
        // final String md5 = FileMD5Util.getMD5(file);
        // System.out.println(md5);
    }
}
