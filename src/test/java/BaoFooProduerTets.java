import com.mod.loan.common.message.OrderRepayQueryMessage;
import com.mod.loan.config.rabbitmq.RabbitConst;
import org.junit.Test;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;

public class BaoFooProduerTets extends BaseSpringBootJunitTest {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Test
    public void test(){
        OrderRepayQueryMessage message = new OrderRepayQueryMessage();
        message.setMerchantAlias("jishidai");
        message.setRepayNo("bf20190522154030855836113");
        message.setTimes(1);
        message.setRepayType(1);
        rabbitTemplate.convertAndSend(RabbitConst.baofoo_queue_repay_order_query, message);
    }
}
