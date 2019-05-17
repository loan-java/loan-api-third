import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;


public class ControllerTest extends BaseSpringBootJunitTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    public void submitOrder() throws Exception {

        String param = "{\"method\":\"fund.withdraw.req\",\"sign\":\"H9QgPfesvsW0zQBcLyA/rS4qWL/kduVyGf1JEGUSHQbJ79RFyHWNgCy3k5/I1CKLeXmxK7puqTEqxObRcxTvSqiFyKxHqqs3LXOf3fNymQtrWeQZD76r2okNpLx3CORJObSiuL/mp4KYQHB36YQtWAJUNw4PsC9eBwlmd69zGiY=\",\"merchant_id\":\"Hsd0515\",\"des_key\":\"mO5Wy9dZStJYfUIQdGvyrIUHZzGPloG36tTNiuCpH9PpPj0xUsfjo399iX05naIOqnzSSjc1McZCySpyMjFxNwg0QVlKr+61PAWMITETv31b/TyyV+u2LJRoLQfoxpam2GMMQaBG0g6deoUFbYr0esB6gQuEs0vP1T+uvF4kYQQ=\",\"biz_data\":\"8kS7ry67Ceejqmav5pKh/QFgBChIjtSdkGQCwlE0No1oyTBCRv6S4gen3uzKcKJMAmv3UFkZt/R2JjCtsf/Iboyx6AWDAche\",\"biz_enc\":\"1\"}";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.valueOf("application/json;UTF-8"));
        HttpEntity<String> strEntity = new HttpEntity<>(param, headers);

        String body = restTemplate.postForObject("/rongze/dispatcherRequest", strEntity, String.class);
        System.out.println(body);

    }
}
