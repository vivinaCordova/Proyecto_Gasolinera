package org.unl.gasolinera.base.controller.service;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import com.vaadin.collaborationengine.UserInfo;
import com.vaadin.hilla.BrowserCallable;

import io.micrometer.common.lang.NonNull;
import jakarta.annotation.security.PermitAll;

@BrowserCallable
public class UserInfoService {

    @PermitAll 
    public @NonNull UserInfo getUserInfo() {
        var auth = SecurityContextHolder.getContext().getAuthentication(); 
        var authorities = auth.getAuthorities()
            .stream()
            .map(GrantedAuthority::getAuthority)
            .toList();
        return new UserInfo(auth.getName(), authorities); 
    }

    public record UserInfo(
    @NonNull String name,
    @NonNull Collection<String> authorities
) {
}
}