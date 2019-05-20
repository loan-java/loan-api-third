package com.mod.loan;

import com.mod.loan.api.QjldHeTianApi;
import com.mod.loan.config.qjld.QjldConfig;
import com.mod.loan.model.dto.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;

import javax.annotation.PostConstruct;
import java.io.IOException;

/**
 * 决策请求工具类
 *
 * @author lijing
 * @date 2017/12/26 0026.
 */
@Slf4j
@Component
public class QjldHeTianHelper {

    @Autowired
    private QjldConfig qjldConfig;

    private QjldHeTianApi api;

    /**
     * 初始化加载
     */
    @PostConstruct
    public void init() {
        log.info("加载决策地址ip:{}", qjldConfig.getQjldHeTianUrl());
        Retrofit retrofit = RetrofitUtil.createRetrofit(null, qjldConfig.getQjldHeTianUrl());
        api = retrofit.create(QjldHeTianApi.class);
    }

    /**
     * 全景雷达请求
     *
     * @param reqDTO 请求信息
     */
    public QjldHeTianResDTO qjldHeTianQuery(QjldHeTianBaseReqDTO reqDTO) {
        try {
            log.info("开始执行和添请求:{}", reqDTO);
            Call<EngineResult<QjldHeTianResDTO>> call = api.qjldHeTianQuery(reqDTO);
            EngineResult<QjldHeTianResDTO> result = execute(call);
            log.info("和添请求结果:{}", result);
            return result.getData();
        } catch (Exception e) {
            log.error("和添执行失败,原因:{}", e);
        }
        return null;
    }


    /**
     * 结果打印
     *
     * @param call call
     * @param <T>  泛型
     */
    private static <T> EngineResult<T> execute(Call<EngineResult<T>> call) {
        try {
            long startTime = System.currentTimeMillis();
            Response<EngineResult<T>> response = call.execute();
            if (response.code() == 200) {
                EngineResult<T> result = response.body();
                log.info("执行成功,耗时:{}ms,响应结果:{}", System.currentTimeMillis() - startTime, result);
                return result;
            }
            log.error("查询失败:{}", response.code());
        } catch (IOException e) {
            log.error("执行失败，原因;", e);
        }
        return null;
    }
}
