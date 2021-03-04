package wz_ling1991.base.framework.support;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DefaultCacheManager implements CacheManager {

    private static final Map<String, CacheObject> cacheMap = new ConcurrentHashMap<>(16);

    @Override
    public List<CacheObject> getAllValue() {
        return new ArrayList<>(cacheMap.values());
    }

    @Override
    public CacheObject getCache(String key) {
        return cacheMap.get(key);
    }

    @Override
    public void putCache(String key, CacheObject value) {
        cacheMap.put(key, value);
    }
}
