package com.litool.rpc;

import com.litool.demo.service.TestService;
import com.litool.utils.timedot.TimeRecordUtils;
import org.junit.jupiter.api.Test;

import java.util.function.Function;


/**
 * @author libinzhou
 * @date 2024/12/26 9:56
 */
public class RPCTest {

    @Test
    public void test() {
        Function<TestService, Void> function = (service) -> {
            service.doSomethingWithTiming();
            return null;
        };
        RPCUtils.request(function,TestService.class);
    }


}
