package com.github.ajharry69.testsoap.bpm.common.headers;

import com.github.ajharry69.testsoap.bpm.common.headers.validators.HeaderValidator;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

@Setter
@ConfigurationProperties(prefix = "kcb.api.headers.validation")
public class HeaderValidationProperties {
    private static final Set<HeaderRule> DEFAULT_HEADERS = Set.of(
            HeaderRule.builder().headerName("X-FeatureCode")
                    .required(false)
                    .build(),
            HeaderRule.builder().headerName("X-FeatureName")
                    .build(),
            HeaderRule.builder().headerName("X-ServiceCode")
                    .build(),
            HeaderRule.builder().headerName("X-ServiceName")
                    .build(),
            HeaderRule.builder().headerName("X-ServiceSubCategory")
                    .required(false)
                    .build(),
            HeaderRule.builder().headerName("X-MinorServiceVersion")
                    .validator(new HeaderValidator.Regex(Pattern.compile("v?\\d+(.\\d+){0,2}", Pattern.CASE_INSENSITIVE)))
                    .build(),
            HeaderRule.builder().headerName("X-ChannelCategory")
                    .build(),
            HeaderRule.builder().headerName("X-ChannelCode")
                    .build(),
            HeaderRule.builder().headerName("X-ChannelName")
                    .build(),
            HeaderRule.builder().headerName("X-RouteCode")
                    .required(false)
                    .build(),
            HeaderRule.builder().headerName("X-TimeStamp")
                    .required(false)
                    .validator(new HeaderValidator.EpochTimestamp())
                    .build(),
            HeaderRule.builder().headerName("X-ServiceMode")
                    .required(false)
                    .build(),
            HeaderRule.builder().headerName("X-SubscriberEvents")
                    .required(false)
                    .build(),
            HeaderRule.builder().headerName("X-CallBackURL")
                    .required(false)
                    .validator(new HeaderValidator.Regex("^https?://.+..+"))
                    .build()
    );
    private Set<HeaderRule> headers;

    public Set<HeaderRule> getHeaders() {
        if (headers == null) {
            headers = new HashSet<>();
        } else {
            headers = new HashSet<>(headers);
        }
        headers.addAll(DEFAULT_HEADERS);
        return headers;
    }
}