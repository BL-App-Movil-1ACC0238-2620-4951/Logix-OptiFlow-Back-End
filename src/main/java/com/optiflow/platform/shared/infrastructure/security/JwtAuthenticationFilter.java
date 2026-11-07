package com.optiflow.platform.shared.infrastructure.security;

import com.optiflow.platform.shared.domain.exceptions.DomainException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

  private final JwtTokenService jwtTokenService;

  public JwtAuthenticationFilter(JwtTokenService jwtTokenService) {
    this.jwtTokenService = jwtTokenService;
  }

  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {
    String authorization = request.getHeader("Authorization");
    if (authorization != null && authorization.startsWith("Bearer ")) {
      try {
        JwtClaims claims = jwtTokenService.parse(authorization.substring("Bearer ".length()));
        AuthenticatedUser user = new AuthenticatedUser(claims.userId(), claims.role());
        JwtAuthenticationToken authentication = new JwtAuthenticationToken(
            user,
            List.of(new SimpleGrantedAuthority("ROLE_" + claims.role())));
        SecurityContextHolder.getContext().setAuthentication(authentication);
      } catch (DomainException exception) {
        SecurityContextHolder.clearContext();
      }
    }
    filterChain.doFilter(request, response);
  }
}
