package com.litool.rpc;


import org.apache.dubbo.config.ApplicationConfig;
import org.apache.dubbo.config.ReferenceConfig;
import org.apache.dubbo.config.RegistryConfig;
import org.springframework.beans.factory.annotation.Value;

import java.util.*;
import java.util.function.Function;

/**
 * @author libinzhou
 * @date 2024/12/26 9:56
 */
//@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
//@RunWith(SpringRunner.class)
public class RPCUtils {

    /**
     * 0:自定义
     * 1:本地
     * 2：远程
     */
    private static final Integer URL_TYPE = 2;
    private static final String DEFAULT_URL_PREFIX = "";
    private static final String LOCAL_ZOOKEEPER_URL_PREFIX = "";
    private static final String REMOTE_ZOOKEEPER_URL_PREFIX = "zookeeper://zk-0.zk-hs.docker34.svc.qipeipu.k8s.test:2181?backup=zk-1.zk-hs.docker34.svc.qipeipu.k8s.test:2181,zk-2.zk-hs.docker34.svc.qipeipu.k8s.test:2181";

    @Value("${dubbo.consumer.version}") // 需要开启SpringRunner才能注入
    private static String VERSION = "2.0.0";

    public static  <S> S register(Class interfaceClass){
        // 创建应用配置
        ApplicationConfig application = new ApplicationConfig();
        application.setName("dubbo-client");

        // 创建注册中心配置
        RegistryConfig registry = new RegistryConfig();
        registry.setAddress(getAddress());

        // 创建服务引用配置
        ReferenceConfig<S> reference = new ReferenceConfig<>();
        reference.setApplication(application);
        reference.setRegistry(registry);
        reference.setVersion(VERSION);
        reference.setRetries(0);
        reference.setInterface(interfaceClass);

        // 获取远程服务代理
        S serviceGet = reference.get();
        return serviceGet;
    }

    public static  <S,R> R request(Function<S,R> function,Class interfaceClass){
        // 创建应用配置
        ApplicationConfig application = new ApplicationConfig();
        application.setName("dubbo-client");

        // 创建注册中心配置
        RegistryConfig registry = new RegistryConfig();
        registry.setAddress(getAddress());

        // 创建服务引用配置
        ReferenceConfig<S> reference = new ReferenceConfig<>();
        reference.setApplication(application);
        reference.setRegistry(registry);
        reference.setVersion(VERSION);
        reference.setRetries(0);
        reference.setInterface(interfaceClass);

        // 获取远程服务代理
        S serviceGet = reference.get();
        R result = function.apply(serviceGet);
        showResult(result);
        return result;
    }

    private static void showResult(Object result) {
        if (Objects.isNull(result)) {
            System.out.println("响应不存在");
            return;
        }
        // 判断body类型
        Object content = null;
        if (Objects.isNull(result)) {
            content = result;
        } /*else if (result instanceof com.baturu.commons.dtos.ResultDTO) {
            com.baturu.commons.dtos.ResultDTO resultDTO = (com.baturu.commons.dtos.ResultDTO) result;
            content = resultDTO.getModel();
        } else if (result instanceof com.baturu.parts.dtos.ResultDTO){
            com.baturu.parts.dtos.ResultDTO resultDTO = (com.baturu.parts.dtos.ResultDTO) result;
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
            content = result;
        }

        // 展示
        if (Objects.isNull(content)) {
            System.out.println("返回内容为空");
        }  else if (content instanceof Map) {
            Map map = (Map) content;
            for (Object key : map.keySet()) {
                Object value = map.get(key);
                if (value instanceof Collection){
                    List list = (List) value;
                    System.out.println(key + " : ");
                    list.stream().forEach(System.out::println);
                } else {
                    System.out.println(key + " : " +value);
                }
            }
        } else if (content instanceof Collection) {
            Collection collection = (Collection) content;
            collection.stream().forEach(System.out::println);
        } else {
            System.out.println(content);
        }
    }

    private static String getAddress() {
        if (URL_TYPE == 0) {
            return DEFAULT_URL_PREFIX;
        } else if (URL_TYPE == 1) {
            return LOCAL_ZOOKEEPER_URL_PREFIX;
        } else if (URL_TYPE == 2){
            return REMOTE_ZOOKEEPER_URL_PREFIX;
        } else {
            System.out.println("未定义Address类型!");
            return null;
        }
    }

}
