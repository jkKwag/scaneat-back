package com.scaneat.back.client;

import java.util.Map;

public record GeminiFunctionCall(String name, Map<String, Object> args) {
}
