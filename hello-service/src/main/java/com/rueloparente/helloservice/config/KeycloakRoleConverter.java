package com.rueloparente.helloservice.config;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

/**
 * This class is essential to correctly map Keycloak's realm roles to Spring Security's authorities.
 * By default, Spring Security looks for a "scope" or "scp" claim. Keycloak places roles in a different claim.
 */
public class KeycloakRoleConverter implements Converter<Jwt, AbstractAuthenticationToken> {

  @Override
  public AbstractAuthenticationToken convert(Jwt jwt) {
    Map<String, Object> realmAccess = jwt.getClaimAsMap("realm_access");
    Collection<GrantedAuthority> authorities = Collections.emptyList();

    if (realmAccess != null && realmAccess.get("roles") instanceof Collection<?> roles) {
      authorities = roles.stream()
                         .filter(String.class::isInstance)
                         .map(String.class::cast)
                         .map(roleName -> "ROLE_" + roleName)
                         .map(SimpleGrantedAuthority::new)
                         .collect(Collectors.toList());
    }

    return new JwtAuthenticationToken(jwt, authorities);
  }
}