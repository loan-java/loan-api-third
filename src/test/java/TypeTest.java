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

    @Test
    public void requestBindCard() {
        User user = userMapper.selectByPrimaryKey((long)3);
        String orderNo="1547535181813468800";
        typeFilterService.getInfoByTypeA(user, orderNo);
    }



    @Test
    public void guize() {
        User user = userMapper.selectByPrimaryKey((long)3);
        String orderNo="1669965333712744448";
        typeFilterService.guize(user, orderNo);
    }

}
