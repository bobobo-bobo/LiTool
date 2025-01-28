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
        Function<TestService, String> function = (service) -> {
            String hello = service.test("hello");
            return hello;
        };
        RPCUtils.request(function,TestService.class);
    }


}
