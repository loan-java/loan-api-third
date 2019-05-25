package com.mod.loan.util.aliyun;

import com.alibaba.fastjson.JSONObject;
import com.aliyun.oss.OSSClient;
import com.aliyun.oss.model.OSSObject;
import com.aliyun.oss.model.PutObjectResult;
import com.mod.loan.config.Constant;
import com.mod.loan.controller.UploadController;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;
import sun.misc.BASE64Decoder;

import java.io.*;
import java.util.UUID;

public class OSSUtil {

    private static Logger logger = LoggerFactory.getLogger(UploadController.class);


    public static String upload(MultipartFile file) {
        String fileName = file.getOriginalFilename();
        String fileType = fileName.substring(fileName.lastIndexOf("."));
        OSSClient ossClient = new OSSClient(Constant.OSS_ENDPOINT_IN, Constant.OSS_ACCESSKEY_ID, Constant.OSS_ACCESS_KEY_SECRET);
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

        OSSClient ossClient = new OSSClient(Constant.OSS_ENDPOINT_IN, Constant.OSS_ACCESSKEY_ID, Constant.OSS_ACCESS_KEY_SECRET);
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
        return null;
    }

    /**
     * 字节上传
     * @param base64Str
     * @return
     */
    public static String uploadImage(String base64Str,String filesuffix) {
        // Endpoint以杭州为例，其它Region请按实际情况填写。
        String endpoint = Constant.OSS_ENDPOINT_IN;
        // 阿里云主账号AccessKey拥有所有API的访问权限，风险很高。强烈建议您创建并使用RAM账号进行API访问或日常运维，请登录 https://ram.console.aliyun.com 创建RAM账号。
        String accessKeyId = Constant.OSS_ACCESSKEY_ID;
        String accessKeySecret = Constant.OSS_ACCESS_KEY_SECRET;
        // 创建OSSClient实例。
        OSSClient ossClient = new OSSClient(endpoint, accessKeyId,accessKeySecret);
        String filepath = null;
        String fileType = "."+filesuffix;
        try {
            BASE64Decoder decoder = new BASE64Decoder();
            byte[] bytes1 = decoder.decodeBuffer(base64Str);
            String newFileName = UUID.randomUUID().toString().replaceAll("-", "") + fileType;
            filepath = new DateTime().getYear() + "/" + new DateTime().toString("MMdd") + "/" + newFileName;
            PutObjectResult result = ossClient.putObject(Constant.OSS_STATIC_BUCKET_NAME, filepath, new ByteArrayInputStream(bytes1));
            logger.info("上传照片：" + JSONObject.toJSONString(result));
        }catch (Exception e) {
            e.printStackTrace();
            logger.error("文件上传失败", e);
        } finally {
            if (ossClient != null) {
                ossClient.shutdown();
            }
        }
        return filepath;
    }

    /**
     * 删除文件
     * @param objectName
     * @param bucketNameStr  bucket目录
     * @return
     */
    public static void deleteFile(String objectName,String bucketNameStr) {
        // Endpoint以杭州为例，其它Region请按实际情况填写。
        String endpoint = Constant.OSS_ENDPOINT_IN;
        // 阿里云主账号AccessKey拥有所有API的访问权限，风险很高。强烈建议您创建并使用RAM账号进行API访问或日常运维，请登录 https://ram.console.aliyun.com 创建RAM账号。
        String accessKeyId = Constant.OSS_ACCESSKEY_ID;
        String accessKeySecret = Constant.OSS_ACCESS_KEY_SECRET;
        // 创建OSSClient实例。
        OSSClient ossClient = new OSSClient(endpoint, accessKeyId,accessKeySecret);
        try {
            // Endpoint以杭州为例，其它Region请按实际情况填写。
            String bucketName = bucketNameStr;
            // 删除文件。
            ossClient.deleteObject(bucketName, objectName);
            // 关闭OSSClient。
            ossClient.shutdown();
        }catch (Exception e) {
            e.printStackTrace();
            logger.error("文件上传失败", e);
        } finally {
            if (ossClient != null) {
                ossClient.shutdown();
            }
        }
    }



}
