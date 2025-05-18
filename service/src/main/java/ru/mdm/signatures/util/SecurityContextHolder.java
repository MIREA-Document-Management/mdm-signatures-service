package ru.mdm.signatures.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.oauth2.jwt.Jwt;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SecurityContextHolder {

    public static Mono<String> getUserLogin() {
        return ReactiveSecurityContextHolder.getContext()
                .map(SecurityContext::getAuthentication)
                .map(Authentication::getPrincipal)
                .ofType(Jwt.class)
                .map(jwt -> jwt.getClaim("sub").toString());
    }

    public static Flux<String> getUserRoles() {
        return ReactiveSecurityContextHolder.getContext()
                .map(SecurityContext::getAuthentication)
                .map(Authentication::getAuthorities)
                .flatMapMany(Flux::fromIterable)
                .filter(SimpleGrantedAuthority.class::isInstance)
                .map(GrantedAuthority::getAuthority);
    }

    public static Mono<Boolean> isAdmin() {
        return getUserRoles()
                .any("mdm-admin"::equals);
    }
}
