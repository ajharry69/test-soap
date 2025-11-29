package com.github.ajharry69.testsoap.bpm.dto;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
@EqualsAndHashCode
public class AdditionalData {
    private String key;
    private String value;
}