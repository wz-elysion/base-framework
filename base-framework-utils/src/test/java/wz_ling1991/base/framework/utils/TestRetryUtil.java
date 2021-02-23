package wz_ling1991.base.framework.utils;


import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

@Slf4j
public class TestRetryUtil {

    @Test
    public void test() {
        calculate();
        calculate(1, 0);
    }


    private Integer calculate() {
        try {
            return 1 / 0;
        } catch (Exception e) {
            log.error("calculate error");
            Object obj = RetryUtil.INSTANCE.retry();
            if (obj instanceof Integer) {
                return (Integer) obj;
            }
            return null;
        }
    }

    private Integer calculate(int a, int b) {
        try {
            return a / b;
        } catch (Exception e) {
            log.error("calculate error");
            Object obj = RetryUtil.INSTANCE.setWaitMillis(100).setRetryTimes(3).retry(a, b);
            if (obj instanceof Integer) {
                return (Integer) obj;
            }
            return null;
        }
    }
}
