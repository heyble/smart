package com.smart.future.storage.vo;

import org.springframework.web.multipart.MultipartFile;

public class ChunkVO {
    private Long id;
    /**
     * 当前文件块，从0开始
     */
    private Integer chunkNumber;
    /**
     * 分块大小
     */
    private Long chunkSize;
    /**
     * 当前分块大小
     */
    private Long currentChunkSize;

    /**
     * 文件标识
     */
    private String identifier;
    /**
     * 文件名
     */
    private String fileName;
    /**
     * 总块数
     */
    private Integer totalChunks;

    private String md5;

    private String chunkMD5;

    public String getMd5() {
        return md5;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }

    public String getChunkMD5() {
        return chunkMD5;
    }

    public void setChunkMD5(String chunkMD5) {
        this.chunkMD5 = chunkMD5;
    }

    private MultipartFile file;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getChunkNumber() {
        return chunkNumber;
    }

    public void setChunkNumber(Integer chunkNumber) {
        this.chunkNumber = chunkNumber;
    }

    public Long getChunkSize() {
        return chunkSize;
    }

    public void setChunkSize(Long chunkSize) {
        this.chunkSize = chunkSize;
    }

    public Long getCurrentChunkSize() {
        return currentChunkSize;
    }

    public void setCurrentChunkSize(Long currentChunkSize) {
        this.currentChunkSize = currentChunkSize;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public Integer getTotalChunks() {
        return totalChunks;
    }

    public void setTotalChunks(Integer totalChunks) {
        this.totalChunks = totalChunks;
    }

    public MultipartFile getFile() {
        return file;
    }

    public void setFile(MultipartFile file) {
        this.file = file;
    }
}
