package wz_ling1991.base.framework.utils;

import lombok.extern.slf4j.Slf4j;
import wz_ling1991.base.framework.support.CacheManager;
import wz_ling1991.base.framework.support.CacheObject;
import wz_ling1991.base.framework.support.DefaultCacheManager;
import wz_ling1991.base.framework.support.UpdateCacheFunction;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Slf4j
public final class CacheUtil {


    private static ExecutorService es = Executors.newFixedThreadPool(10);

    //这里可以替换成其它的CacheManager，也可以改造此类，让其成为spring bean。
    private static CacheManager cacheManager = new DefaultCacheManager();

    static {
        new Thread(CacheUtil::updateCache).start();
    }

    private static void updateCache() {
        while (true) {
            //TODO 考虑任务是否会把程序打崩？
            cacheManager.getAllValue().stream().filter(CacheObject::isRequireUpdate).forEach(cacheObject -> {
                es.submit(() -> {
                    Object newValue = cacheObject.getUpdateCacheFunction().updateCache();
                    cacheObject.setValue(newValue);
                    cacheObject.setLastUpdateTime(LocalDateTime.now());
                });
            });
        }
    }


    public static void cache(String key, Object value) {
        cache(key, value, null);
    }

    public static void cache(String key, Object value, UpdateCacheFunction function) {
        cache(key, value, null, 60L, TimeUnit.SECONDS);
    }

    public static void cache(String key, Object value, UpdateCacheFunction function, Long updateInterval, TimeUnit timeUnit) {
        CacheObject cacheObject = CacheObject.of(value, LocalDateTime.now(), updateInterval, timeUnit, function);
        cacheManager.putCache(key, cacheObject);
    }

    public static <T> T getCache(String key, Class<T> type) {
        CacheObject v = cacheManager.getCache(key);
        if (Objects.isNull(v) || Objects.isNull(v.getValue())) {
            return null;
        }
        return type.cast(v.getValue());
    }


}
