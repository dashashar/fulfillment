package ru.itis.fulfillment.impl.security.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Component
@Slf4j
public class GoogleSheetFilter extends OncePerRequestFilter {

    @Value("${google.spreadsheet.secret}")
    private String validSecret;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String apiSecret = request.getHeader("X-API-SECRET");
        if (apiSecret == null || !apiSecret.equals(validSecret)) {
            log.error("An attempt to change the status of a shipment with an invalid secret key");
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid API Secret");
            return;
        }
        SecurityContextHolder.getContext().setAuthentication(
                new PreAuthenticatedAuthenticationToken(
                        "spreadsheet-service",
                        null,
                        Collections.emptyList()
                )
        );
        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getRequestURI();
        return !(path.matches("/api/v1/shipment/[^/]+/status") &&
                ("PATCH".equalsIgnoreCase(request.getMethod())));
    }
}
