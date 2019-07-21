import com.mod.loan.mapper.OrderMapper;
import com.mod.loan.mapper.UserMapper;
import com.mod.loan.model.Order;
import com.mod.loan.model.User;
import com.mod.loan.service.TypeFilterService;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Slf4j
public class TypeTest extends BaseSpringBootJunitTest {

    @Autowired
    private TypeFilterService typeFilterService;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private OrderMapper orderMapper;

    @Test
    public void requestBindCard() {
        User user = userMapper.selectByPrimaryKey((long) 3);
        String orderNo = "1547535181813468800";
        typeFilterService.getInfoByTypeA(user, orderNo);
    }


    @Test
    public void guize() {
//        User user = userMapper.selectByPrimaryKey((long)3);
//        String orderNo="1669965333712744448";
        List<Order> list = orderMapper.getLendOrder();
        if (list.size() > 0) {
            System.out.println("总订单数:" + list.size());
            list.stream().forEach(order -> {
                log.info("用户编号:{},订单编号:{}", order.getUid(), order.getOrderNo());
                System.out.println("用户编号:" + order.getUid() + ",订单编号:" + order.getOrderNo());
                User user = userMapper.selectByPrimaryKey(order.getUid());
                typeFilterService.guize(user, order.getOrderNo());
            });
        }
    }

}
