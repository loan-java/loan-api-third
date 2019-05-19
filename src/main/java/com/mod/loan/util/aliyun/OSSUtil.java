package com.mod.loan.util.aliyun;

import com.aliyun.oss.OSSClient;
import com.aliyun.oss.model.OSSObject;
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

    /**
     * 获取oss上的指定文件
     *
     * @param objectName
     * @param bucketName
     * @return
     */
    public static String ossGetFile(String objectName, String bucketName) {
        StringBuffer stringBuffer = new StringBuffer();
        // 创建OSSClient实例。
        OSSClient ossClient = null;
        try {
            // Endpoint以杭州为例，其它Region请按实际情况填写。
            String endpoint = endpoint_in;
            // 阿里云主账号AccessKey拥有所有API的访问权限，风险很高。强烈建议您创建并使用RAM账号进行API访问或日常运维，请登录 https://ram.console.aliyun.com 创建RAM账号。
            String accessKeyId = Constant.OSS_ACCESSKEY_ID;
            String accessKeySecret = Constant.OSS_ACCESS_KEY_SECRET;
            // 创建OSSClient实例。
            ossClient = new OSSClient(endpoint, accessKeyId, accessKeySecret);
            // 判断文件是否存在。doesObjectExist还有一个参数isOnlyInOSS，如果为true则忽略302重定向或镜像；如果为false，则考虑302重定向或镜像。
            boolean found = ossClient.doesObjectExist(bucketName, objectName);
            logger.error("判断文件objectName=【" + objectName + "】在bucketName=【" + bucketName + "】是否存在【" + found + "】");
            if (!found) {
                throw new RuntimeException("当前查询数据不存在");
            }
            // ossObject包含文件所在的存储空间名称、文件名称、文件元信息以及一个输入流。
            OSSObject ossObject = ossClient.getObject(bucketName, objectName);
            // 读取文件内容。
            BufferedReader reader = new BufferedReader(new InputStreamReader(ossObject.getObjectContent()));
            while (true) {
                String line = reader.readLine();
                if (line == null) break;
                stringBuffer.append(line);
            }
            // 数据读取完成后，获取的流必须关闭，否则会造成连接泄漏，导致请求无连接可用，程序无法正常工作。
            reader.close();
        }catch (Exception e) {
            logger.error("获取oss上的指定文件失败：", e);
            throw new RuntimeException(e.getMessage());
        } finally {
            if (ossClient != null) {
                // 关闭OSSClient。
                ossClient.shutdown();
            }
        }
        return stringBuffer.toString();
    }

}
