package com.mod.loan.controller;

import com.mod.loan.common.annotation.LoginRequired;
import com.mod.loan.common.enums.ResponseEnum;
import com.mod.loan.common.model.RequestThread;
import com.mod.loan.common.model.ResultMessage;
import com.mod.loan.util.aliyun.OSSUtil;
import org.apache.commons.lang.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

@RestController
public class UploadController {

	@RequestMapping(value = "upload")
	@LoginRequired(check = true)
	public ResultMessage upload(@RequestParam("file") MultipartFile file) {
		String filePath = OSSUtil.upload(file);
		if (StringUtils.isBlank(filePath)) {
			return new ResultMessage(ResponseEnum.M4000.getCode(), "上传失败！");
		}
		Map<String, String> data = new HashMap<>();
		data.put("url", filePath);
		return new ResultMessage(ResponseEnum.M2000, data);
	}

	@RequestMapping(value = "uploadString")
	@LoginRequired(check = true)
	public ResultMessage upload(@RequestParam("str") String str) {
		String filePath = OSSUtil.uploadStr(str, RequestThread.getUid());
		if (StringUtils.isBlank(filePath)) {
			return new ResultMessage(ResponseEnum.M4000.getCode(), "上传失败！");
		}
		Map<String, String> data = new HashMap<>();
		data.put("url", filePath);
		return new ResultMessage(ResponseEnum.M2000, data);
	}


}
