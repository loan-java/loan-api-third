package com.mod.loan.controller.upload;

import com.mod.loan.common.enums.ResponseEnum;
import com.mod.loan.common.model.ResultMessage;
import com.mod.loan.config.Constant;
import com.mod.loan.service.UploadService;
import com.mod.loan.util.DateUtil;
import org.apache.commons.lang.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/file")
public class FileController {

    @Resource
    private UploadService uploadService;

    @PostMapping(value = "/upload")
    public ResultMessage upload(@RequestParam("file") MultipartFile file) {
        String path = uploadService.uploadFile(DateUtil.getStringDateShort(), file);
        if (StringUtils.isBlank(path)) {
            return new ResultMessage(ResponseEnum.M4000.getCode(), "上传失败！");
        }
        Map<String, String> data = new HashMap<>();
        data.put("url", path);
        return new ResultMessage(ResponseEnum.M2000, data);
    }

    @GetMapping(value = "/visit")
    public void visit(HttpServletResponse response, @RequestParam("f") String f) {
        File file = null;
        FileInputStream fis = null;
        try {
            file = new File(Constant.FILE_SAVE_PATH + f);
            if (!file.exists()) {
                return;
            }
            fis = new FileInputStream(file);
            final byte[] buf = new byte[1024];
            while (fis.read(buf) > 0) {
                response.getOutputStream().write(buf);
            }
        } catch (final Exception e) {
            System.out.println(e.getMessage());
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (final IOException e) {
                    fis = null;
                }
            }
            response.setStatus(HttpServletResponse.SC_MOVED_PERMANENTLY);
        }
    }
}
