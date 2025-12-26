package com.uixs.util;

import java.util.Map;
import java.util.Optional;

public class MapUtils {

    /**
     * Map에서 지정된 키에 해당하는 값을 안전하게 가져옵니다.
     * 값이 null이거나 요청된 타입으로 캐스팅할 수 없는 경우 Optional.empty()를 반환합니다.
     *
     * @param map       값을 가져올 Map
     * @param key       가져올 값의 키
     * @param targetType 예상되는 값의 Class 타입
     * @param <T>       예상되는 값의 타입
     * @return 값이 존재하고 올바른 타입이면 Optional로 래핑된 값, 아니면 Optional.empty()
     */
    public static <T> Optional<T> getSafeValue(Map<String, Object> map, String key, Class<T> targetType) {
        if (map == null || !map.containsKey(key)) {
            return Optional.empty();
        }

        Object value = map.get(key);
        if (value == null) {
            return Optional.empty();
        }

        try {
            // Number 타입은 호환성 고려
            if (targetType.isInstance(value)) {
                return Optional.of(targetType.cast(value));
            } else if (Number.class.isAssignableFrom(targetType) && value instanceof Number) {
                // int, long, double 등 Number 타입 간의 변환 시도
                Number numValue = (Number) value;
                if (targetType == Integer.class || targetType == int.class) {
                    return Optional.of(targetType.cast(numValue.intValue()));
                } else if (targetType == Long.class || targetType == long.class) {
                    return Optional.of(targetType.cast(numValue.longValue()));
                } else if (targetType == Double.class || targetType == double.class) {
                    return Optional.of(targetType.cast(numValue.doubleValue()));
                }
                // 다른 Number 타입 필요시 추가
            } else if (targetType == String.class) {
                return Optional.of(targetType.cast(String.valueOf(value)));
            }

            return Optional.empty(); // 타입 불일치
        } catch (ClassCastException e) {
            // 캐스팅 예외 발생 시 (사실상 위의 isInstance 체크로 대부분 방지됨)
            return Optional.empty();
        }
    }

    /**
     * Map에서 String 값을 안전하게 가져옵니다.
     *
     * @param map 값을 가져올 Map
     * @param key 가져올 값의 키
     * @return 값이 존재하고 String 타입이면 Optional로 래핑된 String 값, 아니면 Optional.empty()
     */
    public static Optional<String> getString(Map<String, Object> map, String key) {
        return getSafeValue(map, key, String.class);
    }

    /**
     * Map에서 Integer 값을 안전하게 가져옵니다.
     *
     * @param map 값을 가져올 Map
     * @param key 가져올 값의 키
     * @return 값이 존재하고 Integer 타입이면 Optional로 래핑된 Integer 값, 아니면 Optional.empty()
     */
    public static Optional<Integer> getInteger(Map<String, Object> map, String key) {
        return getSafeValue(map, key, Integer.class);
    }

    /**
     * Map에서 Long 값을 안전하게 가져옵니다.
     *
     * @param map 값을 가져올 Map
     * @param key 가져올 값의 키
     * @return 값이 존재하고 Long 타입이면 Optional로 래핑된 Long 값, 아니면 Optional.empty()
     */
    public static Optional<Long> getLong(Map<String, Object> map, String key) {
        return getSafeValue(map, key, Long.class);
    }

    // 필요에 따라 다른 타입(Double, Boolean 등)에 대한 메서드를 추가할 수 있습니다.
}