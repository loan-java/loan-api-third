import com.mod.loan.mapper.UserMapper;
import com.mod.loan.model.User;
import com.mod.loan.util.DateUtil;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import sun.nio.cs.US_ASCII;


public class ControllerTest extends BaseSpringBootJunitTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private UserMapper userMapper;

    @Test
    public void md5PhoneAndIdcard() throws Exception {
        User user= userMapper.getMd5PhoneAndIdcard("23B74636066879AC03570D0DFAC5813F");
        System.out.println(user);
    }

    @Test
    public void submitOrder() throws Exception {
        String param = "{\"method\":\"fund.withdraw.req\",\"sign\":\"H9QgPfesvsW0zQBcLyA/rS4qWL/kduVyGf1JEGUSHQbJ79RFyHWNgCy3k5/I1CKLeXmxK7puqTEqxObRcxTvSqiFyKxHqqs3LXOf3fNymQtrWeQZD76r2okNpLx3CORJObSiuL/mp4KYQHB36YQtWAJUNw4PsC9eBwlmd69zGiY=\",\"merchant_id\":\"Hsd0515\",\"des_key\":\"mO5Wy9dZStJYfUIQdGvyrIUHZzGPloG36tTNiuCpH9PpPj0xUsfjo399iX05naIOqnzSSjc1McZCySpyMjFxNwg0QVlKr+61PAWMITETv31b/TyyV+u2LJRoLQfoxpam2GMMQaBG0g6deoUFbYr0esB6gQuEs0vP1T+uvF4kYQQ=\",\"biz_data\":\"8kS7ry67Ceejqmav5pKh/QFgBChIjtSdkGQCwlE0No1oyTBCRv6S4gen3uzKcKJMAmv3UFkZt/R2JjCtsf/Iboyx6AWDAche\",\"biz_enc\":\"1\"}";
        post(param);
    }

    @Test
    public void queryContract() throws Exception {
        String param = "{\"method\":\"fund.deal.contract\",\"sign\":\"i9c0ZpJUUK4FMoYehfEsqqIRqP/KyrZ1GQ9/4tn5wYFvXz5Aa9WeDhF/fTWLRDGt5PwokVw2nEhiz+JHfiLsJ4b7OxqTIjtxaDWxWDVmqLPBJUgK5Db0AzNL1EDxlHUhGGyxqv08/uYdTyENlFVi167lDmdoqICeRoXBoUAaSzY=\",\"merchant_id\":\"Hsd0515\",\"des_key\":\"s/0sJMxWxrYsYIpn2cmpMX3WcZCq0fOESVrIvg0PpgbdsMU5XnUkCZ/zZp5Z1iN7Ny+F5SP3DFxvj90xrkEvU0y7QO448TCNrr3aqTU2m5tE9x5hiY/KadZlazh1Xf9D6Ve+i9azc3MYYBmmL/bfD9DDa0wsSl03WWzBSFd3viw=\",\"biz_data\":\"1l8z6putf+B2qaxIBhhtCCI8Dszmf5tLmbH1FDrFx7MwkvPQbGeO1KUL71Va5aAUTGWYr9bfZK5Zic851+Umo3TiSLP9SQw6yePDP0Re5lUa8CSfyFTn1egXat8rZxGV1eIU67o3oA8=\",\"biz_enc\":\"1\"}";
        post(param);
    }

    @Test
    public void queryOrderStatus() throws Exception {
        String param = "{\"method\":\"fund.order.status\",\"sign\":\"pNFt2nYIbQSO5YtqZMoxzCyr7xX/d3BtTFvSU+Qigteo/uyEjhSP93y1vVZoSv8ZH6t5HOxUWomkiHtUkiIT3UgtO6VLrCwW2uMrfEh5TDMaIFcZOJz03d4sFwnV6ylUaLoRcikDZSS6XhxGqz2wUfLuOL1mIS2A+7XuLqNmCg8=\",\"merchant_id\":\"Hsd0515\",\"des_key\":\"ZD6Yjz/XN4FifIEUtkGdqfyaVL+kB1vufDWlfcOepvg6NDe6xm/JKc/eTXOsU15Dp7XIpLx/Ct7HU/LnGtoF9lBrx44BObO8BybPx13SS6ZSwh/FxnUvMDKYeEkKcwLiMxv5mxb957IFpxUlhAqOOEtlSt/g+6nT/B9SgKh4PQs=\",\"biz_data\":\"5Mfeubt3bWU8mygDpJmBuP8ezHw37NKJjFauLjpEkFjjJJPwNeaN9w==\",\"biz_enc\":\"1\"}";
        post(param);
    }

    @Test
    public void repay() throws Exception {
        String param = "{\"method\":\"fund.payment.req\",\"sign\":\"kB1F+3aIDJDm+DT9EtYLy0Hs14+3HpL4I3lNMd97ZIpdP/qsEr6OR1K4LTxeSZdIyJCp+DpCx/5lTpGdl+8odDg1IbJxml29VdqU6wrOrmPaXS6TyiFw6HGQH89D01Il2m4apeEtv1zULv1LyHfFNNs1FtSJBy+3DEHu2aCKIfo=\",\"merchant_id\":\"Hsd0515\",\"des_key\":\"dL5anlFj/TLTgzkwV0wJtA4QAansBcJVS4/Qr/WOS1sYagIivDK3QlSCTHbC2V8d40kBW+LzGjVQGedDCTuVoPaepV2oW0hR9bO2wSrT8KI3s6hKcx7FLsdF2kHKQcdrzUgFWGvraiEsCR0twsyl1Yh99jMGNtLV4050GFyQQd8=\",\"biz_data\":\"xapDBxebEueRqbzFx65iwzAij2HaraLFh+ZOci2maSvW0stYXUTiI220GlsZuVzjS+BeMnC17AGDRPVjuJxLVcjgI2B9fYYt\",\"biz_enc\":\"1\"}";
        post(param);
    }

    private String post(String param) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.valueOf("application/json;UTF-8"));
        HttpEntity<String> strEntity = new HttpEntity<>(param, headers);

        String body = restTemplate.postForObject("/rongze/dispatcherRequest", strEntity, String.class);
        System.out.println(body);
        return body;
    }
}
