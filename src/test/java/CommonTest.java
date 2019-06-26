import com.mod.loan.config.Constant;
import com.mod.loan.util.chanpay.ChanpayApiRequest;
import com.mod.loan.util.rongze.BizDataUtil;
import com.mod.loan.util.rongze.RongZeRequestUtil;
import com.mod.loan.util.rongze.SignUtil;
import com.mod.loan.util.rongze.StandardDesUtils;
import com.mod.loan.util.yeepay.YeePayApiRequest;
import org.junit.Test;

import javax.annotation.Resource;
import javax.sql.DataSource;

/**
 * @ author liujianjian
 * @ date 2019/5/15 13:56
 */
public class CommonTest extends BaseSpringBootJunitTest {

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
        String result = RongZeRequestUtil.doPost(Constant.rongZeQueryUrl, "api.charge.data", "{'order_no':'111'}");
        System.out.println(result);
    }


    @Test
    public void sign() throws Exception {

        String con = RongZeRequestUtil.buildRequestParams("api.charge.data", "{'order_no':'111'}");

        String s = SignUtil.genSign(con);
        System.out.println(s);

        System.out.println(SignUtil.checkSign(con, s));
    }

    @Test
    public void bizData() throws Exception {
        String despwd = StandardDesUtils.generateDesKey();
        String s = BizDataUtil.encryptBizData("{'order_no':'111'}", despwd);
        System.out.println(s);

        String ss = BizDataUtil.decryptBizData(s, RongZeRequestUtil.genDescKey(despwd));
        System.out.println(ss);
    }

}
