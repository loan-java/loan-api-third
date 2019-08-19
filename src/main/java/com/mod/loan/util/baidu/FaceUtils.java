package com.mod.loan.util.baidu;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONPath;
import com.mod.loan.common.enums.ResponseEnum;
import com.mod.loan.common.model.ResultMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.math.BigInteger;

public class FaceUtils {
    private static Logger logger = LoggerFactory.getLogger(FaceUtils.class);

    public static final Double LEFT_EYE = 0.3;  //小于0.6
    public static final Double RIGHT_EYE = 0.3;    //小于0.6
    public static final Double NOSE = 0.2; //小于0.7
    public static final Double MOUTH = 0.2;    //小于0.7
    public static final Double LEFT_CHEEK = 0.3;   //小于0.8
    public static final Double CHIN_CONTOUR = 0.2; //小于0.6
    public static final Double RIGHT_CHEEK = 0.3;  //小于0.8

    public static final Double BLUR = 0.75; //小于0.7
    public static final Integer ROLL = 10; //小于20度
    public static final Integer PITCH = 16;    //小于20度
    public static final Integer YAW = 10;  //小于20度

    public static final Double FACE_LIVENESS = 0.8;//大于0.65
    public static final Integer COMPLETENESS = 0; //大于0  备注：只有0或1
    public static final Integer ILLUMINATION =45; //大于40
    public static final Double FACE_PROBABILITY = 0.9; //大于0.9

    public static ResultMessage checkArgs(String ress){
        ResultMessage faceDTO = new ResultMessage();
        //质量检测参考
        if(JSONPath.read(ress, "$.result") == null){
            faceDTO.setStatus(ResponseEnum.M4000.getCode());
            faceDTO.setMsg("请上传人脸信息");
            return  faceDTO;
        }

        JSONArray jsonArray = (JSONArray) JSONPath.read(ress, "$.result.face_list");
        //遮挡范围
        Object left_eye = JSONPath.eval(jsonArray, "$[0].quality.occlusion.left_eye");
        Object right_eye =  JSONPath.eval(jsonArray, "$[0].quality.occlusion.right_eye");
        Object nose = JSONPath.eval(jsonArray, "$[0].quality.occlusion.nose");
        Object mouth = JSONPath.eval(jsonArray, "$[0].quality.occlusion.mouth");
        Object left_cheek =  JSONPath.eval(jsonArray, "$[0].quality.occlusion.left_cheek");
        Object chin_contour =  JSONPath.eval(jsonArray, "$[0].quality.occlusion.chin_contour");
        Object right_cheek = JSONPath.eval(jsonArray, "$[0].quality.occlusion.right_cheek");
        //模糊度范围
        Object blur = JSONPath.eval(jsonArray, "$[0].quality.blur");
        //姿态角度
        Object roll = JSONPath.eval(jsonArray, "$[0].angle.roll");
        Object pitch = JSONPath.eval(jsonArray, "$[0].angle.pitch");
        Object yaw =  JSONPath.eval(jsonArray, "$[0].angle.yaw");
        //活体分数
        Object face_liveness = JSONPath.read(ress, "$.result.face_liveness");
        //人脸完整度
        Object completeness = JSONPath.eval(jsonArray, "$[0].quality.completeness");
        //光照范围
        Object illumination = JSONPath.eval(jsonArray, "$[0].quality.illumination");
        //人脸置信度
        Object face_probability = JSONPath.eval(jsonArray, "$[0].face_probability");

        if(compare(ILLUMINATION,illumination)){
            faceDTO.setStatus(ResponseEnum.M4000.getCode());
            faceDTO.setMsg("请保持光照充足");
            return  faceDTO;
        }
        
        if(compare(left_eye,LEFT_EYE)){
            faceDTO.setStatus(ResponseEnum.M4000.getCode());
            faceDTO.setMsg("请不要遮挡眼睛");
            return  faceDTO;
        }
        if(compare(right_eye,RIGHT_EYE)){
            faceDTO.setStatus(ResponseEnum.M4000.getCode());
            faceDTO.setMsg("请不要遮挡眼睛");
            return  faceDTO;
        }
        if(compare(nose,NOSE)){
            faceDTO.setStatus(ResponseEnum.M4000.getCode());
            faceDTO.setMsg("请不要遮挡鼻子");
            return  faceDTO;
        }
        if(compare(mouth,MOUTH)){
            faceDTO.setStatus(ResponseEnum.M4000.getCode());
            faceDTO.setMsg("请不要遮挡嘴部");
            return  faceDTO;
        }
        if(compare(left_cheek,LEFT_CHEEK)){
            faceDTO.setStatus(ResponseEnum.M4000.getCode());
            faceDTO.setMsg("请不要遮挡脸颊");
            return  faceDTO;
        }
        if(compare(right_cheek,RIGHT_CHEEK)){
            faceDTO.setStatus(ResponseEnum.M4000.getCode());
            faceDTO.setMsg("请不要遮挡脸颊");
            return  faceDTO;
        }
        if(compare(chin_contour,CHIN_CONTOUR)){
            faceDTO.setStatus(ResponseEnum.M4000.getCode());
            faceDTO.setMsg("请不要遮挡下巴");
            return  faceDTO;
        }

        if(compare(blur,BLUR)){
            faceDTO.setStatus(ResponseEnum.M4000.getCode());
            faceDTO.setMsg("图片较模糊");
            return  faceDTO;
        }

        if(compare(roll,ROLL)){
            faceDTO.setStatus(ResponseEnum.M4000.getCode());
            faceDTO.setMsg("不要倾斜头部");
            return  faceDTO;
        }
        if(compare(pitch,PITCH)){
            faceDTO.setStatus(ResponseEnum.M4000.getCode());
            faceDTO.setMsg("不要倾斜头部");
            return  faceDTO;
        }
        if(compare(yaw,YAW)){
            faceDTO.setStatus(ResponseEnum.M4000.getCode());
            faceDTO.setMsg("不要倾斜头部");
            return  faceDTO;
        }

        if(compare(FACE_LIVENESS,face_liveness)){
            faceDTO.setStatus(ResponseEnum.M4000.getCode());
            faceDTO.setMsg("人脸模糊");
            return  faceDTO;
        }

        if(compare(COMPLETENESS,completeness)){
            faceDTO.setStatus(ResponseEnum.M4000.getCode());
            faceDTO.setMsg("人脸模糊");
            return  faceDTO;
        }

        

        if(compare(FACE_PROBABILITY,face_probability)){
            faceDTO.setStatus(ResponseEnum.M4000.getCode());
            faceDTO.setMsg("人脸模糊");
            return  faceDTO;
        }

        faceDTO.setStatus(ResponseEnum.M2000.getCode());
        return faceDTO;
    }

    public static boolean compare(Object bigOne,Object smallOne) {
        boolean result = false;
        BigDecimal big = getBigDecimal(bigOne).abs();
        BigDecimal small = getBigDecimal(smallOne).abs();
        if(big.compareTo(small) > 0){
            result = true;
        }
        return result;
    }

    public static BigDecimal getBigDecimal(Object value) {
        BigDecimal ret = null;
        if (value != null) {
            if (value instanceof BigDecimal) {
                ret = (BigDecimal) value;
            } else if (value instanceof String) {
                ret = new BigDecimal((String) value);
            } else if (value instanceof BigInteger) {
                ret = new BigDecimal(((BigInteger) value));
            } else if (value instanceof Number) {
                ret = new BigDecimal(((Number) value).doubleValue());
            } else {
                throw new ClassCastException("Not possible to coerce [" + value + "] from class " + value.getClass() + " into a BigDecimal.");
            }
        }
        return ret;
    }

    public static void main(String[] args) {
        String str = "{\"result\":{\"thresholds\":{\"frr_1e-3\":0.3,\"frr_1e-2\":0.9,\"frr_1e-4\":0.05},\"face_liveness\":0.999945307,\"face_list\":[{\"face_shape\":{\"probability\":0.6047788262,\"type\":\"square\"},\"face_type\":{\"probability\":0.9988379478,\"type\":\"human\"},\"liveness\":{\"livemapscore\":0.999945307},\"angle\":{\"roll\":-70.60903168,\"pitch\":-16.55523872,\"yaw\":2.784353495},\"face_token\":\"049a9a0ac48192d9e1ccd1717d76f2bd\",\"location\":{\"top\":817.9632568,\"left\":264.5868835,\"rotation\":-71,\"width\":306,\"height\":334},\"face_probability\":0.9636109471,\"quality\":{\"illumination\":91,\"occlusion\":{\"right_eye\":0,\"nose\":0,\"mouth\":0,\"left_eye\":0,\"left_cheek\":0,\"chin_contour\":0,\"right_cheek\":0.003088803031},\"blur\":2.048231636E-6,\"completeness\":1}}]},\"log_id\":4545350510135,\"error_msg\":\"SUCCESS\",\"cached\":0,\"error_code\":0,\"timestamp\":1532595370}";
        System.out.println(checkArgs(str));
    }

}
