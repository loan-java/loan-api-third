package com.mod.loan.util.aliyun;

import com.aliyun.oss.OSSClient;
import com.mod.loan.config.Constant;
import com.mod.loan.controller.UploadController;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;
import sun.misc.BASE64Decoder;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
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

    /**
     * 文件上传
     * @param file
     * @return
     */
    public static String uploadImage(File file) {
        String fileName = file.getName();
        String fileType = fileName.substring(fileName.lastIndexOf("."));
        // Endpoint以杭州为例，其它Region请按实际情况填写。
        String endpoint = endpoint_in;
        // 阿里云主账号AccessKey拥有所有API的访问权限，风险很高。强烈建议您创建并使用RAM账号进行API访问或日常运维，请登录 https://ram.console.aliyun.com 创建RAM账号。
        String accessKeyId = Constant.OSS_ACCESSKEY_ID;
        String accessKeySecret = Constant.OSS_ACCESS_KEY_SECRET;
        // 创建OSSClient实例。
        OSSClient ossClient = new OSSClient(endpoint, accessKeyId,accessKeySecret);
        String filepath = null;
        try {
            String newFileName = UUID.randomUUID().toString().replaceAll("-", "") + fileType;
            filepath = new DateTime().getYear() + "/" + new DateTime().toString("MMdd") + "/" + newFileName;
            // 上传文件流。
            InputStream inputStream = new FileInputStream(file);
            ossClient.putObject(Constant.OSS_STATIC_BUCKET_NAME, filepath, file);
        }catch (Exception e) {
            logger.error("文件上传失败", e);
        } finally {
            if (ossClient != null) {
                ossClient.shutdown();
            }
        }
        return filepath;
    }

    /**
     * 流上传
     * @param inputStream
     * @return
     */
    public static String uploadImage(InputStream inputStream) {
        // Endpoint以杭州为例，其它Region请按实际情况填写。
        String endpoint = endpoint_in;
        // 阿里云主账号AccessKey拥有所有API的访问权限，风险很高。强烈建议您创建并使用RAM账号进行API访问或日常运维，请登录 https://ram.console.aliyun.com 创建RAM账号。
        String accessKeyId = Constant.OSS_ACCESSKEY_ID;
        String accessKeySecret = Constant.OSS_ACCESS_KEY_SECRET;
        // 创建OSSClient实例。
        OSSClient ossClient = new OSSClient(endpoint, accessKeyId,accessKeySecret);
        String filepath = null;
        try {
            String newFileName = UUID.randomUUID().toString().replaceAll("-", "") + ".png";
            filepath = new DateTime().getYear() + "/" + new DateTime().toString("MMdd") + "/" + newFileName;
            ossClient.putObject(Constant.OSS_STATIC_BUCKET_NAME, filepath, inputStream);
        }catch (Exception e) {
            logger.error("文件上传失败", e);
        } finally {
            if (ossClient != null) {
                ossClient.shutdown();
            }
        }
        return filepath;
    }


    /**
     * 字节上传
     * @param base64Str
     * @return
     */
    public static String uploadImage(String base64Str) {
        // Endpoint以杭州为例，其它Region请按实际情况填写。
        String endpoint = endpoint_in;
        // 阿里云主账号AccessKey拥有所有API的访问权限，风险很高。强烈建议您创建并使用RAM账号进行API访问或日常运维，请登录 https://ram.console.aliyun.com 创建RAM账号。
        String accessKeyId = Constant.OSS_ACCESSKEY_ID;
        String accessKeySecret = Constant.OSS_ACCESS_KEY_SECRET;
        // 创建OSSClient实例。
        OSSClient ossClient = new OSSClient(endpoint, accessKeyId,accessKeySecret);
        String filepath = null;
        String fileType = ".png";
        try {
            String newFileName = UUID.randomUUID().toString().replaceAll("-", "") + fileType;
            filepath = new DateTime().getYear() + "/" + new DateTime().toString("MMdd") + "/" + newFileName;
            byte[] content = base64Str.getBytes();   //将字符串转换为byte数组
            ossClient.putObject(Constant.OSS_STATIC_BUCKET_NAME, filepath, new ByteArrayInputStream(content));
        }catch (Exception e) {
            logger.error("文件上传失败", e);
        } finally {
            if (ossClient != null) {
                ossClient.shutdown();
            }
        }
        return filepath;
    }


}
