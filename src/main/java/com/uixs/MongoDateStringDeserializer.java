package com.uixs;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;
import java.util.Map;

public class MongoDateStringDeserializer extends JsonDeserializer<String> {
    @Override
    public String deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        Map<String, String> node = p.readValueAs(Map.class);
        return node.get("$date");  // "$date" 필드의 문자열을 그대로 반환
    }
}
