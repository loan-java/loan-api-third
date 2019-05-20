package com.mod.loan.api;

import com.mod.loan.model.dto.EngineResult;
import com.mod.loan.model.dto.QjldHeTianBaseReqDTO;
import com.mod.loan.model.dto.QjldHeTianResDTO;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * loan-risk 2019/4/29 huijin.shuailijie Init
 */
public interface QjldHeTianApi {

    /**
     * v
     * 全景雷达(和添)
     *
     * @param reqDTO 请求参数
     * @return 响应结果
     */
    @POST("/product/credit/v1/unify")
    Call<EngineResult<QjldHeTianResDTO>> qjldHeTianQuery(@Body QjldHeTianBaseReqDTO reqDTO);

}
