package wz_ling1991.base.framework.support;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.concurrent.TimeUnit;


/**
 * 缓存对象
 */
@Data
@AllArgsConstructor(staticName = "of")
public class CacheObject {

    private Object value;

    /**
     * 上次更新时间
     */
    private LocalDateTime lastUpdateTime;

    /**
     * 更新间隔
     */
    private Long updateInterval;

    /**
     * 更新间隔时间单位
     */
    private TimeUnit timeUnit;

    /**
     * 更新方法
     */
    private UpdateCacheFunction updateCacheFunction;

    public boolean isRequireUpdate() {
        if (Objects.isNull(updateCacheFunction)) {
            return false;
        }
        return lastUpdateTime.plusNanos(timeUnit.toNanos((updateInterval < 0) ? 0 : updateInterval)).isBefore(LocalDateTime.now());
    }

}
