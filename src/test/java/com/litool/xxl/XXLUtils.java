package com.litool.xxl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;
import org.springframework.http.*;
import org.springframework.util.CollectionUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @author libinzhou
 * @date 2025/1/16 9:15
 */
public class XXLUtils {

    /**
     * 0:自定义
     * 1:本地
     * 2：远程
     */
    private static final Integer URL_TYPE = 2;

    private static final String DEFAULT = "";

    private static final String XXL_JOBS_LOCAL_URL_PREFIX = "";

    private static final String XXL_JOBS_REMOTE_URL_PREFIX = "https://docker34-xxl-job.qipeipu.net";
    private static final String USERNAME = "admin";
    private static final String PASSSWORD = "123456";

    private static MultiValueMap<String, String> headers = new HttpHeaders();

    {
        headers.set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE);
    }

    private static RestTemplate restTemplate = new RestTemplate();

    /**
     * @param targetJobGroupName
     * 根据 xxl-job-executor.properties 的 xxl.job.executor.appname 配置
     * 如果是新的任务组，需要先注册任务组
     * @see XXLUtils#JobGroupRegister()
     *
     * @param targetJobName
     * 想要执行的任务
     * 如果是新的任务，需要先注册
     * @see XXLUtils#jobRegister()
     */
    public static void jobRun(String targetJobGroupName,String targetJobName,String... jobParams) {
        if (StringUtils.isBlank(targetJobGroupName) || StringUtils.isBlank(targetJobName)){
            System.out.println("【执行任务】错误：任务组名和任务名不允许为空！");
            return;
        }
        login();
        // 发送 MediaType.APPLICATION_FORM_URLENCODED_VALUE 类型Body，需要 MultiValueMap 存储表单数据
        String targetJobID = findTargetJobID(targetJobGroupName,targetJobName);
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("id", targetJobID);
        updateJobParam(targetJobID,targetJobName,jobParams);
        ResponseEntity<String> response = request("/jobinfo/trigger",
                HttpMethod.POST,
                String.class,
                Maps.newHashMap(),
                body);
        showResponse(response);
    }

    /**
     * 如果是新的任务组，需要先注册任务组
     * @see XXLUtils#JobGroupRegister()
     */
    public static void jobRegister(String targetJobGroupName,String targetJobName) {
        if (StringUtils.isBlank(targetJobGroupName) || StringUtils.isBlank(targetJobName)){
            System.out.println("【注册任务】错误：任务组名和任务名不允许为空！");
            return;
        }
        login();
        // 发送 MediaType.APPLICATION_FORM_URLENCODED_VALUE 类型Body，需要 MultiValueMap 存储表单数据
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("jobGroup", findTargetJobGroupID(targetJobGroupName));
        body.add("jobDesc", "trade本地任务测试");
        body.add("executorRouteStrategy", "FIRST");
        body.add("jobCron", "* * * * * ? 2040-2041");
        body.add("glueType", "BEAN");
        body.add("executorHandler", targetJobName);
        body.add("executorParam", "");
        body.add("childJobId", "");
        body.add("executorBlockStrategy", "SERIAL_EXECUTION");
        body.add("executorFailStrategy", "FAIL_ALARM");
        body.add("author", "libinzhou");
        body.add("alarmEmail", "");
        body.add("glueRemark", "GLUE代码初始化");
        body.add("glueSource", "");
        ResponseEntity<String> response = request("/jobinfo/add",
                HttpMethod.POST,
                String.class,
                Maps.newHashMap(),
                body);
        showResponse(response);
    }

    /**
     * 为你的任务组起个名字吧，
     * 注意要跟本地启动的服务命名的任务组名字一样
     */
    public static void jobGroupRegister(String targetJobGroupName) {
        if (StringUtils.isBlank(targetJobGroupName)){
            System.out.println("【注册任务组】错误：任务组名不允许为空！");
            return;
        }
        login();
        // 发送 MediaType.APPLICATION_FORM_URLENCODED_VALUE 类型Body，需要 MultiValueMap 存储表单数据
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("appName", targetJobGroupName);
        body.add("title", targetJobGroupName);
        body.add("order", "1");
        body.add("addressType", "0"); // 自动注册
        body.add("addressList:", "");
        ResponseEntity<String> response = request("/jobgroup/save",
                HttpMethod.POST,
                String.class,
                Maps.newHashMap(),
                body);
        showResponse(response);
    }

    private static void updateJobParam(String targetJobID,String targetJobName, String... params) {
        // 发送 MediaType.APPLICATION_FORM_URLENCODED_VALUE 类型Body，需要 MultiValueMap 存储表单数据
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("id", targetJobID);
        body.add("jobDesc", "trade本地任务测试");
        body.add("executorRouteStrategy", "FIRST");
        body.add("jobCron", "* * * * * ? 2040-2041");
        body.add("glueType", "BEAN");
        body.add("executorHandler", targetJobName);
        body.add("executorParam", Objects.isNull(params) ? null
                : Arrays.stream(params).collect(Collectors.joining(","))); // 参数
        body.add("childJobId", "");
        body.add("executorBlockStrategy", "SERIAL_EXECUTION");
        body.add("executorFailStrategy", "FAIL_ALARM");
        body.add("author", "libinzhou");
        body.add("alarmEmail", "");
        ResponseEntity<String> response = request("/jobinfo/update",
                HttpMethod.POST,
                String.class,
                Maps.newHashMap(),
                body);
    }

    private static void login() {
        // 发送 MediaType.APPLICATION_FORM_URLENCODED_VALUE 类型Body，需要 MultiValueMap 存储表单数据
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("userName", USERNAME);
        body.add("password", PASSSWORD);

        ResponseEntity<String> response = request("/login",
                HttpMethod.POST,
                String.class,
                Maps.newHashMap(),
                body);
        setCookie(response);
    }

    private static String findTargetJobID(String targetJobGroupName,String targetJobName) {
        if (StringUtils.isBlank(targetJobGroupName) || StringUtils.isBlank(targetJobName)){
            System.out.println("【查询任务】错误：任务组名和任务名不允许为空！");
            throw new RuntimeException();
        }
        // 发送 MediaType.APPLICATION_FORM_URLENCODED_VALUE 类型Body，需要 MultiValueMap 存储表单数据
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("jobGroup", findTargetJobGroupID(targetJobGroupName));
        body.add("executorHandler", targetJobName);
        body.add("jobDesc", "");
        body.add("start", "0");
        body.add("length", "10");
        ResponseEntity<String> response = request("/jobinfo/pageList",
                HttpMethod.POST,
                String.class,
                Maps.newHashMap(),
                body);
        String responseBody = response.getBody();
        String targetJobID = getTargetJobID(responseBody);
        return targetJobID;
    }

    private static String getTargetJobID(String allJobsBody) {
        if (StringUtils.isEmpty(allJobsBody)) {
            throw new RuntimeException("任务列表响应为空");
        }
        Map<String, Object> result = JSON.parseObject(allJobsBody, HashMap.class);
        JSONArray allJobList = (JSONArray) result.get("data");
        if (CollectionUtils.isEmpty(allJobList)) {
            throw new RuntimeException("任务列表为空");
        }
        Map<String, Object> firstData = (Map) allJobList.get(0);
        Integer id = (Integer) firstData.get("id");
        System.out.println("targetJobID：" + id);
        return id.toString();
    }

    private static String findTargetJobGroupID(String targetJobGroupName) {
        if (StringUtils.isBlank(targetJobGroupName)){
            System.out.println("【查询任务组】错误：任务组名不允许为空！");
            throw new RuntimeException();
        }
        // 发送 MediaType.APPLICATION_FORM_URLENCODED_VALUE 类型Body，需要 MultiValueMap 存储表单数据
        ResponseEntity<String> response = request("/jobgroup",
                HttpMethod.GET,
                String.class,
                Maps.newHashMap(),
                null);
        String responseBody = response.getBody();

        String targetJobGroupID = getTargetJobGroupID(targetJobGroupName, responseBody);
        return targetJobGroupID;
    }

    private static String getTargetJobGroupID(String targetJobGroupName, String allJobGroupName) {
        if (StringUtils.isEmpty(allJobGroupName)) {
            throw new RuntimeException("任务分组列表为空");
        }
        if (StringUtils.isEmpty(targetJobGroupName)) {
            throw new RuntimeException("任务分组不允许为空");
        }
        //(?s) 表示开启 DOTALL 模式，实现.*?可以匹配所有字符（包括换行符）
        Pattern pattern = Pattern.compile("appName=\"([^\"]+)\"(?s).*?id=\"([^\"]+)\"");
        Matcher matcher = pattern.matcher(allJobGroupName);
        while (matcher.find()) {
            String appName = matcher.group(1);
            String targetJobGroupID = matcher.group(2);
            if (targetJobGroupName.equals(appName)) {
                System.out.println("targetJobGroupID：" + targetJobGroupID);
                return targetJobGroupID;
            }
        }
        throw new RuntimeException("目标任务分组不存在");
    }

    private static  <T> ResponseEntity<T> request(String url, HttpMethod httpMethod, Class<T> tClass, Map uriParam, Object body) {
        HttpEntity httpEntity = new HttpEntity(body, headers);
        ResponseEntity<T> responseEntity = restTemplate.exchange(getURL(url),
                httpMethod,
                httpEntity,
                tClass,
                uriParam);

        if (responseEntity.getStatusCode() == HttpStatus.OK) {
            System.out.println(url + " 请求成功！");
        } else {
            System.out.println("请求失败！");
        }
        return responseEntity;
    }

    private static String getURL(String url) {
        if (URL_TYPE == 0) {
            return DEFAULT + url;
        } else if (URL_TYPE == 1) {
            return XXL_JOBS_LOCAL_URL_PREFIX + url;
        } else {
            return XXL_JOBS_REMOTE_URL_PREFIX + url;
        }
    }

    private static <T> void showResponse(ResponseEntity<T> response) {
        if (Objects.isNull(response)) {
            System.out.println("响应不存在");
            return;
        }
        // 判断body类型
        T body = response.getBody();
        Object content = null;
        if (Objects.isNull(response.getBody())) {
            content = body;
        } else {
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

    private static void setCookie(ResponseEntity<String> response) {
        if (response.getStatusCode() != HttpStatus.OK) {
            System.out.println("设置cookie失败");
            return;
        }
        HttpHeaders responseHeaders = response.getHeaders();
        List<String> cookies = responseHeaders.get("Set-Cookie");
        if (Objects.isNull(cookies) || cookies.isEmpty()) {
            System.out.println("设置cookie失败: Set-Cookie为空");
        }
        String XXL_JOB_LOGIN_IDENTITY = cookies.get(0);
        if (StringUtils.isEmpty(XXL_JOB_LOGIN_IDENTITY)) {
            System.out.println("设置cookie失败: XXL_JOB_LOGIN_IDENTITY为空");
        }
        headers.set(HttpHeaders.COOKIE, XXL_JOB_LOGIN_IDENTITY);
        System.out.println("设置cookie成功：" + XXL_JOB_LOGIN_IDENTITY);
    }
}
