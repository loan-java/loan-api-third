import com.alibaba.fastjson.JSON;
import com.mod.loan.common.model.RequestThread;
import com.mod.loan.common.model.ResultMessage;
import com.mod.loan.mapper.BankMapper;
import com.mod.loan.mapper.OrderMapper;
import com.mod.loan.mapper.OrderPhoneMapper;
import com.mod.loan.model.Bank;
import com.mod.loan.model.Order;
import com.mod.loan.model.OrderPhone;
import com.mod.loan.model.vo.UserBankInfoVO;
import com.mod.loan.service.OrderService;
import com.mod.loan.service.YeePayService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.Date;

public class YeePayTest extends BaseSpringBootJunitTest {

    @Autowired
    private YeePayService yeePayService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private OrderPhoneMapper orderPhoneMapper;


    @Autowired
    private BankMapper bankMapper;

    //绑卡请求
    @Test
    public void requestBindCard() {
        long uid = 1L;
        String orderNo = "1661269536227111226";
        String cardno = "6214835899276321";
        String phone = "15867122886";
        Bank bank = bankMapper.selectByPrimaryKey("CMB");
        ResultMessage message = yeePayService.requestBindCard(uid, cardno, phone, bank);
        System.out.println(JSON.toJSONString(message));
    }

    //绑卡确认
    @Test
    public void confirmBindCard() {
        long uid = 1L;
        String smsCode = "867827";
        String bankCode = "CMB";
        String bankName = "招商银行";
        String cardNo = "6214835899276321";
        String cardPhone = "15867122886";
        UserBankInfoVO userBankInfoVO = new UserBankInfoVO();
        userBankInfoVO.setCardCode(bankCode);
        userBankInfoVO.setCardName(bankName);
        userBankInfoVO.setCardNo(cardNo);
        userBankInfoVO.setCardPhone(cardPhone);
        ResultMessage message = yeePayService.confirmBindCard(smsCode, uid, userBankInfoVO);
        System.out.println(JSON.toJSONString(message));
    }

    //还款
    @Test
    public void repay() throws Exception {
        Order order = orderService.findOrderByOrderNoAndSource("1661269536227111225", 1);
        ResultMessage message = yeePayService.repay(order);
        System.out.println(JSON.toJSONString(message));
    }

    @Test
    public void addOrder() {
        Order order = new Order();
        order.setOrderNo("1661269536227111226");
        order.setUid(1L);
        order.setBorrowDay(6);
        order.setBorrowMoney(new BigDecimal("1500.00"));
        order.setActualMoney(new BigDecimal("1050.00"));
        order.setTotalRate(new BigDecimal("30.00"));
        order.setTotalFee(new BigDecimal("450.00"));
        order.setInterestRate(new BigDecimal("1.00"));
        order.setOverdueDay(0);
        order.setOverdueFee(new BigDecimal(0));
        order.setOverdueRate(new BigDecimal("2.00"));
        order.setInterestFee(new BigDecimal("1.50"));
        order.setShouldRepay(new BigDecimal("1501.50"));
        order.setHadRepay(new BigDecimal(0));
        order.setReduceMoney(new BigDecimal(0));
        order.setStatus(11);
        order.setCreateTime(new Date());
        order.setMerchant(RequestThread.getClientAlias());
        order.setProductId(34L);
        order.setUserType(1);
        order.setPaymentType("yeepay");
        OrderPhone orderPhone = new OrderPhone();
        orderPhone.setParamValue("");
        orderPhone.setPhoneModel("third|7");
        orderPhone.setPhoneType("H5");
        order.setSource(1);
        addOrUpdateOrder(order, orderPhone);
    }

    public void addOrUpdateOrder(Order order, OrderPhone orderPhone) {
        if (order.getId() != null && order.getId() > 0) {
            orderMapper.updateByPrimaryKeySelective(order);
            orderPhone.setOrderId(order.getId());
            orderPhoneMapper.updateByPrimaryKeySelective(orderPhone);
            return;
        }
        addOrder(order, orderPhone);
    }

    public int addOrder(Order order, OrderPhone orderPhone) {
        orderMapper.insertSelective(order);
        orderPhone.setOrderId(order.getId());
        return orderPhoneMapper.insertSelective(orderPhone);
    }
}
