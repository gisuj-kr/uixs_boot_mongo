package com.uixs.util;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 다양한 ISO-8601 문자열(Z 접미사 포함 등)을 LocalDateTime으로 유연하게 변환하는 역직렬화기
 */
public class FlexibleLocalDateTimeDeserializer extends JsonDeserializer<LocalDateTime> {

    @Override
    public LocalDateTime deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        String date = p.getText();
        if (date == null || date.isEmpty()) {
            return null;
        }

        try {
            // 1. Z 접미사나 오프셋이 포함된 경우 (예: 2024-04-25T05:55:38.124Z)
            // UTC 시간을 시스템 표준인 한국 시간(Asia/Seoul)으로 보정한 뒤 LocalDateTime으로 변환합니다.
            return OffsetDateTime.parse(date, DateTimeFormatter.ISO_OFFSET_DATE_TIME)
                    .atZoneSameInstant(java.time.ZoneId.of("Asia/Seoul"))
                    .toLocalDateTime();
        } catch (Exception e) {
            try {
                // 2. T가 포함된 표준 ISO 형식 시도
                return LocalDateTime.parse(date);
            } catch (Exception e2) {
                try {
                    // 3. 공백이 포함된 형식 시도 (예: 2026-04-10 13:00:00)
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                    return LocalDateTime.parse(date, formatter);
                } catch (Exception e3) {
                    // 파싱 실패 시 null
                    return null;
                }
            }
        }
    }
}
