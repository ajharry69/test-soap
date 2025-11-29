package com.github.ajharry69.testsoap.bpm.common.headers;

import com.github.ajharry69.testsoap.bpm.common.headers.validators.HeaderValidator;
import lombok.*;
import org.springframework.util.StringUtils;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HeaderRule {
    private String headerName;
    @Builder.Default
    private boolean required = true;
    @Builder.Default
    private HeaderValidator validator = (headerName, headerValue) -> StringUtils.hasText(headerValue);

    public boolean isValid(String headerValue) {
        return validator.isValid(headerName, headerValue);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;

        HeaderRule that = (HeaderRule) o;
        return getHeaderName().equals(that.getHeaderName());
    }

    @Override
    public int hashCode() {
        return getHeaderName().hashCode();
    }
}