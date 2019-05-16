import com.mod.loan.config.Constant;
import com.mod.loan.util.rongze.BizDataUtil;
import com.mod.loan.util.rongze.RongZeRequestUtil;
import com.mod.loan.util.rongze.SignUtil;
import com.mod.loan.util.rongze.StandardDesUtils;
import org.junit.Test;

/**
 * @ author liujianjian
 * @ date 2019/5/15 13:56
 */
public class CommonTest extends BaseSpringBootJunitTest {

    @Test
    public void req() throws Exception {
        String result = RongZeRequestUtil.doPost(Constant.rongZeQueryUrl, "api.charge.data", "{'order_no':'111'}");
        System.out.println(result);
    }


    @Test
    public void sign() throws Exception {

        String con= RongZeRequestUtil.buildRequestParams("api.charge.data", "{'order_no':'111'}");

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
