package wz_ling1991.base.framework.utils;

import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * 其它重试：spring retry，guava retry等
 */
@Slf4j
public enum RetryUtil {
    INSTANCE;

    private static ThreadLocal<Map<String, Integer>> retryTimesInThread = new ThreadLocal<>();
    private static final int DEFAULT_RETRY_TIMES = 5;
    private static final int DEFAULT_RETRY_WAITS = 1000;

    private static final String RETRY_TIMES_KEY = "retry_times_key";
    private static final String WAIT_TIME_KEY = "wait_time_key";

    private static final Map<String, String> baseTypesMap = new HashMap<String, String>() {
        {
            put("int", "java.lang.Integer");
            put("double", "java.lang.Double");
            put("long", "java.lang.Long");
            put("short", "java.lang.Short");
            put("byte", "java.lang.Byte");
            put("boolean", "java.lang.Boolean");
            put("char", "java.lang.Character");
            put("float", "java.lang.Float");
        }
    };


    /**
     * 重试执行当前方法
     */
    public Object retry(Object... args) {
        try {
            String targetClassName = Thread.currentThread().getStackTrace()[2].getClassName();
            String targetMethodName = Thread.currentThread().getStackTrace()[2].getMethodName();

            Class<?> clazz = Class.forName(targetClassName);
            Object targetObject = clazz.newInstance();
            Method targetMethod = getTargetMethod(clazz, targetMethodName, args);
            if (targetMethod == null) {
                return null;
            }
            targetMethod.setAccessible(true);
            retryWait();
            return targetMethod.invoke(targetObject, args);
        } catch (Exception e) {
            log.error("retry fail:{} ", e.getMessage());
            return null;
        }
    }

    private static void retryWait() throws InterruptedException {
        Map<String, Integer> map = retryTimesInThread.get() == null ? new HashMap<>() : retryTimesInThread.get();
        Integer currentRetryTimes = map.get(RETRY_TIMES_KEY);
        if (Objects.isNull(currentRetryTimes)) {
            currentRetryTimes = DEFAULT_RETRY_TIMES;
        }

        if (currentRetryTimes <= 0) {
            retryTimesInThread.remove();
            throw new RuntimeException("retry times use up error!");
        }
        log.info("remaining retry times: {}", currentRetryTimes);
        map.put(RETRY_TIMES_KEY, --currentRetryTimes);

        Integer currentRetryWaitMillis = map.get(WAIT_TIME_KEY);
        if (Objects.isNull(currentRetryWaitMillis)) {
            currentRetryWaitMillis = DEFAULT_RETRY_WAITS;
            map.put(WAIT_TIME_KEY, currentRetryWaitMillis);
        }
        retryTimesInThread.set(map);
        TimeUnit.MILLISECONDS.sleep(currentRetryWaitMillis);
    }

    private Method getTargetMethod(Class<?> clazz, String targetMethodName, Object... args) {
        Method[] methods = clazz.getDeclaredMethods();
        for (Method method : methods) {
            if (method.getName().equals(targetMethodName) && checkParameters(method, args)) {
                return method;
            }
        }
        return null;
    }

    private boolean checkParameters(Method method, Object... args) {
        Class<?>[] parameterTypes = method.getParameterTypes();
        int parameterCount = Objects.isNull(args) ? 0 : args.length;
        if (parameterTypes.length != parameterCount) {
            return false;
        }
        for (int i = 0; i < parameterCount; i++) {
            String typeName = parameterTypes[i].getName();
            String argTypeName = args[i].getClass().getName();
            if (typeName.equals(argTypeName)) {
                continue;
            }
            //判断是否基础类型
            if (baseTypesMap.containsKey(typeName) || baseTypesMap.containsKey(argTypeName)) {
                if (argTypeName.equals(baseTypesMap.get(typeName)) || typeName.equals(baseTypesMap.get(argTypeName))) {
                    continue;
                }
            }
            return false;
        }
        return true;
    }

    /**
     * 设置当前方法重试次数
     */
    public RetryUtil setRetryTimes(Integer retryTimes) {
        if (retryTimesInThread.get() == null) {
            Map<String, Integer> map = new HashMap<>();
            retryTimesInThread.set(map);
        }
        if (Objects.isNull(retryTimesInThread.get().get(RETRY_TIMES_KEY))) {
            retryTimesInThread.get().put(RETRY_TIMES_KEY, retryTimes);
        }
        return INSTANCE;
    }

    public RetryUtil setWaitMillis(Integer waitMillis) {
        if (retryTimesInThread.get() == null) {
            Map<String, Integer> map = new HashMap<>();
            retryTimesInThread.set(map);
        }
        if (Objects.isNull(retryTimesInThread.get().get(WAIT_TIME_KEY))) {
            retryTimesInThread.get().put(WAIT_TIME_KEY, waitMillis);
        }
        return INSTANCE;
    }
}
