package wz_ling1991.base.framework.support;

import java.util.List;

public interface CacheManager {

    List<CacheObject> getAllValue();

    CacheObject getCache(String key);

    void putCache(String key, CacheObject value);
}
