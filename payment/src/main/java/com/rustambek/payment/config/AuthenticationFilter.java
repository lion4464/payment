package com.rustambek.payment.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rustambek.payment.exception.ApiException;
import com.rustambek.payment.exception.ErrorResponse;
import com.rustambek.payment.model.user.User;
import com.rustambek.payment.service.user.UserService;
import com.rustambek.payment.utils.ErrorMessageUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@Component
@Slf4j
@RequiredArgsConstructor
public class AuthenticationFilter extends OncePerRequestFilter {

    private final UserService userService;

    private final TokenUtil tokenUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
        if (!request.getMethod().equalsIgnoreCase("OPTIONS")) {
            try {
                String headerName = "Authorization";
                final String authHeader = request.getHeader(headerName);
                if (authHeader != null && authHeader.startsWith("Bearer ")) {
                    String token = authHeader.substring(7);
                       String username = tokenUtil.getUsernameFromToken(token);
                        User userEntity = userService.findByUsername(username);

                        UserDetailsImpl userDetails = new UserDetailsImpl(userEntity);
                        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        SecurityContextHolder.getContext().setAuthentication(authentication);

                }
                chain.doFilter(request, response);
            } catch (ApiException apiException) {
                handleApiException(apiException, response, request);
            } catch (Exception exception) {
                handleException(exception, response, request);
            }

        }
    }
    private void handleApiException(ApiException exception, HttpServletResponse httpServletResponse, HttpServletRequest request) throws IOException {
        ErrorResponse errorResponse = new ErrorResponse(
                exception.getHttpStatus().value(),
                exception.getClass().getSimpleName(),
                request.getRequestURI(),
                exception.getLocalizedMessage(),
                exception.getData()
        );
        sendError(exception.getHttpStatus().value(), httpServletResponse, errorResponse);
    }
    private void handleException(Exception exception, HttpServletResponse response, HttpServletRequest request) throws IOException {
        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                exception.getClass().getSimpleName(),
                request.getRequestURI(),
                exception.getLocalizedMessage(),
                ErrorMessageUtil.getErrorMessage(exception)
        );
        sendError(HttpStatus.INTERNAL_SERVER_ERROR.value(), response, errorResponse);
    }

    private void sendError(int status, HttpServletResponse response, ErrorResponse errorResponse) throws IOException {
        if (response.isCommitted()) {
            logger.error("Cannot send error response - response already committed");
            return;
        }
        try {
            response.reset();
        } catch (IllegalStateException e) {
            logger.warn("Could not reset response - it may have already been committed");
        }
        response.setStatus(status);
        response.setContentType("application/json;charset=UTF-8");

        try (PrintWriter writer = response.getWriter()) {
            writer.write(new ObjectMapper().writeValueAsString(errorResponse));
            writer.flush();
        }
    }

}
