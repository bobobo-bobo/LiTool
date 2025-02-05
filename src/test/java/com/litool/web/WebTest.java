package com.litool.web;


import com.litool.web.WebUtils;
import org.assertj.core.util.Maps;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;


import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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

    @Test
    public void deepseek() {

        Map<String, Object> body = new HashMap<>();
        body.put("model","deepseek-reasoner");
        body.put("messages",this.getMessages());
        body.put("stream",false);

        WebUtils.setToken("Authorization","Bearer sk-22c6b90c060a4aa9be7a2cba242e42d5");
        ResponseEntity<String> response = WebUtils.request(
                "https://api.deepseek.com/chat/completions",
                null,
                HttpMethod.POST,
                String.class,
                body,
                null);
        WebUtils.showResponse(response);
    }

    private List<Object> getMessages() {
        List<Object> messages = new ArrayList<>();
        Map<String, Object> content = new HashMap<>();
        content.put("role","user");
        content.put("content","你好");
        messages.add(content);
        return messages;
    }
}
