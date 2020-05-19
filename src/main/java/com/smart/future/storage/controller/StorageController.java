package com.smart.future.storage.controller;

import com.smart.future.common.constant.SmartCode;
import com.smart.future.common.exception.ApplicationException;
import com.smart.future.common.exception.SmartApplicationException;
import com.smart.future.common.vo.ResponseVO;
import com.smart.future.storage.service.IStorageService;
import com.smart.future.storage.util.StorageUtil;
import com.smart.future.storage.vo.ChunkVO;
import com.smart.future.storage.vo.FileVO;
import org.apache.catalina.connector.ClientAbortException;
import org.apache.tomcat.util.http.fileupload.servlet.ServletFileUpload;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

@RestController
@RequestMapping("/storage")
@CrossOrigin(origins = "*")
public class StorageController {

    @Autowired
    private IStorageService storageService;

    @RequestMapping(value = "/upload",method = RequestMethod.POST)
    public ResponseVO<?> upload(MultipartFile file){
        try {
            FileVO fileVO = storageService.upload(file);
            return ResponseVO.okWithData(fileVO);
        } catch (SmartApplicationException e) {
            return ResponseVO.error(e.getCode(), e.getMessage());
        }
    }

    @RequestMapping(value = "/uploadFile", method = RequestMethod.POST)
    public ResponseVO<?> uploadFile(HttpServletRequest request, ChunkVO chunk) throws ApplicationException {
        if (ServletFileUpload.isMultipartContent(request)) {
            storageService.uploadFile2Temp(chunk);
            return ResponseVO.ok();
        } else {
            return ResponseVO.error(SmartCode.Storage.REQUEST_ERROR, "不支持的请求类型");
        }
        // TODO 全局异常处理
    }

    @RequestMapping(value = "/mergeFile", method = RequestMethod.POST)
    public ResponseVO<?> mergeFile(ChunkVO chunk) throws ApplicationException {
        return ResponseVO.okWithData(storageService.mergeFile4Temp(chunk));
    }

    @RequestMapping("/download")
    public void download(HttpServletRequest request, HttpServletResponse response,
                         @RequestHeader(required = false) String range,
                         @RequestParam("filePath") String filePath) throws ApplicationException {
        // 加载需要下载的文件
        File file = storageService.loadFile(filePath);
        final long fileLength = file.length();
        // 计算下载文件的开始位置和结束位置
        // 1 没有请求头Range:bytes
        // 2 bytes=0-
        // 3 bytes=-100
        // 4 bytes=0-100
        long startByte = StorageUtil.parseStartPointByRange(range);
        long endByte = StorageUtil.parsEndPointByRange(range);
        if (endByte == -1L) endByte = fileLength - 1;

        // 文件名
        String fileName = file.getName();
        // 文件类型
        String contentType = request.getServletContext().getMimeType(fileName);

        // 各种响应头设置
        // 坑爹地方一：非一次性下载完响应状态为206
        int status = (startByte == 0 && endByte == fileLength - 1) ? 200 : 206;
        // 要下载的长度（endByte为总长度-1，这时候要加回去）
        long contentLength = endByte - startByte + 1;
        setResponse(response, fileLength, startByte, endByte, fileName, contentType, status, contentLength);

        // 将文件写入Response响应给客户端
        writeFile2Response(response, file, startByte, endByte, contentLength);
    }

    private void writeFile2Response(HttpServletResponse response, File file, long startByte, long endByte, long contentLength) throws SmartApplicationException {
        // 已传送数据大小
        long transmitted = 0;
        try (BufferedOutputStream outputStream = new BufferedOutputStream(response.getOutputStream());
             RandomAccessFile randomAccessFile = new RandomAccessFile(file, "r")) {
            byte[] buff = new byte[4096];
            int len = 0;
            randomAccessFile.seek(startByte);
            // 坑爹地方四：判断是否到了最后不足4096（buff的length）个byte这个逻辑（(transmitted + len) <= contentLength）要放前面！！！
            // 不然会先读取randomAccessFile，造成后面读取位置出错，找了一天才发现问题所在
            while ((transmitted + len) <= contentLength && (len = randomAccessFile.read(buff)) != -1) {
                outputStream.write(buff, 0, len);
                transmitted += len;
                // 停一下，方便测试（本地下载传输速度特别快，没反应过来就下载好了），实际生产环境中用的时候需要删除这一行
                // Thread.sleep(10);
            }
            //处理不足buff.length部分
            if (transmitted < contentLength) {
                len = randomAccessFile.read(buff, 0, (int) (contentLength - transmitted));
                outputStream.write(buff, 0, len);
                transmitted += len;
            }

            outputStream.flush();
            response.flushBuffer();

        } catch (ClientAbortException e) {
            // 捕获此异常表示用户停止下载
            // System.out.println("用户停止下载：" + startByte + "-" + endByte + "：" + transmitted);
        } catch (IOException e) {
            throw new SmartApplicationException(SmartCode.Storage.INTERNAL_ERROR, "服务器出错");
        }
    }

    private void setResponse(HttpServletResponse response, long fileLength, long startByte, long endByte, String fileName, String contentType, int status, long contentLength) {
        response.setStatus(status);
        //参考资料：https://www.ibm.com/developerworks/cn/java/joy-down/index.html
        //坑爹地方二：看代码
        response.setHeader("Accept-Ranges", "bytes");
        response.setContentType(contentType);
        response.setHeader("Content-Type", contentType);
        //这里文件名换你想要的，inline表示浏览器可以直接使用（比如播放音乐，我方便测试用的）
        //参考资料：http://hw1287789687.iteye.com/blog/2188500
        response.setHeader("Content-Disposition", "inline;filename=" + fileName);
        response.setHeader("Content-Length", String.valueOf(contentLength));
        //坑爹地方三：Content-Range，格式为
        // [要下载的开始位置]-[结束位置]/[文件总大小]
        response.setHeader("Content-Range", "bytes " + startByte + "-" + endByte + "/" + fileLength);
    }

}
