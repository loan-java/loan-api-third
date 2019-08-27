import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.mod.loan.common.enums.UserOriginEnum;
import com.mod.loan.common.exception.BizException;
import com.mod.loan.config.Constant;
import com.mod.loan.util.bengbeng.BengBengBizDataUtil;
import com.mod.loan.util.bengbeng.BengBengRequestUtil;
import com.mod.loan.util.bengbeng.BengBengSignUtil;
import com.mod.loan.util.bengbeng.BengBengStandardDesUtils;
import com.mod.loan.util.chanpay.ChanpayApiRequest;
import com.mod.loan.util.yeepay.YeePayApiRequest;
import org.junit.Test;

import javax.annotation.Resource;
import javax.sql.DataSource;

/**
 * @ author liujianjian
 * @ date 2019/5/15 13:56
 */
public class BengBengCommonTest extends BaseSpringBootJunitTest {

    @Resource
    private DataSource dataSource;
    @Resource
    private ChanpayApiRequest chanpayApiRequest;

    @Test
    public void chanpay() throws Exception {
//        chanpayApiRequest.bindCardRequest("100", "7", "6217856200024467149", "362424199103213912", "张亮", "18667166539");
//        chanpayApiRequest.bindCardConfirm("100", "3232");
//        chanpayApiRequest.cardPayRequest("2121", "3232", "000000", "1111", "0.01");
//        chanpayApiRequest.queryTrade("2121");
//        chanpayApiRequest.queryPayBalance();
//        chanpayApiRequest.transfer("2121", "工商银行", "000000", "1111", "0.01");
    }

    @Test
    public void yeePay() throws Exception {
        YeePayApiRequest.bindCardRequest("1", "1", "621000000", "342422", "test", "15867133397");
    }

    @Test
    public void t() {
        System.out.println(dataSource);
    }

    @Test
    public void req() throws Exception {
        JSONObject jsonObject1 = new JSONObject();
        jsonObject1.put("order_no", "SN201908261643125274");
        jsonObject1.put("type", "2");
        String mxMobile = BengBengRequestUtil.doPost(Constant.bengBengQueryUrl, "api.charge.data", jsonObject1.toJSONString());
        JSONObject jsonObject = JSONObject.parseObject(mxMobile);
        String dataStr = jsonObject.getString("data");
        JSONObject all = JSONObject.parseObject(dataStr);
        JSONObject report = new JSONObject();
        report = all.getJSONObject("report_data");
        JSONArray applicationCheck = report.getJSONArray("application_check");
        System.out.println(applicationCheck.toJSONString());
    }


    @Test
    public void sign() throws Exception {

        String con = BengBengRequestUtil.buildRequestParams("api.charge.data", "{'order_no':'111'}");

        String s = BengBengSignUtil.genSign(con);
        System.out.println(s);

        System.out.println(BengBengSignUtil.checkSign(con, s));
    }

    @Test
    public void bizData() throws Exception {
        String despwd = BengBengStandardDesUtils.generateDesKey();
        String s = BengBengBizDataUtil.encryptBizData("{'order_no':'111'}", despwd);
        System.out.println(s);

        String ss = BengBengBizDataUtil.decryptBizData(s, BengBengRequestUtil.genDescKey(despwd));
        System.out.println(ss);
    }

}
