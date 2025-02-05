package com.litool.web;




import org.springframework.http.*;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author libinzhou
 * @date 2024/12/26 9:56
 */
public class WebUtils {

    /**
     * 0:自定义
     * 1:本地
     * 2：远程
     */
    private static final Integer URL_TYPE = 1;
    private static final String DEFAULT_URL_PREFIX = "";
    private static final String LOCAL_URL_PREFIX = "http://localhost:8080/";
    private static final String REMOTE_URL_PREFIX = "http://docker33-as.qipeipu.net/";

    private static RestTemplate restTemplate = new RestTemplate();


    /**
     * 请求头
     * */
    private static MultiValueMap<String, String> headers = new HttpHeaders();
    private static final String TOKEN_HEADER = "Token";
    private static final String TOKEN = "Token";
    {
        headers.set(HttpHeaders.CONTENT_TYPE, "application/json");
        headers.set(TOKEN_HEADER, TOKEN);
    }



    public static void setToken(String tokenHeader,String token) {
        headers.set(tokenHeader, token);
    }

    public static <T> ResponseEntity<T> request(String url,
                                                Map<String,Object> queryParams,
                                                HttpMethod httpMethod,
                                                Class<T> tClass,
                                                Object body,
                                                Object... pathVariables) {
        HttpEntity httpEntity = new HttpEntity(body, headers);
        url = addURLPrefix(url);
        url = addURLQueryParam(url,queryParams);
        ResponseEntity<T> responseEntity = restTemplate.exchange(url ,
                httpMethod,
                httpEntity,
                tClass,
                Objects.isNull(pathVariables)?0:pathVariables);

        if (responseEntity.getStatusCode() == HttpStatus.OK) {
            System.out.println("请求成功！");
        } else {
            System.out.println("请求失败！");
        }
        return responseEntity;
    }

    private static String addURLQueryParam(String url, Map<String,Object> queryParams) {
        if (Objects.isNull(queryParams) || queryParams.isEmpty()){
            return url;
        }
        UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder
                .fromHttpUrl(url);
        queryParams.forEach( (name,value) ->
            uriComponentsBuilder.queryParam(name,value)
        );
        String newURL = uriComponentsBuilder.build().toString();
        return newURL;
    }

    public static  <T> void showResponse(ResponseEntity<T> response) {
        if (Objects.isNull(response)) {
            System.out.println("响应不存在");
            return;
        }
        // 判断body类型
        T body = response.getBody();
        Object content = null;
        if (Objects.isNull(response.getBody())) {
            content = body;
        } /*else if (body instanceof ResultDTO) {
            ResultDTO resultDTO = (ResultDTO) body;
            content = resultDTO.getModel();
        } else if (body instanceof com.baturu.parts.dtos.ResultDTO) {
            com.baturu.parts.dtos.ResultDTO resultDTO = (com.baturu.parts.dtos.ResultDTO) body;
            if (resultDTO.isSuccess()) {
                content = resultDTO.getModel();
            } else {
                content = resultDTO.getMsg();
            }
        } else if (body instanceof Result) {
            Result result = (Result) body;
            if (result.success()) {
                content = result.getData();
            } else {
                content = result.getMsg();
            }
        } else if (body instanceof com.baturu.parts.dtos.ResultDTO){
            com.baturu.parts.dtos.ResultDTO resultDTO = (com.baturu.parts.dtos.ResultDTO) body;
            if (resultDTO.isSuccess()) {
                content = resultDTO.getModel();
                if (content instanceof PageModel){
                    PageModel pageModel = (PageModel)content;
                    System.out.println("page:" + pageModel.getPage());
                    System.out.println("pageSize:" + pageModel.getPageSize());
                    System.out.println("total:" + pageModel.getTotalCount());
                    content = pageModel.getData();
                } else {
                    content = resultDTO.getModel();
                }
            } else {
                content = resultDTO.getMsg();
            }
        }*/ else {
            content = body;
        }

        // 展示
        if (Objects.isNull(content)) {
            System.out.println("响应内容为空");
        } else if (content instanceof Map) {
            Map map = (Map) content;
            for (Object key : map.keySet()) {
                Object value = map.get(key);
                if (value instanceof Collection) {
                    List list = (List) value;
                    System.out.println(key + " : ");
                    list.stream().forEach(System.out::println);
                } else {
                    System.out.println(key + " : " + value);
                }
            }
        } else if (content instanceof Collection) {
            Collection collection = (Collection) content;
            collection.stream().forEach(System.out::println);
        } else {
            System.out.println(content);
        }
    }

    private static String addURLPrefix(String url) {
        if (URL_TYPE == 0) {
            return DEFAULT_URL_PREFIX + url;
        } else if (URL_TYPE == 1) {
            return LOCAL_URL_PREFIX + url;
        } else if (URL_TYPE == 2){
            return REMOTE_URL_PREFIX + url;
        } else {
            System.out.println("未定义URL前缀类型!");
            return null;
        }
    }
}
