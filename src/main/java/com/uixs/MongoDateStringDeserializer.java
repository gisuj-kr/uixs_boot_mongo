package com.uixs;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Map;

public class MongoDateStringDeserializer extends JsonDeserializer<String> {

    // 출력 형식: 화면과 엑셀에서 날짜를 yyyy-MM-dd 형태로 표시하기 위한 포맷터
    private static final DateTimeFormatter OUTPUT_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    // 한국 표준시 (UTC+9)
    private static final ZoneId KST = ZoneId.of("Asia/Seoul");

    @Override
    public String deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        Map<String, String> node = p.readValueAs(Map.class);
        String raw = node.get("$date");  // 예: "2026-04-16T15:00:00Z" (UTC 기준)

        if (raw == null || raw.isEmpty()) {
            return null;
        }

        try {
            // UTC 기준 ISO-8601 문자열을 한국 시간(KST)으로 변환한 뒤 날짜 문자열만 반환합니다.
            // 예: "2026-04-16T15:00:00Z"(UTC) → "2026-04-17"(KST)
            return OffsetDateTime.parse(raw, DateTimeFormatter.ISO_OFFSET_DATE_TIME)
                    .atZoneSameInstant(KST)
                    .format(OUTPUT_FORMATTER);
        } catch (Exception e) {
            // 이미 날짜 형식이거나 파싱할 수 없는 경우 원본 문자열 반환
            return raw;
        }
    }
}
