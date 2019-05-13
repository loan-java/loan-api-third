package com.mod.loan.util.aliyun;

import com.aliyun.oss.OSSClient;
import com.mod.loan.config.Constant;
import com.mod.loan.controller.UploadController;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.util.UUID;

public class OSSUtil {

    private static Logger logger = LoggerFactory.getLogger(UploadController.class);

    private static String endpoint_in = "https://oss-cn-zhangjiakou.aliyuncs.com";// 外网

    public static String upload(MultipartFile file) {
        String fileName = file.getOriginalFilename();
        String fileType = fileName.substring(fileName.lastIndexOf("."));
        OSSClient ossClient = new OSSClient(endpoint_in, Constant.OSS_ACCESSKEY_ID, Constant.OSS_ACCESS_KEY_SECRET);
        String filepath = "";
        try {
            DateTime dateTime = new DateTime();
            String newFileName = UUID.randomUUID().toString().replaceAll("-", "") + fileType;
            filepath = dateTime.getYear() + "/" + dateTime.toString("MMdd") + "/" + newFileName;
            ossClient.putObject(Constant.OSS_STATIC_BUCKET_NAME, filepath, file.getInputStream());
            return filepath;
        } catch (Exception e) {
            logger.error("文件上传失败", e);
        } finally {
            if (ossClient != null) {
                ossClient.shutdown();
            }
        }
        return null;
    }

    public static String uploadStr(String str, Long uid) {

        OSSClient ossClient = new OSSClient(endpoint_in, Constant.OSS_ACCESSKEY_ID, Constant.OSS_ACCESS_KEY_SECRET);
        String fileType = ".txt";
        try {
            DateTime dateTime = new DateTime();
            String newFileName = uid + fileType;
            String filepath = dateTime.getYear() + "/" + dateTime.toString("MMdd") + "/" + newFileName;
            ossClient.putObject(Constant.OSS_STATIC_BUCKET_NAME_MOBILE, filepath, new ByteArrayInputStream(str.getBytes()));
            return filepath;
        } catch (Exception e) {
            logger.error("文件上传失败", e);
        } finally {
            if (ossClient != null) {
                ossClient.shutdown();
            }
        }
// 关闭OSSClient。
        ossClient.shutdown();
        return null;
    }


}
