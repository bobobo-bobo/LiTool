package com.litool;

import com.alibaba.fastjson.JSON;
import com.baturu.commons.dtos.ResultDTO;
import com.baturu.reverse.api.enums.returnorder.ReturnOrderApplyReason;
import com.baturu.reverse.api.enums.returnorder.ReturnOrderAuditStates;
import com.baturu.reverse.common.constant.Constant;
import com.baturu.reverse.common.utils.Result;
import com.baturu.reverse.modules.returnorder.controller.ReturnOrderAuditSubmitVO;
import com.baturu.reverse.modules.returnorder.dto.ReturnOrderApplyDTO;
import com.baturu.reverse.modules.returnorder.vo.orderdetail.OrderAndDetailInfoVO;
import com.baturu.reverse.modules.returnorder.vo.orderdetail.OrderDetailInfoVO;
import com.baturu.reverse.modules.returnorder.vo.returndetail.OrderInfoVO;
import com.baturu.reverse.modules.security.dto.LoginDTO;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.*;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.net.MalformedURLException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author libinzhou
 * @date 2024/12/26 9:56
 */
@SpringBootTest
public class WebTest {


    private MultiValueMap<String, String> headers = new HttpHeaders();

    private RestTemplate restTemplate = new RestTemplate();

    /**
     * 0:自定义
     * 1:本地
     * 2：远程
     */
    private static final Integer urlType = 0;

    private static final String DEFAULT = "";
    private static final String REMOTE_URL_PREFIX = "http://docker33-as.qipeipu.net/reverse/";

    private static final String LOCAL_URL_PREFIX = "http://localhost:8869/reverse/";

    {
        headers.set(HttpHeaders.CONTENT_TYPE, "application/json");
    }

    public void temp() throws MalformedURLException {
        // 方便跳转到路径处
        restTemplate.getForEntity("/returnorder/asreturnorder/queryOrder/{orderNo}", null);
        restTemplate.getForEntity("/returnorder/asreturnorder/apply", null);
        restTemplate.getForEntity("/returnorder/asreturnorder/orgAudit", null);
        restTemplate.getForEntity("/returnorder/asreturnorder/supplierAudit", null);
        restTemplate.getForEntity("/returnorder/asreturnorder/holiday", null);
    }

    @Test
    public void auditReturnOrder() {
        this.loginAndSetToken();
        Map<String, String> params = Maps.newHashMap();
        ReturnOrderAuditSubmitVO returnOrderAuditSubmitVO = new ReturnOrderAuditSubmitVO();
        returnOrderAuditSubmitVO.setReturnId(1981064184l);
        returnOrderAuditSubmitVO.setStatus(ReturnOrderAuditStates.ALL.getValue());
        returnOrderAuditSubmitVO.setRemark("libinzhou 审核通过");
        ResponseEntity<String> response = request("/returnorder/asreturnorder/orgAudit",
                HttpMethod.GET,
                String.class,
                params,
                returnOrderAuditSubmitVO);
        this.showResponse(response);
    }

    @Test
    public void createReturnOrder() {
        this.loginAndSetToken();
        Map<String, String> params = Maps.newHashMap();

        ResponseEntity<Result> response = request("/returnorder/asreturnorder/queryOrder/O600170811",
                HttpMethod.GET,
                Result.class,
                params,
                null);
        this.showResponse(response);
        Result<OrderAndDetailInfoVO> result = response.getBody();
        OrderAndDetailInfoVO orderAndDetailInfoVO = JSON.parseObject(JSON.toJSONString(result.getData()), OrderAndDetailInfoVO.class);
        OrderInfoVO orderInfoVO = orderAndDetailInfoVO.getOrderInfoVO();
        OrderDetailInfoVO orderDetailInfoVO = orderAndDetailInfoVO.getOrderDetailInfoVOList().get(0);

        List<ReturnOrderApplyDTO> returnOrderApplyDTOs = Lists.newArrayList();
        ReturnOrderApplyDTO returnOrderApplyDTO = new ReturnOrderApplyDTO();
        returnOrderApplyDTO.setOrderId(orderInfoVO.getOrderId());
        returnOrderApplyDTO.setOrderNo(orderInfoVO.getOrderNo());
        returnOrderApplyDTO.setOrderDetailId(orderDetailInfoVO.getOrderDetailId());
        returnOrderApplyDTO.setReturnNum(1);
        returnOrderApplyDTO.setApplyReson(ReturnOrderApplyReason.NEEDLESS.getValue());
        returnOrderApplyDTO.setApplyRemark("libinzhou");
        returnOrderApplyDTO.setOuterPackingPic("https://cdn.pixabay.com/photo/2023/01/23/00/45/cat-7737618_640.jpg");
        returnOrderApplyDTO.setProductIdentifyPic("https://cdn.pixabay.com/photo/2023/01/23/00/45/cat-7737618_640.jpg");
        returnOrderApplyDTO.setProblemAreaPic("https://cdn.pixabay.com/photo/2023/01/23/00/45/cat-7737618_640.jpg");
        returnOrderApplyDTO.setUsed(0);
        returnOrderApplyDTO.setOuterPacket(1);
        returnOrderApplyDTO.setProductTag(1);
        returnOrderApplyDTOs.add(returnOrderApplyDTO);
        ResponseEntity<Result> response02 = request("/returnorder/asreturnorder/apply",
                HttpMethod.POST,
                Result.class,
                params,
                returnOrderApplyDTOs);
        this.showResponse(response02);

    }

    @Test
    public void loginAndSetToken() {
        Map<String, String> params = Maps.newHashMap();

        LoginDTO loginDTO = new LoginDTO();
        loginDTO.setUsername("admin");
        loginDTO.setPassword("admin");
        ResponseEntity<Result> request = request("/login",
                HttpMethod.POST,
                Result.class,
                params,
                loginDTO);
        this.showResponse(request);
        Map<String, String> tokenMap = (Map) request.getBody().getData();
        headers.set(Constant.TOKEN_HEADER, tokenMap.get(Constant.TOKEN_HEADER));
    }

    private <T> ResponseEntity<T> request(String url, HttpMethod httpMethod, Class<T> tClass, Map uriParam, Object body) {
        HttpEntity httpEntity = new HttpEntity(body, headers);
        ResponseEntity<T> responseEntity = restTemplate.exchange(this.getURL(url),
                httpMethod,
                httpEntity,
                tClass,
                uriParam,
                body);

        if (responseEntity.getStatusCode() == HttpStatus.OK) {
            System.out.println("请求成功！");
        } else {
            System.out.println("请求失败！");
        }
        return responseEntity;
    }

    private String getURL(String url) {
        if (urlType == 0) {
            return DEFAULT + url;
        } else if (urlType == 1) {
            return REMOTE_URL_PREFIX + url;
        } else {
            return LOCAL_URL_PREFIX + url;
        }
    }

    private <T> void showResponse(ResponseEntity<T> response) {
        if (Objects.isNull(response)) {
            System.out.println("响应不存在");
            return;
        }
        // 判断body类型
        T body = response.getBody();
        Object content = null;
        if (Objects.isNull(response.getBody())) {
            content = body;
        } else if (body instanceof ResultDTO) {
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
}
