package com.smart.future.common.constant;

import com.google.common.collect.Lists;

import java.util.List;

public interface ResourceCode {

    String VIDEO = "video";

    String AUDIO = "audio";

    String IMAGE = "image";

    List<String> videos = Lists.newArrayList("mp4", "avi");

    List<String> audios = Lists.newArrayList("mp3", "wav");

    List<String> images = Lists.newArrayList("jpg", "png");

    static boolean isVideo(String suffixName) {
        return videos.contains(suffixName.toLowerCase());
    }

    default boolean isImage(String suffixName) {
        return images.contains(suffixName.toLowerCase());
    }

    static String getResourceCode(String suffixName) {
        final String suxName = suffixName.toLowerCase();
        if (videos.contains(suxName)) {
            return VIDEO;
        } else if (images.contains(suxName)) {
            return IMAGE;
        } else if (audios.contains(suffixName)) {
            return AUDIO;
        } else {
            return "";
        }
    }
}
