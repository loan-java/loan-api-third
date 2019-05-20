package com.mod.loan.api;

import com.mod.loan.model.dto.*;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * 决策引擎api
 *
 * @author lijing
 * @date 2017/12/27 0027.
 */
public interface DecisionEngineApi {

    /**
     * 同步请求
     *
     * @param reqDTO 请求参数
     * @return 响应结果
     */
    @POST("/credit-decision-engine/decision/custom/v2/execute")
    Call<EngineResult<DecisionBaseResDTO>> syncDecision(@Body DecisionBaseReqDTO reqDTO);

    /**
     * 异步请求
     *
     * @param reqDTO 请求参数
     * @return 响应结果
     */
    @POST("/credit-decision-engine/decision/custom/v3/execute")
    Call<EngineResult<DecisionBaseResDTO>> nosyncDecision(@Body DecisionBaseReqDTO reqDTO);


    @POST("/credit-decision-engine/decision/queryExecute")
    Call<EngineResult<DecisionResDetailDTO>> queryDecision(@Body DecisionReqDTO reqDTO);

}
