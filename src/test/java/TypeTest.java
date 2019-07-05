import com.mod.loan.mapper.UserMapper;
import com.mod.loan.model.User;
import com.mod.loan.service.TypeFilterService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class TypeTest extends BaseSpringBootJunitTest {

    @Autowired
    private TypeFilterService typeFilterService;

    @Autowired
    private  UserMapper userMapper;

    //绑卡请求
    @Test
    public void requestBindCard() {
        User user = userMapper.selectByPrimaryKey((long)3);
        String orderNo="154753518181346882";
        boolean flag = typeFilterService.getInfoByTypeA(user, orderNo);
        System.out.println(flag);
    }

}
