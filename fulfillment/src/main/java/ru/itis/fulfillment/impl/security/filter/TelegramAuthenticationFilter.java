package ru.itis.fulfillment.impl.security.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import ru.itis.fulfillment.api.dto.internal.response.AppErrorResponse;
import ru.itis.fulfillment.impl.exception.AuthenticationServiceException;
import ru.itis.fulfillment.impl.security.service.TelegramAuthenticationService;
import ru.itis.fulfillment.impl.security.token.TelegramAuthentication;

import java.io.IOException;

@RequiredArgsConstructor
@Component
public class TelegramAuthenticationFilter extends OncePerRequestFilter {

    private final TelegramAuthenticationService authService;
    private final ObjectMapper objectMapper;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {
        try {
            String initData = request.getHeader("Authorization");
            if (initData == null || initData.isBlank()) {
                filterChain.doFilter(request, response);
                return;
            }
            TelegramAuthentication authentication = authService.authenticate(initData);
            SecurityContextHolder.getContext().setAuthentication(authentication);
            filterChain.doFilter(request, response);
        } catch (AuthenticationServiceException e) {
            SecurityContextHolder.clearContext();

            AppErrorResponse errorResponse = new AppErrorResponse(
                    HttpStatus.UNAUTHORIZED,
                    "Authentication failed",
                    e.getMessage(),
                    request.getRequestURI());
            response.setContentType("application/json");
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
        }
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request){
        String path = request.getServletPath();
        return path.equals("/api/v1/account/register");
    }
}
