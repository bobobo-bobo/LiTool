package com.litool.demo.service;

import com.litool.utils.timedot.TimeRecordUtils;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
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


    private final Timer myTimer;

    @Autowired
    public TestServiceImpl(MeterRegistry meterRegistry) {
        this.myTimer = Timer.builder("my.test.service.timer")
                .description("A timer for my testService")
                .publishPercentileHistogram()
                .register(meterRegistry);
    }

    @Override
    public void doSomethingWithTiming() {
        myTimer.record(() -> {
            // Your business logic here
            try {
                Thread.sleep(100); // 模拟耗时操作
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
    }

    @Override
    public String test(String arg) {
        TimeRecordUtils.init("测试任务");
        TimeRecordUtils.dot(() -> System.out.println(arg),"输出入参");
        TimeRecordUtils.print();
        return arg;
    }
}
