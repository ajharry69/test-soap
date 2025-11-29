package com.github.ajharry69.testsoap.bpm.common.headers.exceptions;

import com.github.ajharry69.testsoap.bpm.common.headers.HeaderRule;
import lombok.Getter;
import lombok.RequiredArgsConstructor;


@Getter
@RequiredArgsConstructor
public abstract class HeaderException extends Exception {
    private final HeaderRule rule;
}
