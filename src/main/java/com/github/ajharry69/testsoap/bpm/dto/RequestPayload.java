package com.github.ajharry69.testsoap.bpm.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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
public class RequestPayload<T> implements Serializable {
    @Serial
    private static final long serialVersionUID = 7868310611900741033L;
    @NotNull(message = "Field messageID is required.")
    @NotBlank(message = "Field messageID cannot be blank.")
    @Size(min = 1, max = 32, message = "Field messageID must be between 1 and 32 characters long.")
    private String messageID;
    private @Valid T primaryData;
    @NotNull(message = "Field additionalData is required.")
    @Size(min = 1, message = "Field additionalData must have at least 1 item.")
    private List<AdditionalData> additionalData = new ArrayList<>();
}