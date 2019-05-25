import com.alibaba.fastjson.JSONObject;
import com.mod.loan.common.exception.BizException;
import com.mod.loan.common.model.RequestThread;
import com.mod.loan.config.Constant;
import com.mod.loan.mapper.OrderUserMapper;
import com.mod.loan.mapper.UserMapper;
import com.mod.loan.model.User;
import com.mod.loan.util.Base64Util;
import com.mod.loan.util.DateUtil;
import com.mod.loan.util.aliyun.OSSUtil;
import com.mod.loan.util.rongze.RongZeRequestUtil;
import org.apache.commons.lang.StringUtils;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import sun.nio.cs.US_ASCII;


public class ControllerTest extends BaseSpringBootJunitTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private OrderUserMapper orderUserMapper;

    @Test
    public void md5PhoneAndIdcard() throws Exception {
        System.out.println(orderUserMapper.getUidByOrderNoAndSourceAndUid("111",2,(long)1));
    }

    @Test
    public void submitOrder() throws Exception {
        String param = "{\"method\":\"fund.withdraw.req\",\"sign\":\"H9QgPfesvsW0zQBcLyA/rS4qWL/kduVyGf1JEGUSHQbJ79RFyHWNgCy3k5/I1CKLeXmxK7puqTEqxObRcxTvSqiFyKxHqqs3LXOf3fNymQtrWeQZD76r2okNpLx3CORJObSiuL/mp4KYQHB36YQtWAJUNw4PsC9eBwlmd69zGiY=\",\"merchant_id\":\"Hsd0515\",\"des_key\":\"mO5Wy9dZStJYfUIQdGvyrIUHZzGPloG36tTNiuCpH9PpPj0xUsfjo399iX05naIOqnzSSjc1McZCySpyMjFxNwg0QVlKr+61PAWMITETv31b/TyyV+u2LJRoLQfoxpam2GMMQaBG0g6deoUFbYr0esB6gQuEs0vP1T+uvF4kYQQ=\",\"biz_data\":\"8kS7ry67Ceejqmav5pKh/QFgBChIjtSdkGQCwlE0No1oyTBCRv6S4gen3uzKcKJMAmv3UFkZt/R2JjCtsf/Iboyx6AWDAche\",\"biz_enc\":\"1\"}";
        post(param);
    }

    @Test
    public void queryContract() throws Exception {
        String param = "{\"method\":\"fund.deal.contract\",\"sign\":\"i9c0ZpJUUK4FMoYehfEsqqIRqP/KyrZ1GQ9/4tn5wYFvXz5Aa9WeDhF/fTWLRDGt5PwokVw2nEhiz+JHfiLsJ4b7OxqTIjtxaDWxWDVmqLPBJUgK5Db0AzNL1EDxlHUhGGyxqv08/uYdTyENlFVi167lDmdoqICeRoXBoUAaSzY=\",\"merchant_id\":\"Hsd0515\",\"des_key\":\"s/0sJMxWxrYsYIpn2cmpMX3WcZCq0fOESVrIvg0PpgbdsMU5XnUkCZ/zZp5Z1iN7Ny+F5SP3DFxvj90xrkEvU0y7QO448TCNrr3aqTU2m5tE9x5hiY/KadZlazh1Xf9D6Ve+i9azc3MYYBmmL/bfD9DDa0wsSl03WWzBSFd3viw=\",\"biz_data\":\"1l8z6putf+B2qaxIBhhtCCI8Dszmf5tLmbH1FDrFx7MwkvPQbGeO1KUL71Va5aAUTGWYr9bfZK5Zic851+Umo3TiSLP9SQw6yePDP0Re5lUa8CSfyFTn1egXat8rZxGV1eIU67o3oA8=\",\"biz_enc\":\"1\"}";
        post(param);
    }

    @Test
    public void queryOrderStatus() throws Exception {
        String param = "{\"method\":\"fund.order.status\",\"sign\":\"pNFt2nYIbQSO5YtqZMoxzCyr7xX/d3BtTFvSU+Qigteo/uyEjhSP93y1vVZoSv8ZH6t5HOxUWomkiHtUkiIT3UgtO6VLrCwW2uMrfEh5TDMaIFcZOJz03d4sFwnV6ylUaLoRcikDZSS6XhxGqz2wUfLuOL1mIS2A+7XuLqNmCg8=\",\"merchant_id\":\"Hsd0515\",\"des_key\":\"ZD6Yjz/XN4FifIEUtkGdqfyaVL+kB1vufDWlfcOepvg6NDe6xm/JKc/eTXOsU15Dp7XIpLx/Ct7HU/LnGtoF9lBrx44BObO8BybPx13SS6ZSwh/FxnUvMDKYeEkKcwLiMxv5mxb957IFpxUlhAqOOEtlSt/g+6nT/B9SgKh4PQs=\",\"biz_data\":\"5Mfeubt3bWU8mygDpJmBuP8ezHw37NKJjFauLjpEkFjjJJPwNeaN9w==\",\"biz_enc\":\"1\"}";
        post(param);
    }

    @Test
    public void repay() throws Exception {
        String param = "{\"method\":\"fund.payment.req\",\"sign\":\"kB1F+3aIDJDm+DT9EtYLy0Hs14+3HpL4I3lNMd97ZIpdP/qsEr6OR1K4LTxeSZdIyJCp+DpCx/5lTpGdl+8odDg1IbJxml29VdqU6wrOrmPaXS6TyiFw6HGQH89D01Il2m4apeEtv1zULv1LyHfFNNs1FtSJBy+3DEHu2aCKIfo=\",\"merchant_id\":\"Hsd0515\",\"des_key\":\"dL5anlFj/TLTgzkwV0wJtA4QAansBcJVS4/Qr/WOS1sYagIivDK3QlSCTHbC2V8d40kBW+LzGjVQGedDCTuVoPaepV2oW0hR9bO2wSrT8KI3s6hKcx7FLsdF2kHKQcdrzUgFWGvraiEsCR0twsyl1Yh99jMGNtLV4050GFyQQd8=\",\"biz_data\":\"xapDBxebEueRqbzFx65iwzAij2HaraLFh+ZOci2maSvW0stYXUTiI220GlsZuVzjS+BeMnC17AGDRPVjuJxLVcjgI2B9fYYt\",\"biz_enc\":\"1\"}";
        post(param);
    }

    @Test
    public void userbase() throws Exception {
        String param="{\"method\":\"fund.userinfo.base\",\"sign\":\"tMw3PqVWxWjFCqLszwfVKNx228DK+t/qiJ4krUKeuhtnbbiDCulQr9i/99/KGAZfilbStOKNzCcc7otZ8XQ9CqyHDgJuwqJIEoAiEw/7zGMS/V/vuJOI57XQEoQrYGH0ykrzE96Q4h+XtFQKD6844TYOip+OLYQ2pARs1wVg4h8=\",\"merchant_id\":\"Hsd0515\",\"des_key\":\"rpH8ubJCDTepJcXXWvCYBY0d6OrGaryT0k1xa7dMVF3ay1fPDMQO5avzs2KT4kjFiXH8ygWazDj5JAcB2Pm4BXdSxi7Tk32U6TlnRDE5G2tfs7o+/jz6gDJfIoNrOFi6zXPv8S3+AzOIBpAlXgy5IPgjhb8ro8SA4iMuRYWZYv0=\",\"biz_data\":\"oId7Kwv3glT+VythxUzInUhThU0VHFd8iuarPBkZ1p3X2CSX+Oen9G8xXU/Ri4X6ezOnL/eLLIwMP48qZ6Fqx4+Lr0X6uOHbwMxwkL8fkBErO1yoskvzWaXQNMMiLOswwN8icKs8OOGC3RXtpjeRMVL6c0ISF5VcjyhATOW6BzTgj0eNyHSLxyBGlrjzXpliuwUSHCXbK1FkQlqyUHfmq4Xb26XwhkrgzHXA/WlBtiQLxLPW9qeOdTs+gU4Pkjau99A6+IjAhftH1Uov15Z+djHeUnlUiBSGn9mF2VCl/QnpubmbJOr/9UK7g7VR7YZtvJwyClLijZfRi5sd6hwiFkVDvIGWbuvDke0qA2ha+Ajo+gBPwBgJaJJc5Bb1AAhpgT0gLOl2DWWrNn7JIJ9FRek93RoIp5HNuOHH9JvG48+y+zVrkcshYfuUGwKJgQXdILTO3RWJsFjQDzDBobmq2ck0wXs8bk7Lv4wi+RtnMIyo2dilLIWvon0/+0KZXR5FO2eF4ByruHavd8jQRMnTbjqqW/J1PZ5c6sl5nbt0xT1SB6pSR4b1xxLfbjz6wNVQ8QkAPk0d09fDFkuOObqdAxjbQGusA89MDpdj7iNYqbNV5ffTRN3yXl1CbkfVBPr21dvp38KU2g08l2Le4UiB/yx45PbvFpYRDxF/3HuB+udtJw1O0g0sQ3+JD0vIvgaywTZOzg6+tGh5Oip5O52jrWHmHH4rXJ4v8HkQMjfY2QDWBuKm2Q2llQMcW12GsAwmNa2JB6UsgDIyFukGt8fUdNkQ7av3HBuQ2eediCvTKpzcp9yqvnqxImRkKjLbb8Qi3QMoAEZnPkamGFudas2JL2aqMaooUcI9fogmJ2v/fD8=\",\"biz_enc\":\"1\"}";
        post(param);
    }


    @Test
    public void certauth() throws Exception {
        String param="{\"method\":\"fund.cert.auth\",\"sign\":\"Vs6jHKpVhHe7YWtu/U7q23Ch6zFOrVoOGDsM3clDdN8fAyKM0k3dSOQmujF5RqfpgxG5qlKMkz2VMg+God/qYCdoiPxrg8y6LqVCCC9sGuSJZv92Br/ZNUQhQWTkmd3WVWD6QxdtSQn1Y1nqiC2nnTmEOti1IuD5kuI3MG5DxpY=\",\"merchant_id\":\"Hsd0515\",\"des_key\":\"U8hwcs2zkpeLoDHQtkji+C081eHIdYEkhdwmsFSOb96e1Yt2OmanwpVLrFvimhe4pKNZeYTDNGapu3XXowSgtIFJ9gZraJUloPd9SuJ0twAPtzlxOk3+p9dGnSg1OqTzRH72L8PFlRRagguDXajDS6iraQqI9NQBvVyPVfNkd1A=\",\"biz_data\":\"WAV+oSgtqjqUQPCFRrGLfW9DQLIlJn7NxHWWUl2GSP7TXNI9nPfek0vR4hxKIEuxUD3smOK0AdpsGzebr1R3oM35BBPXEzE65q84nhjzbncaQUAtYE2ek98quOh/f2uoa6Uqied88+W7NYr1p9qz08Cd8+2TLJq5d09mo5Oel/NZJnr+Wv30Wgjmv0+b0INOQky0nemEasG51HrKFOjYWsfX8fqvjoZZS0nbzSkixAVmQh7ZEsJ6USVgNHvf6vPx\",\"biz_enc\":\"1\"}";
        post(param);
    }

    @Test
    public void userinfoaddit() throws Exception {
        String param="{\"method\":\"fund.userinfo.addit\",\"sign\":\"gQ/k1tlUBiO6Vnzzo+dOxAeXuqomzcIGgDuc2vZXUBzEaSOTSk65YfEL1NUEWb6/Kc2jZXcIft8oIiBzDoBeIYzPrsSOIoTgWsWqA4ysuGy/IAT21xW2meCVR2sHhCMKXw8wQ2dYgnJcPDKqfUzV3Vt20WjzHxaSkvfVD7eLqWc=\",\"merchant_id\":\"Hsd0515\",\"des_key\":\"m8QsKekbNB8By2B++U+DznPydfG6vGgAJ5mV307fiuRbyYK24jd5mjpJJxzB21L4BE+4xwemDyOCKhVQxyHuMcJKVsV7C7lU/bqA8W+4MEbqfJfPdhbyn1PcuBSFtEB0Udm84O44nLMBdC4wq7f2M+bLQTki/2bSYGEwEuVYgBY=\",\"biz_data\":\"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\",\"biz_enc\":\"1\"}";
        post(param);
    }

    private String post(String param) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.valueOf("application/json;UTF-8"));
        HttpEntity<String> strEntity = new HttpEntity<>(param, headers);

        String body = restTemplate.postForObject("/rongze/dispatcherRequest", strEntity, String.class);
        System.out.println(body);
        return body;
    }


    @Test
    public void getRzInfor() throws Exception {
        JSONObject jsonObject1=new JSONObject();
        jsonObject1.put("order_no","1658473363288821760");
        jsonObject1.put("fileid","15564321122325608");
        String result1 = RongZeRequestUtil.doPost(Constant.rongZeQueryUrl, "api.resource.findfile", jsonObject1.toJSONString());
        JSONObject resultJson1 = JSONObject.parseObject(result1);
        if(!resultJson1.containsKey("code") || !resultJson1.containsKey("data") || resultJson1.getInteger("code") != 200){
            throw new BizException("推送用户补充信息:身份证正面信息解析失败"  + result1);
        }
        JSONObject data = resultJson1.getJSONObject("data");

        String base64str = data.getString("filestr");
        String filesuffix = data.getString("filesuffix");

        String imgCertFront = OSSUtil.uploadImage(base64str,filesuffix);

        System.out.println("image::" + imgCertFront);

    }


    @Test
    public void getRzInforR() throws Exception {
        JSONObject jsonObject1=new JSONObject();
        jsonObject1.put("order_no","1658473363288821760");
        jsonObject1.put("type","1");
        String mxMobile = RongZeRequestUtil.doPost(Constant.rongZeQueryUrl, "api.charge.data", jsonObject1.toJSONString());
        //判断运营商数据
        JSONObject jsonObject = JSONObject.parseObject(mxMobile);
        if(!jsonObject.containsKey("code") || !jsonObject.containsKey("data") ||jsonObject.getInteger("code") != 200){
            throw new BizException("推送用户补充信息:下载运营商数据解析失败");
        }
        String dataStr = jsonObject.getString("data");
        JSONObject all = JSONObject.parseObject(dataStr);
        JSONObject data = all.getJSONObject("data");
        JSONObject report = data.getJSONObject("report");
        JSONObject members = report.getJSONObject("members");
        //上传
        String mxMobilePath = OSSUtil.uploadStr(members.toJSONString(),999L);

        System.out.println("运营商：："+mxMobilePath);
    }


    @Test
    public void deleteOSSfile() throws Exception {
        OSSUtil.deleteFile("2019/0524/ce461a4693ae43d690c3cc212d95c2df.jpg",Constant.OSS_STATIC_BUCKET_NAME);
    }


}
