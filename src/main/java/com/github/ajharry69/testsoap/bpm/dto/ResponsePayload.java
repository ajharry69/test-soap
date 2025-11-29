package com.github.ajharry69.testsoap.bpm.dto;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@ToString
@EqualsAndHashCode
public class ResponsePayload<T> implements Serializable {
    @Serial
    private static final long serialVersionUID = -7857624541088572239L;
    private String statusCode;
    private String statusDescription;
    private String messageCode;
    private String messageDescription;
    private List<ErrorInfo> errorInfo;
    private String messageID;
    private String conversationID;
    private List<AdditionalData> additionalData = new ArrayList<>();
    private T primaryData;

    @Setter
    @Getter
    @ToString
    @EqualsAndHashCode
    public static class ErrorInfo {
        private String errorCode;
        private String errorDescription;
    }
}
