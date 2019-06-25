import com.alibaba.fastjson.JSON;
import com.mod.loan.common.model.ResultMessage;
import com.mod.loan.model.Order;
import com.mod.loan.service.OrderService;
import com.mod.loan.service.YeePayService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class YeePayTest extends BaseSpringBootJunitTest {

    @Autowired
    private YeePayService yeePayService;

    @Autowired
    private OrderService orderService;

    //绑卡请求
    @Test
    public void requestBindCard() {
        long uid = 2L;
        String orderNo = "1661269536227111224";
        String cardno = "6222021202041701419";
        String phone = "15868417851";
        ResultMessage message = yeePayService.requestBindCard(uid, orderNo, cardno, phone);
        System.out.println(JSON.toJSONString(message));
    }

    //绑卡确认
    @Test
    public void confirmBindCard() {
        String orderNo = "1661269536227111111";
        long uid = 2L;
        String smsCode = "";
        String bankCode = "CMB";
        String bankName = "招商银行";
        String cardNo = "6222021202041701419";
        String cardPhone = "15868417851";
        ResultMessage message = yeePayService.confirmBindCard(orderNo, uid, smsCode, bankCode, bankName, cardNo, cardPhone);
        System.out.println(JSON.toJSONString(message));
    }

    //还款
    @Test
    public void repay() throws Exception {
        Order order = orderService.findOrderByOrderNoAndSource("1661269536227111111", 1);
        ResultMessage message = yeePayService.repay(order);
        System.out.println(JSON.toJSONString(message));
    }
}
