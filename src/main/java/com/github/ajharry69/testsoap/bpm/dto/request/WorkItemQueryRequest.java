package com.github.ajharry69.testsoap.bpm.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/*
 * @project kcb-sfa-bpm-st
 * @author Chris Watia - KENCONT211390
 */
@Data
public class WorkItemQueryRequest {
    @NotNull(message = "ProcessInstanceId is required ")
    private String processInstanceId;
}
