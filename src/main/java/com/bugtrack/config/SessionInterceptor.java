package com.bugtrack.config;

import com.bugtrack.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.cors.CorsUtils;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class SessionInterceptor implements HandlerInterceptor {
  private final AuthService authService;

  public SessionInterceptor(AuthService authService) {
    this.authService = authService;
  }

  @Override
  public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
    String path = request.getRequestURI();
    if (CorsUtils.isPreFlightRequest(request) || path.equals("/api/auth/login") || path.startsWith("/h2-console")) {
      return true;
    }
    if (path.startsWith("/api/") && authService.current(request.getSession(false)) == null) {
      response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
      return false;
    }
    return true;
  }
}
