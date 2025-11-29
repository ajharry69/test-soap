package com.github.ajharry69.testsoap.bpm.common.headers;

import com.github.ajharry69.testsoap.bpm.common.headers.validators.HeaderValidator;
import lombok.*;

import java.util.Locale;
import java.util.Objects;

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
    private HeaderValidator validator = HeaderValidator.DEFAULT;

    public boolean isValid(String headerValue) {
        return validator.isValid(headerName, headerValue);
    }

    private String getCaseInsensitiveHeaderName() {
        String name = getHeaderName();
        if (name == null) return null;
        return name.strip().toLowerCase(Locale.ROOT);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;

        HeaderRule that = (HeaderRule) o;

        String name = getCaseInsensitiveHeaderName();
        if (name == null && that.getCaseInsensitiveHeaderName() == null) return true;
        if (name == null) return false;

        return name.equals(that.getCaseInsensitiveHeaderName());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getCaseInsensitiveHeaderName());
    }
}