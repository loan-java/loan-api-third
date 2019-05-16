import com.mod.loan.util.rongze.RongZeRequestUtil;
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
    public void t() throws Exception {

        String param = RongZeRequestUtil.buildRequestParams("fund.deal.contract", "{\"order_no\":1}");
        System.out.println(param);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.valueOf("application/json;UTF-8"));
        HttpEntity<String> strEntity = new HttpEntity<>(param, headers);

        String body = restTemplate.postForObject("/rongze/dispatcherRequest", strEntity, String.class);
        System.out.println(body);

    }
}
