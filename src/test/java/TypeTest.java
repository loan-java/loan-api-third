import com.mod.loan.mapper.OrderMapper;
import com.mod.loan.mapper.UserMapper;
import com.mod.loan.model.Order;
import com.mod.loan.model.User;
import com.mod.loan.service.TypeFilterService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

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
            list.stream().forEach(order -> {
                User user = userMapper.selectByPrimaryKey(order.getUid());
                typeFilterService.guize(user, order.getOrderNo());
            });
        }
    }

}
