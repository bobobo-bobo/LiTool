package com.litool.xxl;

import org.junit.jupiter.api.Test;

/**
 * @author libinzhou
 * @date 2025/1/16 9:15
 */
public class XXLTest {


    @Test
    public void jobRunTest() {
        XXLUtils.jobRun("trade-service-local",
                "orderFeedbackByTargetTimeJobs",
                "2024-12-31",
                "2024-12-31");
        XXLUtils.jobRun("","orderFeedbackByTargetTimeJobs");
        XXLUtils.jobRun("trade-service-local","");
        XXLUtils.jobRun("","");
    }

}
