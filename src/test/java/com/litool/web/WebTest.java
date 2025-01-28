package com.litool.web;


import com.litool.web.WebUtils;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;


import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author libinzhou
 * @date 2024/12/26 9:56
 */
@SpringBootTest
public class WebTest {


    public void temp() throws MalformedURLException {
        // 方便跳转到路径处
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getForEntity("/monitor/health", null);
        restTemplate.getForEntity("/monitor/ask/{ask}", null);
        restTemplate.getForEntity("/monitor/askByParam", null);
    }

    @Test
    public void health() {
        ResponseEntity<String> response = WebUtils.request(
                "/monitor/health",
                null,
                HttpMethod.GET,
                String.class,
                null,
                null);
        WebUtils.showResponse(response);
    }

    @Test
    public void askByPathVariables() {
        ResponseEntity<String> response = WebUtils.request(
                "/monitor/ask/{ask}",
                null,
                HttpMethod.GET,
                String.class,
                null,
                123);
        WebUtils.showResponse(response);
    }

    @Test
    public void askByParam() {
        Map<String, Object> queryParams = new HashMap<>();
        queryParams.put("ask","hello");
        ResponseEntity<String> response = WebUtils.request(
                "/monitor/askByParam",
                queryParams,
                HttpMethod.GET,
                String.class,
                null,
                null);
        WebUtils.showResponse(response);
    }
}
