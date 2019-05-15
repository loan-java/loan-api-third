import com.mod.loan.config.Constant;
import com.mod.loan.util.rongze.RequestUtil;
import org.junit.Test;

/**
 * @ author liujianjian
 * @ date 2019/5/15 13:56
 */
public class CommonTest extends BaseSpringBootJunitTest {

    @Test
    public void m() throws Exception {
        String param = RequestUtil.buildRequestParams("api.charge.data", "{'order_no':'111'}");
        System.out.println(param);

        String result = RequestUtil.doPost(Constant.rongZeQueryUrl, param);
        System.out.println(result);
    }
}
