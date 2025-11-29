package com.github.ajharry69.testsoap.bpm.common;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Duration;
import java.time.Period;
import java.util.Date;

@Component
@Order(Ordered.LOWEST_PRECEDENCE - 1)
class KCBResponseFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        var startTimeStamp = request.getHeader("X-TimeStamp");
        if (startTimeStamp != null && !StringUtils.hasText(response.getHeader("X-ElapsedTime"))) {
            var elapsedTime = System.currentTimeMillis() - Long.parseLong(startTimeStamp) * 1_000;
            response.setHeader("X-ElapsedTime", String.valueOf(elapsedTime));
        }
        filterChain.doFilter(request, response);
    }
}
