package com.litool.perform;

import com.litool.LiToolApplication;
import com.litool.demo.service.TestService;
import com.litool.rpc.RPCUtils;
import com.litool.utils.timedot.TimeRecordUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.StopWatch;

import java.util.function.Function;


/**
 * @author libinzhou
 * @date 2024/12/26 9:56
 */
@SpringBootTest
public class PerformTest {

    private static long totalTimes = 1000;

    @Autowired
    private TestService testService;

    @Test
    public void test() {
        StopWatch stopWatch = new StopWatch("性能测试");
        for (int i = 0; i < totalTimes; i++) {
            stopWatch.start("任务" + i);
            String hello = testService.test("hello");
            stopWatch.stop();
        }
        // 计算平均时间
        long totalTimeNanos = stopWatch.getTotalTimeNanos();
        long averageTimeNanos = totalTimeNanos / totalTimes;
        System.out.println(totalTimes + "次执行平均时间：" + averageTimeNanos + " ns");
        System.out.println(stopWatch.prettyPrint());
    }


}
