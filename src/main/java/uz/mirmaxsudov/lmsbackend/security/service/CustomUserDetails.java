package uz.mirmaxsudov.lmsbackend.security.service;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import uz.mirmaxsudov.lmsbackend.model.entity.auth.Permission;
import uz.mirmaxsudov.lmsbackend.model.entity.auth.Role;
import uz.mirmaxsudov.lmsbackend.model.entity.auth.User;
import uz.mirmaxsudov.lmsbackend.model.enums.auth.UserStatus;
import uz.mirmaxsudov.lmsbackend.service.base.RoleService;
import uz.mirmaxsudov.lmsbackend.service.base.UserService;

import java.util.*;

public record CustomUserDetails(User user, Set<Role> roles, Set<Permission> permissions) implements UserDetails {
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Set<GrantedAuthority> grantedAuthorities = new HashSet<>();

        roles.forEach(role -> {
            grantedAuthorities.add(
                    new SimpleGrantedAuthority("ROLE_" + role.getName().toUpperCase()));

        });

        permissions.forEach(permission -> {
            grantedAuthorities.add(
                    new SimpleGrantedAuthority("PERM_" + permission.getCode().toUpperCase()));
        });

        return grantedAuthorities;
    }

    public UUID getId() {
        return user.getId();
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getEmail();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return user.getStatus().equals(UserStatus.ACTIVE);
    }
}
