package com.litool.demo.service;

import com.litool.utils.timedot.TimeRecordUtils;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.stereotype.Service;

/**
 * @author libinzhou
 * @date 2025/1/28 19:44
 */
@Service
@DubboService(interfaceName = "com.litool.demo.service.TestService",
        interfaceClass = TestService.class,
        version = "2.0.0")
public class TestServiceImpl implements TestService {


    @Override
    public String test(String arg) {
        TimeRecordUtils.init("测试任务");
        TimeRecordUtils.dot(() -> System.out.println(arg),"输出入参");
        return arg;
    }
}
