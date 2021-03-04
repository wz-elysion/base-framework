package wz_ling1991.base.framework.utils;

import org.junit.Test;
import wz_ling1991.base.framework.support.UpdateCacheFunction;

import java.util.Random;
import java.util.concurrent.TimeUnit;

public class TestCacheUtil {

    @Test
    public void test() throws InterruptedException {

        String testKey1 = "testKey1";
        UpdateCacheFunction f = () -> new Random().nextInt(100);
        CacheUtil.cache(testKey1, 1, f, 2L, TimeUnit.SECONDS);

        String testKey2 = "testKey2";
        CacheUtil.cache(testKey2, 1111);

        while (true) {
            Integer v1 = CacheUtil.getCache(testKey1, Integer.class);
            System.out.println("testKey1:" + v1);
            Integer v2 = CacheUtil.getCache(testKey2, Integer.class);
            System.out.println("testKey2:" + v2);
            Thread.sleep(1000);
        }
    }
}
