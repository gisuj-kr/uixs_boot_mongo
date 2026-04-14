package com.uixs.util;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

/**
 * 다양한 ISO-8601 문자열(시간 정보 포함 등)을 LocalDate로 유연하게 변환하는 역직렬화기.
 * MongoDB에서 마이그레이션된 UTC 날짜 문자열을 한국 시간(KST, UTC+9) 기준으로 변환합니다.
 */
public class FlexibleLocalDateDeserializer extends JsonDeserializer<LocalDate> {

    // 한국 표준시 (UTC+9)
    private static final ZoneId KST = ZoneId.of("Asia/Seoul");

    @Override
    public LocalDate deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        String date = p.getText();
        if (date == null || date.isEmpty()) {
            return null;
        }

        try {
            // 1. yyyy-MM-dd 형식인 경우 바로 파싱 (이미 날짜만 있는 경우)
            if (date.length() == 10) {
                return LocalDate.parse(date);
            }

            // 2. 시간 정보나 타임존이 포함된 경우 (예: 2026-04-16T15:00:00.000Z)
            // UTC 기준 시각을 한국 시간(KST)으로 변환한 뒤 날짜만 추출합니다.
            // 예: "2026-04-16T15:00:00Z"(UTC) → KST 기준 2026-04-17
            return OffsetDateTime.parse(date, DateTimeFormatter.ISO_OFFSET_DATE_TIME)
                    .atZoneSameInstant(KST)
                    .toLocalDate();
        } catch (Exception e) {
            // 3. 최후의 수단: 단순히 앞의 10자만 잘라서 파싱 시도
            try {
                if (date.length() >= 10) {
                    return LocalDate.parse(date.substring(0, 10));
                }
            } catch (Exception e2) {
                // 파싱 실패 시 null 반환
            }
            return null;
        }
    }
}
