package com.crm.auth.enums;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum TokenType {

    BEARER("Bearer");

    @JsonValue
    private final String value;

}
